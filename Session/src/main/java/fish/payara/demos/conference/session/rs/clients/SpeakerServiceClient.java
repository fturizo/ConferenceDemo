
package fish.payara.demos.conference.session.rs.clients;

import java.util.List;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient
@Path("/speaker")
public interface SpeakerServiceClient {
    
    @HEAD
    Response checkSpeakers(@QueryParam("names") List<String> names);
}
