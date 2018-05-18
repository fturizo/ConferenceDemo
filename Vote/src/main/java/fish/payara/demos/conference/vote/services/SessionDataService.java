package fish.payara.demos.conference.vote.services;

import java.io.StringReader;
import java.net.URI;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class SessionDataService {

    private WebTarget sessionService;

    @PostConstruct
    public void init() {
        sessionService = ClientBuilder.newClient().target(URI.create("http://localhost:8081/microservice-session"));
    }

    public String getSessionSummary(Integer id) {
        Response response = sessionService.path("/session/" + id)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatusInfo().toEnum() == Response.Status.OK) {
            try (StringReader reader = new StringReader(response.readEntity(String.class));
                    JsonReader jsonReader = Json.createReader(reader)) {
                JsonObject result = jsonReader.readObject();
                return String.format("[%s] - %s, V: %s", result.getString("date"),
                        result.getString("title"), result.getString("venue"));
            }
        } else {
            throw new IllegalArgumentException("Invalid session id");
        }
    }
}
