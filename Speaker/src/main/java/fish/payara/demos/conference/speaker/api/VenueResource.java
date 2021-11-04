package fish.payara.demos.conference.speaker.api;

import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 *
 * @author Fabio Turizo
 */
@Path("/venue")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class VenueResource {
    
    @Inject
    @ConfigProperty(name = "demo.conference.speaker.venues", defaultValue = "Ocarina")
    private List<String> venues;
    
    @GET
    @Path("/all")
    @Operation(description = "Retrieves a list of all current venues")
    @APIResponse(description = "The list of current venues", responseCode = "200",
                 content = @Content(schema = @Schema(type = SchemaType.ARRAY)))
    public List<String> all(){
        return venues;
    }
    
    @HEAD
    @Path("/{name}")
    @Operation(description = "Verifies if venue exists")
    @APIResponse(description = "The venue does exist", responseCode = "200")
    @APIResponse(description = "Venue doesn't exist", responseCode = "404")
    public Response checkVenue(@Parameter(description = "The name of the venue to verify") @PathParam("name") String name){
        return (venues.contains(name) ? Response.ok(): Response.status(Status.NOT_FOUND)).build();
    }
}
