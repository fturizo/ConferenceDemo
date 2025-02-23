package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.services.AttendeeService;
import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Retry;

/**
 *
 * @author Fabio Turizo
 */
@Path("/attendee")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AttendeeResource {

    @Inject
    AttendeeService attendeeService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 5, delay = 30, delayUnit = ChronoUnit.SECONDS)
    public Response register(Attendee attendee){
        Attendee result = attendeeService.create(attendee);
        return Response.created(URI.create("/attendee/" + result.getId()))
                       .entity(result).build();
    }

    @GET
    @Path("/all")
    public List<Attendee> getAll(){
        return attendeeService.getAllAttendees();
    }

    @GET
    public Response getAttendee(@QueryParam("email") String email){
        var builder = attendeeService.getByEmail(email)
                .map(Response::ok)
                .orElse(Response.status(404));
        return builder.build();
    }
}
