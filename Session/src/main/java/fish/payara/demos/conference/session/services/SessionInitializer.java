package fish.payara.demos.conference.session.services;

import fish.payara.demos.conference.session.entities.Session;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Fabio Turizo
 */
@ApplicationScoped
public class SessionInitializer {

    private static final Logger LOG = Logger.getLogger(SessionInitializer.class.getName());

    @Inject
    SessionService sessionService;

    public void initialize(@Observes Startup event){
        LOG.info("Initializing sessions");
        if(sessionService.all().isEmpty()){
            sessionService.register(new Session("Fun with Kubernetes and Payara Micro", "OCARINA", LocalDate.now(), Duration.ofMinutes(120), List.of("John Doe")));
            sessionService.register(new Session("Securing Microservices with Okta and MicroProfile", "ESPERANZA", LocalDate.now(), Duration.ofMinutes(40), List.of("Adam Fullbright")));
            sessionService.register(new Session("Securing Microservices with Auth0 and MicroProfile", "ALFAJOR", LocalDate.now(), Duration.ofMinutes(40), List.of("John Doe", "Adam Fullbright")));
        }
    }
}
