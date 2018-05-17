package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.SessionRating;
import fish.payara.demos.conference.vote.services.AttendeeService;
import fish.payara.demos.conference.vote.services.SessionRatingService;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Fallback;

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
    public Response rate(SessionRating rating){
        Attendee currentUser = attendeeService.getByEmail(jwtPrincipal.getName())
                                                .orElseThrow(() -> new BadRequestException("Invalid JWT token"));
        //Attendee currentUser = attendeeService.getById(1).get();
        return Response.ok(ratingService.addRating(rating, currentUser)).build();
    }
    
    @GET
    @Path("/session/{session}")
    public List<SessionRating> getRatingSummary(@PathParam("session") Integer sessionId){
        return ratingService.getRatingsFor(sessionId);
    }
}
