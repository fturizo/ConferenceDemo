
package fish.payara.demos.conference.ui.clients;

import fish.payara.demos.conference.ui.entities.Speaker;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient(configKey = "speaker")
@Path("/speaker")
public interface SpeakerServiceClient {

    @GET
    List<Speaker> getSpeakers();

    @POST
    Response addSpeaker(Speaker speaker);
}
