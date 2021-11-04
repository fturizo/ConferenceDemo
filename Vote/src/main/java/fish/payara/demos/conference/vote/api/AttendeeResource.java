package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.Credentials;
import fish.payara.demos.conference.vote.jwt.TokenGenerator;
import fish.payara.demos.conference.vote.services.AttendeeService;
import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
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
    
    @Inject
    TokenGenerator tokenGenerator;
    
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
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials){
        return attendeeService.verify(credentials)
                        .map(tokenGenerator::generateFor)
                        .map(AttendeeResource::prepareJWTResponse)
                        .orElse(Response.status(Status.BAD_REQUEST))
                        .build();
    }
    
    private static ResponseBuilder prepareJWTResponse(String token){
        return Response.ok()
                       .entity(Json.createObjectBuilder().add("token", token).build());
    }
}
