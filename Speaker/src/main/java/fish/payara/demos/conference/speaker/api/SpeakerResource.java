package fish.payara.demos.conference.speaker.api;

import fish.payara.demos.conference.speaker.entitites.Speaker;
import fish.payara.demos.conference.speaker.services.SpeakerService;
import java.util.List;
import java.util.Optional;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.links.Link;
import org.eclipse.microprofile.openapi.annotations.links.LinkParameter;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 *
 * @author Fabio Turizo
 */
@Path("/speaker")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SpeakerResource {

    @Inject
    SpeakerService speakerService;

    @GET
    @Path("/{id}")
    @Operation(description = "Get an speaker from its ID")
    @APIResponse(description = "Speaker information",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON, 
                                    schema = @Schema(ref = "#/components/schemas/Speaker")),
                 responseCode = "200")
    @APIResponse(description = "Speaker not found", responseCode = "404")
    public Response getSpeaker(@Parameter(description = "ID of the speaker", required = false) 
                               @PathParam("id") String id) {
        return Optional.ofNullable(speakerService.get(id))
                .map(Response::ok)
                .orElse(Response.status(Status.NOT_FOUND))
                .build();
    }

    @POST
    @Operation(description = "Create a new speaker")
    @APIResponse(description = "Speaker resource location", responseCode = "201", 
                 links = @Link(name = "location", description = "Location of the new speaker", operationId = "getSpeaker",
                               parameters = @LinkParameter(name = "id", expression = "$response.body#/id")))
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSpeaker(@RequestBody(description = "Speaker information", required = true,
                                            content = @Content(schema = @Schema(ref = "#/components/schemas/Speaker"))) 
                                Speaker speaker, @Context UriInfo uriInfo) {
        Speaker result = speakerService.save(speaker);
        return Response.created(UriBuilder.fromPath(uriInfo.getPath()).path("{id}").build(result.getId()))
                        .entity(speaker).build();
    }
    
    @HEAD
    @Operation(description = "Verifies that the supplied speakers exists")
    @APIResponse(description = "All supplied names are registered", responseCode = "200")
    @APIResponse(description = "One or more of the supplied names do not correspond to a valid speaker", responseCode = "404")
    public Response checkSpeakers(@Parameter(description = "List of names to verify") @QueryParam("names") List<String> names){
        return (speakerService.allNamesExists(names) ? Response.ok() : Response.status(Status.NOT_FOUND)).build();
    }
}
