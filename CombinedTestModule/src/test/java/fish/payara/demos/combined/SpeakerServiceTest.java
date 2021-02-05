package fish.payara.demos.combined;

import fish.payara.demos.combined.base.PayaraMicroContainerBuilder;
import fish.payara.demos.conference.speaker.entitites.Speaker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static fish.payara.demos.combined.utils.ContainerUtils.buildURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpeakerServiceTest {

    @Container
    private static GenericContainer speakerService = PayaraMicroContainerBuilder.fromDefault()
            .withArtifact("microservice-speaker.war").build();

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
