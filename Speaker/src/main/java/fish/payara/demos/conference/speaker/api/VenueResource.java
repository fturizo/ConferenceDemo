package fish.payara.demos.conference.speaker.api;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author Fabio Turizo
 */
@Path("/venue")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class VenueResource {
    
    @Inject
    @ConfigProperty(name = "demo.conference.speaker.venues", defaultValue = "Ocarina")
    private List<String> venues;
    
    @GET
    @Path("/all")
    public List<String> all(){
        return venues;
    }
}