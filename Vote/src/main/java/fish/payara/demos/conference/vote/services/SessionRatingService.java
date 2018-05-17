package fish.payara.demos.conference.vote.services;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.SessionRating;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class SessionRatingService {

    private static final Logger LOG = Logger.getLogger(SessionRatingService.class.getName());
    
    @PersistenceContext(unitName = "Vote")
    EntityManager em;  
    
    @Inject
    Cache<Integer, List<SessionRating>> cachedRatings;

    @Retry(maxRetries = 1)
    @Fallback(fallbackMethod = "cacheRating")
    @Transactional
    public SessionRating addRating(SessionRating rating, Attendee attendee) {
        rating = rating.with(attendee);
        em.persist(rating);
        em.flush();
        return rating;
    }
    
    private SessionRating cacheRating(SessionRating rating, Attendee attendee){
        rating = rating.with(attendee);
        cachedRatings.putIfAbsent(rating.getSessionId(), new ArrayList<>());
        cachedRatings.get(rating.getSessionId()).add(rating);
        return rating;
    }
    
    @Timeout(value = 10, unit = SECONDS)
    public List<SessionRating> getRatingsFor(Integer sessionId){
        isSlow(11);
        List<SessionRating> results = em.createNamedQuery("SessionRating.getForSession", SessionRating.class)
                                        .setParameter("id", sessionId)
                                        .getResultList();
        if(cachedRatings.containsKey(sessionId)){
            results.addAll(cachedRatings.get(sessionId));
        }
        return results;
    }
    
    private boolean isSlow(int secondsToSleep){
        if (Math.random() > 0.4) {
            LOG.info("Sleeping " + secondsToSleep + "s");
            try {
                Thread.sleep(secondsToSleep * 1_000);
            } catch (InterruptedException ex) {}
            return true;
        }
        return false;
    }
}
