package fish.payara.demos.combined;

import fish.payara.demos.combined.utils.ContainerUtils;
import fish.payara.demos.conference.speaker.entitites.Speaker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;

import static fish.payara.demos.combined.utils.ContainerUtils.buildURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpeakerServiceTest {

    static final MountableFile SPEAKER_DEPLOYABLE = MountableFile.forHostPath(Paths.get("target/wars/microservice-speaker.war").toAbsolutePath(), 0777);

    @Container
    private static GenericContainer speakerService = new GenericContainer(ContainerUtils.PAYARA_MICRO_IMAGE)
                .withExposedPorts(ContainerUtils.HTTP_PORT)
                .withCopyFileToContainer(SPEAKER_DEPLOYABLE, "/opt/payara/deployments/microservice-speaker.war")
                .waitingFor(Wait.forHttp("/application.wadl").forStatusCode(200))
                .withCommand("--noCluster --deploy /opt/payara/deployments/microservice-speaker.war --contextRoot /");

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
    @DisplayName("Get speaker data")
    @Order(2)
    public void getSpeakerData(){
        given().
                contentType(ContentType.JSON).
                when().
                get(buildURI(speakerService, "/speaker/1")).
                then().
                assertThat().statusCode(200)
                            .and()
                            .body("name", equalTo("Fabio Turizo"));
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
}
