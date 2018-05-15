package fish.payara.demos.conference.speaker.api;

import fish.payara.demos.conference.speaker.entitites.Venue;
import fish.payara.demos.conference.speaker.services.VenueService;
import java.util.Collection;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Fabio Turizo
 */
@Path("/venue")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class VenueResource {
    
    @Inject
    VenueService venueService;
    
    @GET
    @Path("/all")
    public Collection<Venue> all(){
        return venueService.getAllVenues();
    }
}
