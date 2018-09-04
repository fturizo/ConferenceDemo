package fish.payara.demos.conference.session.rs.clients;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient
@Path("/venue")
public interface VenueServiceClient {
    
    @HEAD
    @Path("/check/{name}")
    Response checkVenue(@PathParam("name") String name);
}
