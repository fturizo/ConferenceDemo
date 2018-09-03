package fish.payara.demos.conference.session.services;

import fish.payara.demos.conference.session.entities.Session;
import java.net.URI;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class SpeakerDomainChecker {

    private WebTarget speakerService;

    @PostConstruct
    public void init() {
        speakerService = ClientBuilder.newClient().target(URI.create("http://localhost:8080/microservice-speaker/"));
    }

    public boolean checkVenue(Session session) {
        Response response = speakerService.path("/venue/check/" + session.getVenue())
                .request().head();
        return response.getStatusInfo().toEnum() == Status.OK;
    }

    public boolean checkSpeakers(Session session) {
        WebTarget target = speakerService.path("/speaker/check/")
                                         .queryParam("names", session.getSpeakers().toArray());
        return target.request().head().getStatusInfo().toEnum() == Status.OK;
    }
}
