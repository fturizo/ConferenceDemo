package fish.payara.demos.combined;

import fish.payara.demos.conference.session.entities.Session;
import fish.payara.demos.conference.speaker.entitites.Speaker;
import fish.payara.test.testcontainers.PayaraMicroContainer;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SessionServiceTest {

    private final static Network INTERNAL_NETWORK = Network.newNetwork();

    @Container
    private static PayaraMicroContainer speakerService = new PayaraMicroContainer()
                .withNoCluster()
                .withNetworkAndAlias(INTERNAL_NETWORK, "speaker")
                .withDeployment("microservice-speaker.war");

    @Container
    private static PayaraMicroContainer sessionService = new PayaraMicroContainer()
            .withNoCluster()
            .withNetwork(INTERNAL_NETWORK)
            .withEnv("fish.payara.demos.conference.session.rs.clients.SpeakerServiceClient/mp-rest/url", "http://speaker:8080/")
            .withEnv("fish.payara.demos.conference.session.rs.clients.VenueServiceClient/mp-rest/url", "http://speaker:8080/")
            .withDeployment("microservice-session.war");

    //Create sample speaker data
    @BeforeAll
    public static void prepareData(){
        given().
                contentType(ContentType.JSON).
                body(new Speaker("Fabio Turizo", "Payara Services Limited")).
                when().
                post(speakerService.buildURI("/speaker")).
                then();
    }

    @Test
    @DisplayName("Add new session 1")
    @Order(1)
    public void addSession1(){
        Session sampleSession = new Session("Easy IT with TestContainers", "OCARINA", LocalDate.now(), Duration.ofHours(1), List.of("Fabio Turizo"));
        given().
                contentType(ContentType.JSON).
                body(sampleSession).
                when().
                post(sessionService.buildURI("/session")).
                then().
                assertThat().statusCode(201)
                            .and()
                            .header("Location",  sessionService.buildURI("/session/1").toString());
    }

    @Test
    @DisplayName("Add new session 2")
    @Order(2)
    public void addSession2(){
        Session sampleSession = new Session("Securing Microservices with Okta", "ESPERANZA", LocalDate.now(), Duration.ofHours(1), List.of("Fabio Turizo"));
        given().
                contentType(ContentType.JSON).
                body(sampleSession).
                when().
                post(sessionService.buildURI("/session")).
                then().
                assertThat().statusCode(201)
                .and()
                .header("Location",  sessionService.buildURI("/session/2").toString());
    }

    @Test
    @DisplayName("Get session data")
    @Order(3)
    public void getSessionData(){
        given().
                contentType(ContentType.JSON).
                when().
                get(sessionService.buildURI("/session/1")).
                then().
                assertThat().statusCode(200).
                            and().
                            body("title", equalTo("Easy IT with TestContainers"));
    }

    @Test
    @DisplayName("Check session doesn't exist")
    @Order(3)
    public void checkSessionDoesntExists(){
        given().
                when().
                get(sessionService.buildURI("/session/100")).
                then().
                assertThat().statusCode(404);
    }

    @Test
    @DisplayName("Delete session data")
    @Order(4)
    public void deleteSession(){
        given().
                when().
                delete(sessionService.buildURI("/session/1")).
                then().
                assertThat().statusCode(202);
    }
}
