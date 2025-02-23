package fish.payara.demos.conference.vote.services;

import fish.payara.demos.conference.vote.entities.Attendee;

import java.util.List;
import java.util.Optional;
import javax.cache.Cache;
import javax.cache.annotation.CacheDefaults;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
@CacheDefaults(cacheName = "attendees")
public class AttendeeService {

    @PersistenceContext(unitName = "Vote")
    EntityManager em;

    @Inject
    Cache<String, Attendee> attendees;

    @Transactional
    public Attendee create(Attendee attendee) {
        em.persist(attendee);
        em.flush();
        attendees.put(attendee.getEmail(), attendee);
        return attendee;
    }

    public Optional<Attendee> getAttendee(Integer id) {
        return Optional.ofNullable(em.find(Attendee.class, id));
    }

    public Optional<Attendee> getByEmail(String email) {
        if(attendees.containsKey(email)){
            return Optional.of(attendees.get(email));
        }else{
            Optional<Attendee> result = em.createNamedQuery("Attendee.fromEmail", Attendee.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst();
            result.ifPresent(attendee -> attendees.put(email, attendee));
            return result;
        }
    }

    public List<Attendee> getAllAttendees() {
        return em.createNamedQuery("Attendee.getAll", Attendee.class).getResultList();
    }
}
