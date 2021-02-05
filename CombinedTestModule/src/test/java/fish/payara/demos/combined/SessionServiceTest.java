package fish.payara.demos.combined;

import fish.payara.demos.combined.base.PayaraMicroContainerBuilder;
import fish.payara.demos.conference.session.entities.Session;
import fish.payara.demos.conference.speaker.entitites.Speaker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static fish.payara.demos.combined.utils.ContainerUtils.buildURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SessionServiceTest {

    private final static Network INTERNAL_NETWORK = Network.newNetwork();

    @Container
    private static GenericContainer speakerService = PayaraMicroContainerBuilder.fromDefault()
            .withNetwork(INTERNAL_NETWORK, "speaker")
            .withArtifact("microservice-speaker.war")
            .build();

    @Container
    private static GenericContainer sessionService = PayaraMicroContainerBuilder.fromDefault()
            .withNetwork(INTERNAL_NETWORK, "session")
            .withArtifact("microservice-session.war")
            .withEnv("fish.payara.demos.conference.session.rs.clients.SpeakerServiceClient/mp-rest/url", "http://speaker:8080/")
            .withEnv("fish.payara.demos.conference.session.rs.clients.VenueServiceClient/mp-rest/url", "http://speaker:8080/")
            .build();

    @Test
    @DisplayName("Add speakers")
    @Order(1)
    public void addSpeaker() {
        given().
                contentType(ContentType.JSON).
                body(new Speaker("Fabio Turizo", "Payara Services Limited")).
                when().
                post(buildURI(speakerService, "/speaker")).
                then().
                assertThat().statusCode(201)
                            .and()
                            .header("Location", buildURI(speakerService, "/speaker/1").toString());
    }

    @Test
    @DisplayName("Add new session 1")
    @Order(2)
    public void addSession1(){
        Session sampleSession = new Session("Easy IT with TestContainers", "OCARINA", LocalDate.now(), Duration.ofHours(1), List.of("Fabio Turizo"));
        given().
                contentType(ContentType.JSON).
                body(sampleSession).
                when().
                post(buildURI(sessionService, "/session")).
                then().
                assertThat().statusCode(201)
                            .and()
                            .header("Location",  buildURI(sessionService, "/session/1").toString());
    }

    @Test
    @DisplayName("Add new session 2")
    @Order(3)
    public void addSession2(){
        Session sampleSession = new Session("Securing Microservices with Okta", "ESPERANZA", LocalDate.now(), Duration.ofHours(1), List.of("Fabio Turizo"));
        given().
                contentType(ContentType.JSON).
                body(sampleSession).
                when().
                post(buildURI(sessionService, "/session")).
                then().
                assertThat().statusCode(201)
                .and()
                .header("Location",  buildURI(sessionService, "/session/2").toString());
    }

    @Test
    @DisplayName("Get session data")
    @Order(4)
    public void getSessionData(){
        given().
                contentType(ContentType.JSON).
                when().
                get(buildURI(sessionService, "/session/1")).
                then().
                assertThat().statusCode(200).
                            and().
                            body("title", equalTo("Easy IT with TestContainers"));
    }

    @Test
    @DisplayName("Check session doesn't exist")
    @Order(4)
    public void checkSessionDoesntExists(){
        given().
                when().
                get(buildURI(sessionService, "/session/100")).
                then().
                assertThat().statusCode(404);
    }

    @Test
    @DisplayName("Delete session data")
    @Order(5)
    public void deleteSession(){
        given().
                when().
                delete(buildURI(sessionService, "/session/1")).
                then().
                assertThat().statusCode(202);
    }
}
