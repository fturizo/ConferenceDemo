package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.SessionRating;
import fish.payara.demos.conference.vote.services.AttendeeService;
import fish.payara.demos.conference.vote.services.SessionRatingService;
import io.opentracing.Tracer;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.opentracing.Traced;

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
    
    @Inject
    Tracer tracer;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Traced
    public Response rate(SessionRating rating) {
        tracer.activeSpan().setTag("isCritical", true);
        Attendee currentUser = attendeeService.getByEmail(jwtPrincipal.getName())
                .orElseThrow(() -> new BadRequestException("Invalid JWT token"));
        return Response.ok(ratingService.addRating(rating, currentUser)).build();
    }

    @GET
    @Traced(operationName = "get-ratings")
    @Path("/session/{session}")
    public List<SessionRating> getRatingsForSession(@PathParam("session") Integer sessionId) {
        return ratingService.getRatingsFor(sessionId);
    }

    @GET
    @Path("/summary/{session}")
    public Response getSummaryForSession(@PathParam("session") Integer sessionId) {
        List<SessionRating> results = ratingService.getRatingsFor(sessionId);
        System.out.println("Session count: " + results.size());
        return Response.ok().entity(Json.createObjectBuilder()
                    .add("count", results.size())
                    .add("average", results.stream().mapToDouble(SessionRating::getRating).average().orElse(0.0))
                    .build())
                .build();
    }
}
