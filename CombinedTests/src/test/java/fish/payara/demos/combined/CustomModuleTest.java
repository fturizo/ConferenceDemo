package fish.payara.demos.combined;

import fish.payara.demos.combined.containers.PayaraMicroContainer;
import fish.payara.demos.combined.utils.ContainerUtils;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import static fish.payara.demos.combined.utils.ContainerUtils.buildURI;
import static fish.payara.demos.combined.utils.JSONTemplates.*;
import static fish.payara.demos.combined.utils.JSONTemplates.SESSION_RATING;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomModuleTest {

    private static final Network INTERNAL_NETWORK = Network.newNetwork();
    private static final String DB_ALIAS = "db";

    @Container
    private static MySQLContainer dbContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withNetwork(INTERNAL_NETWORK)
            .withNetworkAliases(DB_ALIAS);

    @Container
    private static PayaraMicroContainer speakerService = new PayaraMicroContainer<>(ContainerUtils.getDefaultImage())
            .withNoCluster()
            .withNetworkAndAlias(INTERNAL_NETWORK, "speaker")
            .withDeployment("microservice-speaker.war");

    @Container
    private static PayaraMicroContainer sessionService = new PayaraMicroContainer<>(ContainerUtils.getDefaultImage())
            .withNoCluster()
            .withNetworkAndAlias(INTERNAL_NETWORK, "session")
            .dependsOn(speakerService)
            .withEnv(Map.of(
                    "fish.payara.demos.conference.session.rs.clients.SpeakerServiceClient/mp-rest/url", "http://speaker:8080/",
                    "fish.payara.demos.conference.session.rs.clients.VenueServiceClient/mp-rest/url", "http://speaker:8080/"
            ))
            .withDeployment("microservice-session.war");

    @Container
    private static PayaraMicroContainer voteService = new PayaraMicroContainer<>(ContainerUtils.getDefaultImage())
            .withNetwork(INTERNAL_NETWORK)
            .dependsOn(dbContainer, sessionService)
            .withEnv(Map.of(
                    "DB_JDBC_URL", String.format("jdbc:mysql://%s:3306/%s", DB_ALIAS, dbContainer.getDatabaseName()),
                    "DB_USER", dbContainer.getUsername(),
                    "DB_PASSWORD",  dbContainer.getPassword(),
                    "fish.payara.demos.conference.vote.rs.interfaces.SessionServiceClient/mp-rest/url", "http://session:8080/"
            ))
            .withDeployment("microservice-vote.war")
            .withLibrary("mysql-connector.jar");

    //Create sample speaker and session data
    @BeforeAll
    public static void prepareData(){
        given().
                contentType(ContentType.JSON).
                body(SPEAKER.formatted("Fabio Turizo", "Payara Services Limited")).
                when().
                post(buildURI(speakerService, "/speaker"));
        given().
                contentType(ContentType.JSON).
                body(SESSION.formatted("Easy IT with TestContainers", "OCARINA", LocalDate.now(), Duration.ofHours(1), "[\"Fabio Turizo\"]")).
                when().
                post(buildURI(sessionService, "/session"));
    }

    @Test
    @DisplayName("Register attendee")
    @Order(1)
    public void registerAttendee(){
        given().
                contentType(ContentType.JSON).
                body(ATTENDEE.formatted("Alfredo Molina", "alfredo.molina@gmail.com")).
                when().
                post(buildURI(voteService, "/attendee")).
                then().
                assertThat().statusCode(201);
    }

    @Test
    @DisplayName("Rate session #1")
    @Order(3)
    public void rateSession1(){
        given().
                contentType(ContentType.JSON).
                body(SESSION_RATING.formatted("1", 5)).
                when().
                post(buildURI(voteService, "/rating/1")).
                then().
                assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Rate session #2")
    @Order(3)
    public void rateSession2(){
        given().
                contentType(ContentType.JSON).
                body(SESSION_RATING.formatted("1", 3)).
                when().
                post(buildURI(voteService, "/rating/1")).
                then().
                assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Get session rating summary")
    @Order(4)
    public void getSessionSummary(){
        given().
                contentType(ContentType.JSON).
                when().
                get(buildURI(voteService, "/rating/summary/1")).
                then().
                assertThat().statusCode(200).
                and().
                body("count", equalTo( 2));
    }
}
