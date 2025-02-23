package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.SessionRating;
import fish.payara.demos.conference.vote.services.AttendeeService;
import fish.payara.demos.conference.vote.services.SessionRatingService;
import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Fabio Turizo
 */
@Path("/rating")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SessionVoteResource {

    @Inject
    AttendeeService attendeeService;

    @Inject
    SessionRatingService ratingService;

    @POST
    @Path("/{attendee}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rate(@PathParam("attendee") Integer attendeeId, SessionRating rating) {
        //TODO - Get email properly from logged in user via JWT
        var attendee = attendeeService.getAttendee(attendeeId);
        if(attendee.isPresent()) {
            return Response.ok(ratingService.addRating(rating, attendee.get())).build();
        }else{
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/session/{session}")
    public List<SessionRating> getRatingsForSession(@PathParam("session") String sessionId) {
        return ratingService.getRatingsFor(sessionId);
    }

    @GET
    @Path("/{attendee}")
    public List<SessionRating> getRatingsByAttendee(@PathParam("attendee") String attendeeId) {
        return ratingService.getRatingsBy(attendeeId);
    }

    @GET
    @Path("/summary/{session}")
    public Response getSummaryForSession(@PathParam("session") String sessionId) {
        var results = ratingService.getRatingsFor(sessionId);
        return Response.ok().entity(Json.createObjectBuilder()
                    .add("count", results.size())
                    .add("average", results.stream().mapToDouble(SessionRating::getRating).average().orElse(0.0))
                    .build())
                .build();
    }
}
