package fish.payara.demos.conference.speaker.services;

import fish.payara.demos.conference.speaker.entitites.Speaker;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author fabio
 */
@ApplicationScoped
public class SpeakerService {
    
    @PersistenceContext(unitName = "Speaker")
    EntityManager em;
    
    @Transactional
    public Speaker save(Speaker speaker){
        em.persist(speaker);
        return speaker;
    }
    
    public Optional<Speaker> get(Integer id){
        return Optional.ofNullable(em.find(Speaker.class, id));
    } 
}
