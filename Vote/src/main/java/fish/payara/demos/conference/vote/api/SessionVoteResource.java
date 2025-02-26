package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.SessionRating;
import fish.payara.demos.conference.vote.services.AttendeeService;
import fish.payara.demos.conference.vote.services.SessionDataService;
import fish.payara.demos.conference.vote.services.SessionRatingService;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author Fabio Turizo
 */
@Path("/rating")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("can-see-sessions")
public class SessionVoteResource {

    @Inject
    AttendeeService attendeeService;

    @Inject
    JsonWebToken jwt;

    @Inject
    private SessionDataService sessionDataService;

    @Inject
    SessionRatingService ratingService;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rate(SessionRating rating) {
        var attendee = attendeeService.getByEmail(jwt.getClaim(Claims.email));
        if(attendee.isPresent()) {
            var summary = sessionDataService.getSessionSummary(jwt.getRawToken(), rating.getSessionId());
            return Response.ok(ratingService.addRating(rating, attendee.get(), summary)).build();
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
    public List<SessionRating> getRatingsByAttendee(@PathParam("attendee") Integer attendeeId) {
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
