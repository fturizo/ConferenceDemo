package fish.payara.demos.conference.speaker.services;

import fish.payara.demos.conference.speaker.entitites.Venue;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.annotation.CacheDefaults;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
@CacheDefaults(cacheName = "venues")
public class VenueService {
    
    private int venueCounter = 0;
    
    @Inject
    private Cache<Integer, Venue> venues;
    
    @Inject
    @ConfigProperty(name = "demo.conference.speaker.venues", defaultValue = "Default Venue")
    private List<String> venueNames;
    
    @PostConstruct
    public void init(){
        this.venues.putAll(venueNames.stream().map(this::generate).collect(Collectors.toMap(Venue::getId, Function.identity())));
    }
    
    private Venue generate(String name){
        return new Venue(venueCounter++, name);
    }
    
    public List<Venue> getAllVenues(){
        return IntStream.range(0, venueCounter).mapToObj(venues::get).collect(Collectors.toList());
    }
}
