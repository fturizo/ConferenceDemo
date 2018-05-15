package fish.payara.demos.conference.schedule.services;

import fish.payara.demos.conference.schedule.entities.Schedule;
import java.util.List;
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
    
    @PersistenceContext
    private EntityManager em;
    
    @Transactional
    public Schedule save(Schedule schedule){
        em.persist(schedule);
        em.flush();
        return schedule;
    }
    
    public List<Schedule> getAllSchedules(){
        return em.createNamedQuery("Schedule.getAll", Schedule.class).getResultList();
    }
}
