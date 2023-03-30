package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.SessionRating;
import fish.payara.demos.conference.vote.services.AttendeeService;
import fish.payara.demos.conference.vote.services.SessionRatingService;
import java.security.Principal;
import java.util.List;
import io.opentracing.Tracer;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.BadRequestException;
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
@RolesAllowed("CAN_VOTE")
public class SessionVoteResource {

    @Inject
    Principal jwtPrincipal;

    @Inject
    AttendeeService attendeeService;

    @Inject
    SessionRatingService ratingService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rate(SessionRating rating) {
        Attendee currentUser = attendeeService.getByEmail(jwtPrincipal.getName())
                .orElseThrow(() -> new BadRequestException("Invalid JWT token"));
        return Response.ok(ratingService.addRating(rating, currentUser)).build();
    }

    @GET
    @Path("/session/{session}")
    public List<SessionRating> getRatingsForSession(@PathParam("session") String sessionId) {
        return ratingService.getRatingsFor(sessionId);
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
