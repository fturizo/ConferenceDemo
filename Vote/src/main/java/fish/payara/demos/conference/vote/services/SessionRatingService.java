package fish.payara.demos.conference.vote.services;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.SessionRating;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.cache.Cache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
    SessionDataService dataService;
    
    @Inject
    Cache<String, List<SessionRating>> cachedRatings;

    @Transactional
    @Retry(maxRetries = 3, delay = 1, delayUnit = ChronoUnit.MINUTES)
    @Fallback(fallbackMethod = "cacheRating")
    public SessionRating addRating(SessionRating rating, Attendee attendee) {
        rating = rating.with(attendee, dataService.getSessionSummary(rating.getSessionId()));
        em.persist(rating);
        em.flush();
        return rating;
    }
    
    public SessionRating cacheRating(SessionRating rating, Attendee attendee){
        LOG.info("Caching attendee session rating");
        rating = rating.with(attendee);
        cachedRatings.putIfAbsent(rating.getSessionId(), new ArrayList<>());
        cachedRatings.get(rating.getSessionId()).add(rating);
        return rating;
    }
    
    @Timeout(value = 1, unit = MINUTES)
    public List<SessionRating> getRatingsFor(String sessionId){
        isSlow(11);
        var results = em.createNamedQuery("SessionRating.getForSession", SessionRating.class)
                                        .setParameter("id", sessionId)
                                        .getResultList();
        if(cachedRatings.containsKey(sessionId)){
            results.addAll(cachedRatings.get(sessionId));
        }
        return results;
    }
    
    private boolean isSlow(int secondsToSleep){
        if (Math.random() > 0.4) {
            LOG.log(Level.INFO, "Sleeping {0}s", secondsToSleep);
            try {
                Thread.sleep(secondsToSleep * 1_000);
            } catch (InterruptedException ex) {}
            return true;
        }
        return false;
    }
}
