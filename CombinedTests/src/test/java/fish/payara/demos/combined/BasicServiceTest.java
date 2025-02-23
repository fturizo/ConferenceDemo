package fish.payara.demos.combined;

import fish.payara.demos.combined.utils.ContainerUtils;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;

import static fish.payara.demos.combined.utils.ContainerUtils.buildURI;
import static fish.payara.demos.combined.utils.JSONTemplates.SESSION;
import static fish.payara.demos.combined.utils.JSONTemplates.SPEAKER;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicServiceTest {

    static final MountableFile SPEAKER_DEPLOYABLE = MountableFile.forHostPath(Paths.get("target/wars/microservice-speaker.war").toAbsolutePath(), 0777);

    static final MountableFile SESSION_DEPLOYABLE = MountableFile.forHostPath(Paths.get("target/wars/microservice-session.war").toAbsolutePath(), 0777);
    private final static Network INTERNAL_NETWORK = Network.newNetwork();

    @Container
    private static GenericContainer speakerService = new GenericContainer(ContainerUtils.getDefaultImage())
                .withNetwork(INTERNAL_NETWORK)
                .withNetworkAliases("speaker")
                .withExposedPorts(ContainerUtils.HTTP_PORT)
                .withCopyFileToContainer(SPEAKER_DEPLOYABLE, "/opt/payara/deployments/microservice-speaker.war")
                .waitingFor(Wait.forHttp("/application.wadl").forStatusCode(200))
                .withCommand("--noCluster --deploy /opt/payara/deployments/microservice-speaker.war --contextRoot /");

    @Container
    private static GenericContainer sessionService = new GenericContainer(ContainerUtils.getDefaultImage())
            .withExposedPorts(ContainerUtils.HTTP_PORT)
            .withNetwork(INTERNAL_NETWORK)
            .withEnv("fish.payara.demos.conference.session.rs.clients.SpeakerServiceClient/mp-rest/url", "http://speaker:8080/")
            .withEnv("fish.payara.demos.conference.session.rs.clients.VenueServiceClient/mp-rest/url", "http://speaker:8080/")
            .withCopyFileToContainer(SESSION_DEPLOYABLE, "/opt/payara/deployments/microservice-session.war")
            .waitingFor(Wait.forHttp("/application.wadl").forStatusCode(200))
            .withCommand("--noCluster --deploy /opt/payara/deployments/microservice-session.war --contextRoot /");

    @Test
    @DisplayName("Add speakers")
    @Order(1)
    public void addSpeaker() {
        given().
                contentType(ContentType.JSON).
                body(SPEAKER.formatted("Fabio Turizo", "Payara Services Limited")).
                when().
                post(buildURI(speakerService, "/speaker")).
                then().
                assertThat().statusCode(201);
    }

    @Test
    @DisplayName("Get speaker data")
    @Order(2)
    public void getSpeakerData(){
        given().
                when().
                get(buildURI(speakerService, "/speaker/")).
                then().
                contentType(ContentType.JSON).
                assertThat().statusCode(200)
                .and()
                .body("[0].name", equalTo("Fabio Turizo"));
    }

    @Test
    @DisplayName("Verify speaker exists")
    @Order(3)
    public void checkSpeakerExists(){
        given().
                queryParam("names", "Fabio Turizo").
                when().
                head(buildURI(speakerService, "/speaker")).
                then().
                assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Verify speaker doesn't exist")
    @Order(3)
    public void checkSpeakerDoesntExist(){
        given().
                queryParam("names", "Andres Correa").
                when().
                head(buildURI(speakerService, "/speaker")).
                then().
                assertThat().statusCode(404);
    }

    @Test
    @DisplayName("Add new session 1")
    @Order(4)
    public void addSession1(){
        given().
                contentType(ContentType.JSON).
                body(SESSION.formatted("Easy IT with TestContainers", "OCARINA", LocalDate.now(), Duration.ofHours(1), "[\"Fabio Turizo\"]")).
                when().
                post(buildURI(sessionService, "/session")).
                then().
                assertThat().statusCode(201);
    }

    @Test
    @DisplayName("Add new session 2")
    @Order(5)
    public void addSession2(){
        given().
                contentType(ContentType.JSON).
                body(SESSION.formatted("Securing Microservices with Okta", "ESPERANZA", LocalDate.now(), Duration.ofHours(1), "[\"Fabio Turizo\"]")).
                when().
                post(buildURI(sessionService, "/session")).
                then().
                assertThat().statusCode(201);
    }

    @Test
    @DisplayName("Get session data")
    @Order(6)
    public void getSessionData(){
        given().
                contentType(ContentType.JSON).
                when().
                get(buildURI(sessionService, "/session")).
                then().
                assertThat().statusCode(200).
                            and().
                            body("[0].title", equalTo("Easy IT with TestContainers"));
    }
}
