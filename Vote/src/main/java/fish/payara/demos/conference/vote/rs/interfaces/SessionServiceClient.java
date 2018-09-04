package fish.payara.demos.conference.vote.rs.interfaces;

import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    JsonObject get(@PathParam("id") Integer id);
}
