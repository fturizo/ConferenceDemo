package fish.payara.demos.conference.speaker.services;

import fish.payara.demos.conference.speaker.entitites.Speaker;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 *
 * @author fabio
 */
@ApplicationScoped
public class SpeakerService {
    
    private static final Logger LOG = Logger.getLogger(SpeakerService.class.getName());
    
    @PersistenceContext(unitName = "Speaker")
    EntityManager em;
    
    @Transactional
    public Speaker save(Speaker speaker){
        em.persist(speaker);
        return speaker;
    }
    
    public Speaker get(String id){
        LOG.info("Retrieving speaker from database");
        return em.find(Speaker.class, id);
    }

    public List<Speaker> all(){
        return em.createNamedQuery("Speaker.all", Speaker.class).getResultList();
    }
    
    public boolean allNamesExists(List<String> names){
        var allNames = em.createNamedQuery("Speaker.all", Speaker.class).getResultStream()
                                  .map(Speaker::getName).collect(Collectors.toList());
        return names.stream().allMatch(allNames::contains);
    }
}
