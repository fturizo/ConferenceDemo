package fish.payara.demos.conference.ui.clients;

import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient
@Path("/venue")
public interface VenueServiceClient {
    
    @HEAD
    @Path("/{name}")
    Response checkVenue(@PathParam("name") String name);
}
