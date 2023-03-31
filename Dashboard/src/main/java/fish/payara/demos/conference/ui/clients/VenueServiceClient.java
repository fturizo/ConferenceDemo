package fish.payara.demos.conference.ui.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient
@Path("/venue")
public interface VenueServiceClient {

    @GET
    @Path("/all")
    List<String> getVenues();
}
