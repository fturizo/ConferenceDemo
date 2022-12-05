package fish.payara.demos.combined;

import fish.payara.demos.conference.speaker.entitites.Speaker;
import fish.payara.test.testcontainers.PayaraMicroContainer;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpeakerServiceTest {

    @Container
    private static PayaraMicroContainer speakerService = new PayaraMicroContainer()
                .withNoCluster()
                .withDeployment("microservice-speaker.war");

    @Test
    @DisplayName("Add speakers")
    @Order(1)
    public void addSpeaker() {
        given().
                contentType(ContentType.JSON).
                body(new Speaker("Fabio Turizo", "Payara Services Limited")).
                when().
                post(speakerService.buildURI("/speaker")).
                then().
                assertThat().statusCode(201)
                            .and()
                            .header("Location", speakerService.buildURI("/speaker/1").toString());
    }

    @Test
    @DisplayName("Get speaker data")
    @Order(2)
    public void getSpeakerData(){
        given().
                contentType(ContentType.JSON).
                when().
                get(speakerService.buildURI("/speaker/1")).
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
                head(speakerService.buildURI("/speaker")).
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
                head(speakerService.buildURI("/speaker")).
                then().
                assertThat().statusCode(404);
    }
}
