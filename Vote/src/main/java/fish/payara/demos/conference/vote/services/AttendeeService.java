package fish.payara.demos.conference.vote.services;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.Credentials;
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
public class AttendeeService {

    @PersistenceContext(unitName = "Vote")
    EntityManager em;

    @Transactional
    public Attendee create(Attendee attendee) {
        em.persist(attendee);
        em.flush();
        return attendee;
    }

    public Optional<Attendee> getByEmail(String email) {
        return em.createNamedQuery("Attendee.fromEmail", Attendee.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }
    
    public Optional<Attendee> getById(Integer id) {
        return Optional.ofNullable(em.find(Attendee.class, id));
    }

    public List<Attendee> getAllAttendees() {
        return em.createNamedQuery("Attendee.getAll", Attendee.class).getResultList();
    }

    public Optional<Attendee> verify(Credentials credentials) {
        return em.createNamedQuery("Attendee.checkCredentials", Attendee.class)
                .setParameter("email", credentials.getEmail())
                .setParameter("password", credentials.getPassword())
                .getResultStream()
                .findFirst();
    }
}
