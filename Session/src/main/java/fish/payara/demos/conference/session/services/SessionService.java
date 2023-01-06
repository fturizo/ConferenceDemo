package fish.payara.demos.conference.session.services;

import fish.payara.demos.conference.session.entities.Session;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class SessionService {
    
    @PersistenceContext(unitName = "Session")
    EntityManager em;
    
    @Inject
    SpeakerDomainChecker domainChecker;
    
    AtomicInteger sessionSpaces;
    
    @PostConstruct
    public void init(){
        sessionSpaces = new AtomicInteger(5);
    }
            
    @Gauge(name = "session.spaces", absolute = true, unit = MetricUnits.NONE)
    public Integer sessionSpaces(){
        return sessionSpaces.get();
    }
    
    @Transactional
    @Timed(name = "session.creation.time", absolute = true)
    public Session register(Session session){
        if(!domainChecker.checkSpeakers(session) || !domainChecker.checkVenue(session)){
            throw new IllegalArgumentException("Invalid venue or speaker names");
        }else if(sessionSpaces.get() == 0){
            throw new IllegalStateException("No more session spaces");
        }
        em.persist(session);
        em.flush();
        sessionSpaces.decrementAndGet();
        return session;
    }
    
    @Transactional
    public void delete(Session session){
        em.remove(em.find(Session.class, session.getId()));
        sessionSpaces.incrementAndGet();
        em.flush();
    }
    
    public Optional<Session> retrieve(Integer id){
        return Optional.ofNullable(em.find(Session.class, id));
    }
    
    public List<Session> retrieve(LocalDate date){
        return em.createNamedQuery("Session.getForDay", Session.class).setParameter("date", date).getResultList();
    }
}
