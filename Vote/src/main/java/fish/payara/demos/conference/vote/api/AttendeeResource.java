package fish.payara.demos.conference.vote.api;

import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.Credentials;
import fish.payara.demos.conference.vote.jwt.TokenGenerator;
import fish.payara.demos.conference.vote.services.AttendeeService;
import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
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
