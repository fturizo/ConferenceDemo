package fish.payara.test.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PayaraMicroContainer extends GenericContainer<PayaraMicroContainer> {

    private static final DockerImageName MICRO_IMAGE = DockerImageName.parse("payara/micro:5.2022.3-jdk17");

    private static final int DEFAULT_HTTP_PORT = 8080;
    private static final String DEFAULT_WAIT_ENDPOINT = "/application.wadl";
    private static final String DEFAULT_DEPLOYMENT_PATH = "/opt/payara/deployments/";

    private static final String DEFAULT_LIBS_PATH = "/opt/payara/libs/";

    private static final String LOCAL_DEPLOYMENT_PATH = "target/wars/";

    private static final String LOCAL_LIBS_PATH = "target/libs/";

    private boolean noCluster = false;

    private boolean enableRequestTracing = false;
    private String contextRoot = "/";

    private LocalFileUnit deploymentUnit;

    private List<LocalFileUnit> additionalLibraries;

    public PayaraMicroContainer(){
        super(MICRO_IMAGE);
        this.setExposedPorts(List.of(DEFAULT_HTTP_PORT));
        this.setWaitStrategy(Wait.forHttp(DEFAULT_WAIT_ENDPOINT).forStatusCode(200));
    }

    public int getMappedHttpPort(){
        return getMappedPort(DEFAULT_HTTP_PORT);
    }

    public PayaraMicroContainer withNetworkAndAlias(Network network, String alias){
        return withNetwork(network)
                .withNetworkAliases(alias);
    }

    public PayaraMicroContainer withNoCluster(){
        this.noCluster = true;
        return this;
    }

    public PayaraMicroContainer withRequestTracing(){
        this.enableRequestTracing = true;
        return this;
    }

    public PayaraMicroContainer withContextRoot(String contextRoot){
        this.contextRoot = contextRoot;
        return this;
    }

    public PayaraMicroContainer withDeployment(String fileName){
        var warFile = MountableFile.forHostPath(Paths.get(LOCAL_DEPLOYMENT_PATH + fileName).toAbsolutePath(), 0777);
        var deploymentFilePath = DEFAULT_DEPLOYMENT_PATH + fileName;
        this.deploymentUnit = new LocalFileUnit(warFile, deploymentFilePath);
        return withCopyFileToContainer(warFile, deploymentFilePath);
    }

    public PayaraMicroContainer withLibrary(String fileName){
        var jarFile = MountableFile.forHostPath(Paths.get(LOCAL_LIBS_PATH + fileName).toAbsolutePath(), 0777);
        var libFile = DEFAULT_LIBS_PATH + fileName;
        if(this.additionalLibraries == null){
            this.additionalLibraries = new ArrayList<>();
        }
        this.additionalLibraries.add(new LocalFileUnit(jarFile, libFile));
        return withCopyFileToContainer(jarFile, libFile);
    }

    private StringBuilder setupCommand(){
        var options = new StringBuilder();
        if(noCluster){
            options.append("--noCluster ");
        }
        if(enableRequestTracing){
            options.append("--enablerequesttracing ");
        }
        if(deploymentUnit != null){
            options.append("--deploy ")
                    .append(this.deploymentUnit.path)
                    .append(" ");
        }
        if(additionalLibraries != null && !additionalLibraries.isEmpty()){
            options.append("--addLibs ")
                    .append(additionalLibraries.stream().map(LocalFileUnit::path).collect(Collectors.joining(":")))
                    .append(" ");
        }
        if(contextRoot != null){
            options.append("--contextRoot ")
                    .append(this.contextRoot)
                    .append(" ");
        }
        return options;
    }

    public URI buildURI(String path){
        return UriBuilder.fromUri("http://" + getHost())
                .port(getMappedHttpPort())
                .path(path)
                .build();
    }

    @Override
    public void start(){
        this.setCommand(setupCommand().toString());
        super.start();
    }

    private record LocalFileUnit(MountableFile file, String path){
    }
}
