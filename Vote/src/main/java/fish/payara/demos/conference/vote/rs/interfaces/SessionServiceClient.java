package fish.payara.demos.conference.vote.rs.interfaces;

import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author fabio
 */
@RegisterRestClient
@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public interface SessionServiceClient {
    
    @GET
    @Path("/{id}")
    JsonObject get(@PathParam("id") String id);
}
