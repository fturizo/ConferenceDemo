package fish.payara.demos.combined.utils;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;

public interface ContainerUtils {

    static final String DEFAULT_JDK_VERSION = "11";
    int HTTP_PORT = 8080;

    DockerImageName PAYARA_MICRO_IMAGE = DockerImageName.parse("payara/micro");

    private static String getCurrentPlatformVersion(){
        String version = System.getProperty("payaraVersion");
        if(version == null) {
            version = System.getProperty("payara.version");
            if (version == null) {
                throw new IllegalStateException("Current Payara Platform version not set either via system properties");
            }
        }
        return version;
    }

    private static String getCurrentJDKVersion(){
        String version = System.getProperty("jdkVersion");
        if(version == null){
            version = System.getProperty("jdk.version");
            if(version == null) {
                throw new IllegalStateException("Current JDK version not set either via system properties");
            }
        }
        return version;
    }

    private static String getCurrentTag(){
        String platformVersion = getCurrentPlatformVersion();
        String javaVersion = getCurrentJDKVersion();
        return platformVersion + (DEFAULT_JDK_VERSION.equals(javaVersion) ? "" : String.format("-jdk%s", javaVersion));
    }

    static DockerImageName getDefaultImage(){
        return PAYARA_MICRO_IMAGE.withTag(getCurrentTag());
    }

    static URI buildURI(GenericContainer container, String path){
        return UriBuilder.fromUri("http://" + container.getHost())
                .port(container.getMappedPort(HTTP_PORT))
                .path(path)
                .build();
    }
}
