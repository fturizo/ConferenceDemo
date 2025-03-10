package fish.payara.demos.conference.vote.rs.interfaces;

import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author fabio
 */
@RegisterRestClient
//Required to auto-propagate client headers  - NOT Working in the current build
@RegisterClientHeaders
@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public interface SessionServiceClient {

    @GET
    @Path("/{id}")
    JsonObject get(@HeaderParam("Authorization") String authorization, @PathParam("id") String id);
}
