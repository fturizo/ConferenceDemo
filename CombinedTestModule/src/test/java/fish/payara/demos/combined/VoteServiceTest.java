package fish.payara.demos.combined;

import fish.payara.demos.combined.base.PayaraMicroContainerBuilder;
import fish.payara.demos.combined.utils.ContainerUtils;
import fish.payara.demos.conference.session.entities.Session;
import fish.payara.demos.conference.vote.entities.Attendee;
import fish.payara.demos.conference.vote.entities.Credentials;
import fish.payara.demos.conference.vote.entities.Role;

import static fish.payara.demos.combined.utils.ContainerUtils.buildURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import fish.payara.demos.conference.vote.entities.SessionRating;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VoteServiceTest {

    private static final Network INTERNAL_NETWORK = Network.newNetwork();
    private static final String DB_ALIAS = "db";
    
    @Container
    private static MySQLContainer dbContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                                                .withNetwork(INTERNAL_NETWORK)
                                                .withNetworkAliases(DB_ALIAS);
    @Container
    private static GenericContainer speakerService = PayaraMicroContainerBuilder.fromDefault()
            .withNetwork(INTERNAL_NETWORK, "speaker")
            .withArtifact("microservice-speaker.war")
            .build();

    @Container
    private static GenericContainer sessionService = PayaraMicroContainerBuilder.fromDefault()
            .withNetwork(INTERNAL_NETWORK, "session")
            .withDependencies(speakerService)
            .withArtifact("microservice-session.war")
            .withEnv("fish.payara.demos.conference.session.rs.clients.SpeakerServiceClient/mp-rest/url", "http://speaker:8080/")
            .withEnv("fish.payara.demos.conference.session.rs.clients.VenueServiceClient/mp-rest/url", "http://speaker:8080/")
            .build();

    @Container
    private static GenericContainer voteService = PayaraMicroContainerBuilder.fromDefault()
            .withClustering()
            .withNetwork(INTERNAL_NETWORK, "vote")
            .withArtifact("microservice-vote.war")
            .withLibBinding("mysql-connector.jar")
            .withDependencies(dbContainer, sessionService)
            .withEnv("DB_JDBC_URL", String.format("jdbc:mysql://%s:3306/%s", DB_ALIAS, dbContainer.getDatabaseName()))
            .withEnv("DB_USER", dbContainer.getUsername())
            .withEnv("DB_PASSWORD",  dbContainer.getPassword())
            .withEnv("fish.payara.demos.conference.vote.rs.interfaces.SessionServiceClient/mp-rest/url", "http://session:8080/")
            .withStartupArg("--enableRequestTracing")
            .build();

    @BeforeAll
    public static void createSession(){
        Session sampleSession = new Session("Easy IT with TestContainers", "OCARINA", LocalDate.now(), Duration.ofHours(1), List.of("Fabio Turizo"));
        given().
                contentType(ContentType.JSON).
                body(sampleSession).
                when().
                post(buildURI(sessionService, "/session"));
    }
    
    @Test
    @DisplayName("Register attendee")
    @Order(1)
    public void registerAttendee(){
        Attendee sampleAttendee = new Attendee("Alfredo Molina", "alfredo.molina@gmail.com", "123456789", Role.CAN_VOTE);
        given().
                contentType(ContentType.JSON).
                body(sampleAttendee).
                when().
                post(buildURI(voteService, "/attendee")).
                then().
                assertThat().statusCode(201);     
    }

    @Test
    @DisplayName("Login attendee success")
    @Order(2)
    public void loginAttendee(){
        given().
                contentType(ContentType.JSON).
                body(new Credentials("alfredo.molina@gmail.com", "123456789")).
                when().
                post(buildURI(voteService, "/attendee/login")).
                then().
                assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Login attendee failure")
    @Order(2)
    public void loginAttendeeFail(){
        given().
                contentType(ContentType.JSON).
                body(new Credentials("alfredo.molina@gmail.com", "--------")).
                when().
                post(buildURI(voteService, "/attendee/login")).
                then().
                assertThat().statusCode(400);
    }

    @Test
    @DisplayName("Rate session #1")
    @Order(3)
    public void rateSession1(){
        SessionRating sampleRating = new SessionRating(1, 5);
        given().
                auth().preemptive().oauth2(retrieveAccessToken()).
                contentType(ContentType.JSON).
                body(sampleRating).
                when().
                post(buildURI(voteService, "/rating")).
                then().
                assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Rate session #2")
    @Order(3)
    public void rateSession2(){
        SessionRating sampleRating = new SessionRating(1, 3);
        given().
                auth().preemptive().oauth2(retrieveAccessToken()).
                contentType(ContentType.JSON).
                body(sampleRating).
                when().
                post(buildURI(voteService, "/rating")).
                then().
                assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Get session rating summary")
    @Order(4)
    public void getSessionSummary(){
        given().
                auth().preemptive().oauth2(retrieveAccessToken()).
                contentType(ContentType.JSON).
                when().
                get(buildURI(voteService, "/rating/summary/1")).
                then().
                assertThat().statusCode(200).
                             and().
                             body("count", equalTo( 2));
    }

    private String retrieveAccessToken(){
        return given().
                    contentType(ContentType.JSON).
                    body(new Credentials("alfredo.molina@gmail.com", "123456789")).
                    when().
                    post(buildURI(voteService, "/attendee/login")).
                    then().
                    extract().
                    path("token");
    }
}
