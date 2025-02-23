package fish.payara.demos.conference.ui.clients;

import fish.payara.demos.conference.ui.entities.SessionRating;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "vote")
@Path("/rating")
public interface SessionVoteServiceClient {

    @POST
    @Path("/{attendeeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response rate(@PathParam("attendeeId") String attendeeId, SessionRating rating);

    @GET
    @Path("/session/{session}")
    List<SessionRating> getRatingsForSession(@PathParam("session") String sessionId);

    @GET
    @Path("/{attendee}")
    List<SessionRating> getRatingsByAttendee(@PathParam("attendee") String attendeeId);

    @GET
    @Path("/summary/{session}")
    Response getSummaryForSession(@PathParam("session") String sessionId);
}
