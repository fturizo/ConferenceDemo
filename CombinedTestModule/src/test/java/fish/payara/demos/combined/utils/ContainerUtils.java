package fish.payara.demos.combined.utils;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public interface ContainerUtils {

    int HTTP_PORT = 8080;

    DockerImageName PAYARA_MICRO_IMAGE = DockerImageName.parse("payara/micro:5.2020.4-jdk11");

    static URI buildURI(GenericContainer container, String path){
        return UriBuilder.fromUri("http://" + container.getHost())
                .port(container.getMappedPort(HTTP_PORT))
                .path(path)
                .build();
    }
}
