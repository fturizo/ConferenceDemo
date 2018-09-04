
package fish.payara.demos.conference.session.rs.clients;

import java.util.List;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author Fabio Turizo
 */
@RegisterRestClient
@Path("/speaker")
public interface SpeakerServiceClient {
    
    @HEAD
    @Path("/check")
    Response checkSpeakers(@QueryParam("names") List<String> names);
}
