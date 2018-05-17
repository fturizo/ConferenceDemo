package fish.payara.demos.conference.session.services;

import fish.payara.demos.conference.session.entities.Schedule;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class ScheduleService {
    
    @PersistenceContext(unitName = "Session")
    EntityManager em;
    
    @Transactional
    public Schedule save(Schedule schedule){
        //TODO - Verify venues
        em.persist(schedule);
        em.flush();
        return schedule;
    }
    
    public Optional<Schedule> getSchedule(Integer id){
        return Optional.ofNullable(em.find(Schedule.class, id));
    }
    
    public List<Schedule> getAllSchedules(){
        return em.createNamedQuery("Schedule.getAll", Schedule.class).getResultList();
    }
}
