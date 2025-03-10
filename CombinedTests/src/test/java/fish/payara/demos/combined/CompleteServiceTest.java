package fish.payara.demos.combined;

import fish.payara.demos.combined.utils.ContainerUtils;

import static fish.payara.demos.combined.BasicServiceTest.SPEAKER_DEPLOYABLE;
import static fish.payara.demos.combined.utils.ContainerUtils.buildURI;
import static fish.payara.demos.combined.utils.JSONTemplates.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompleteServiceTest {

    static final MountableFile VOTE_DEPLOYABLE = MountableFile.forHostPath(Paths.get("target/wars/microservice-vote.war").toAbsolutePath(), 0777);
    static final MountableFile MYSQL_DRIVER = MountableFile.forHostPath("target/libs/mysql-connector.jar", 0777);
    private static final Network INTERNAL_NETWORK = Network.newNetwork();
    private static final String DB_ALIAS = "db";

    @Container
    private static MySQLContainer dbContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                                                .withNetwork(INTERNAL_NETWORK)
                                                .withNetworkAliases(DB_ALIAS);
    @Container
    private static GenericContainer speakerService = new GenericContainer(ContainerUtils.getDefaultImage())
                .withNetwork(INTERNAL_NETWORK)
                .withNetworkAliases("speaker")
                .withExposedPorts(ContainerUtils.HTTP_PORT)
                .withCopyFileToContainer(SPEAKER_DEPLOYABLE, "/opt/payara/deployments/microservice-speaker.war")
                .waitingFor(Wait.forHttp("/application.wadl").forStatusCode(200))
                .withCommand("--noCluster --deploy /opt/payara/deployments/microservice-speaker.war --contextRoot /");

    //TODO - Both the sesssion and vote services require authentication details
    @Container
    private static GenericContainer sessionService = new GenericContainer(ContainerUtils.getDefaultImage())
                .withExposedPorts(ContainerUtils.HTTP_PORT)
                .dependsOn(speakerService)
                .withNetwork(INTERNAL_NETWORK)
                .withNetworkAliases("session")
                .withEnv("fish.payara.demos.conference.session.rs.clients.SpeakerServiceClient/mp-rest/url", "http://speaker:8080/")
                .withEnv("fish.payara.demos.conference.session.rs.clients.VenueServiceClient/mp-rest/url", "http://speaker:8080/")
                .withCopyFileToContainer(BasicServiceTest.SESSION_DEPLOYABLE, "/opt/payara/deployments/microservice-session.war")
                .waitingFor(Wait.forHttp("/application.wadl").forStatusCode(200))
                .withCommand("--noCluster --deploy /opt/payara/deployments/microservice-session.war --contextRoot /");

    @Container
    private static GenericContainer voteService = new GenericContainer(ContainerUtils.getDefaultImage())
                .withExposedPorts(ContainerUtils.HTTP_PORT)
                .withNetwork(INTERNAL_NETWORK)
                .dependsOn(dbContainer, sessionService)
                .withEnv("DB_JDBC_URL", String.format("jdbc:mysql://%s:3306/%s", DB_ALIAS, dbContainer.getDatabaseName()))
                .withEnv("DB_USER", dbContainer.getUsername())
                .withEnv("DB_PASSWORD",  dbContainer.getPassword())
                .withEnv("fish.payara.demos.conference.vote.rs.interfaces.SessionServiceClient/mp-rest/url", "http://session:8080/")
                .withCopyFileToContainer(VOTE_DEPLOYABLE, "/opt/payara/deployments/micro-service-vote.war")
                .withCopyFileToContainer(MYSQL_DRIVER, "/opt/payara/libs/mysql-connector.jar")
                .waitingFor(Wait.forHttp("/application.wadl").forStatusCode(200))
                .withCommand("--deploy /opt/payara/deployments/micro-service-vote.war --addLibs /opt/payara/libs/mysql-connector.jar --contextRoot /");

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
