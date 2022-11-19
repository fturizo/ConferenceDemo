package fish.payara.demos.conference.vote.services;

import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import fish.payara.demos.conference.vote.rs.interfaces.SessionServiceClient;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class SessionDataService {

    private static final Logger LOG = Logger.getLogger(SessionDataService.class.getName());

    @Inject
    @RestClient
    private SessionServiceClient sessionService;

    public String getSessionSummary(String id) {
        try{
            JsonObject result = sessionService.get(id);
            return String.format("[%s] - %s, V: %s", result.getString("date"), 
                                result.getString("title"), result.getString("venue"));
        }catch(WebApplicationException ex){
            LOG.log(Level.WARNING, "Error retrieving session with id: {0} message: {1}", new Object[]{id, ex.getLocalizedMessage()});
            throw new IllegalArgumentException("Invalid session id");
        }
    }
}
