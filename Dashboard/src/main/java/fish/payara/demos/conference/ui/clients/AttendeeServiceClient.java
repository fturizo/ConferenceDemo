package fish.payara.demos.conference.ui.clients;

import fish.payara.demos.conference.ui.entities.Attendee;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient(configKey = "vote")
@Path("/attendee")
public interface AttendeeServiceClient {

    @POST
    Response register(Attendee attendee);

    @GET
    @Path("/all")
    List<Attendee> getAll();

    @GET
    Response getAttendee(@QueryParam("email") String email);
}
