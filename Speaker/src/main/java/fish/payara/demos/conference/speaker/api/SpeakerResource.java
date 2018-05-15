package fish.payara.demos.conference.speaker.api;

import fish.payara.demos.conference.speaker.entitites.Speaker;
import fish.payara.demos.conference.speaker.services.SpeakerService;
import java.net.URI;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
    public Response getSpeaker(@PathParam("id") Integer id) {
        return speakerService.get(id)
                .map(Response::ok)
                .orElse(Response.status(Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response addSpeaker(Speaker speaker) {
        Speaker result = speakerService.save(speaker);
        return Response.created(URI.create("/" + result.getId()))
                        .entity(speaker).build();
    }
}
