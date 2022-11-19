package fish.payara.demos.conference.vote.health;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class SessionServiceCheck implements HealthCheck {

    private static final Logger LOG = Logger.getLogger(SessionServiceCheck.class.getName());

    @Inject
    @ConfigProperty(name = "fish.payara.demos.conference.vote.rs.interfaces.SessionServiceClient/mp-rest/url")
    private URI sessionServiceURL;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("microservice-session")
                .withData("url", sessionServiceURL.toString())
                .status(checkService())
                .build();
    }

    private boolean checkService() {
        try{
            var response = ClientBuilder.newClient()
                    .target(sessionServiceURL)
                    .path("/ping")
                    .request()
                    .get();
            return response.getStatus() == 200;
        }catch(RuntimeException exception){
            LOG.log(Level.WARNING, "Error pinging Session Service: {0}", exception.getLocalizedMessage());
            return false;
        }
    }
}
