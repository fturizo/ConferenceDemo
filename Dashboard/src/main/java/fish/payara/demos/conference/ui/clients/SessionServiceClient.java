
package fish.payara.demos.conference.ui.clients;

import fish.payara.demos.conference.ui.entities.Session;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient(configKey = "session")
@Path("/session")
@ClientHeaderParam(name = "Authorization", value = "{fish.payara.demos.conference.ui.controllers.AuthController.buildAuthHeader}")
public interface SessionServiceClient {

    @POST
    public Response create(Session session);

    @GET
    @Path("/{id}")
    Response get(@PathParam("id") String id);

    @DELETE
    @Path("/{id}")
    Response delete(@PathParam("id") String id);

    @GET
    List<Session> getSessions();
}
