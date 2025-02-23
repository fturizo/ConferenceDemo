package fish.payara.demos.combined.containers;

import jakarta.ws.rs.core.UriBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PayaraMicroContainer<SELF extends PayaraMicroContainer<SELF>> extends GenericContainer<SELF> {

    private static final DockerImageName MICRO_IMAGE = DockerImageName.parse("payara/micro");

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

    public PayaraMicroContainer(final DockerImageName imageName){
        super(imageName);
        imageName.assertCompatibleWith(MICRO_IMAGE);
        this.setExposedPorts(List.of(DEFAULT_HTTP_PORT));
        this.setWaitStrategy(Wait.forHttp(DEFAULT_WAIT_ENDPOINT).forStatusCode(200));
    }

    public int getMappedHttpPort(){
        return getMappedPort(DEFAULT_HTTP_PORT);
    }

    public SELF withNetworkAndAlias(Network network, String alias){
        return withNetwork(network)
                .withNetworkAliases(alias);
    }

    public SELF withNoCluster(){
        this.noCluster = true;
        return self();
    }

    public SELF withRequestTracing(){
        this.enableRequestTracing = true;
        return self();
    }

    public SELF withContextRoot(String contextRoot){
        this.contextRoot = contextRoot;
        return self();
    }

    public SELF withDeployment(String fileName){
        var warFile = MountableFile.forHostPath(Paths.get(LOCAL_DEPLOYMENT_PATH + fileName).toAbsolutePath(), 0777);
        var deploymentFilePath = DEFAULT_DEPLOYMENT_PATH + fileName;
        this.deploymentUnit = new LocalFileUnit(warFile, deploymentFilePath);
        return withCopyFileToContainer(warFile, deploymentFilePath);
    }

    public SELF withLibrary(String fileName){
        var jarFile = MountableFile.forHostPath(Paths.get(LOCAL_LIBS_PATH + fileName).toAbsolutePath(), 0777);
        var libFile = DEFAULT_LIBS_PATH + fileName;
        if(this.additionalLibraries == null){
            this.additionalLibraries = new ArrayList<>();
        }
        this.additionalLibraries.add(new LocalFileUnit(jarFile, libFile));
        return withCopyFileToContainer(jarFile, libFile);
    }

    private String[] setupCommand(){
        var commandParts = new ArrayList<String>();
        if(noCluster){
            commandParts.add("--noCluster");
        }
        if(enableRequestTracing){
            commandParts.add("--enablerequesttracing");
        }
        if(deploymentUnit != null){
            commandParts.add("--deploy");
            commandParts.add(this.deploymentUnit.path);
        }
        if(additionalLibraries != null && !additionalLibraries.isEmpty()){
            commandParts.add("--addLibs");
            commandParts.addAll(additionalLibraries.stream().map(LocalFileUnit::path).toList());
        }
        if(contextRoot != null){
            commandParts.add("--contextRoot");
            commandParts.add(contextRoot);
        }
        return commandParts.toArray(new String[0]);
    }

    public URI buildURI(String path){
        return UriBuilder.fromUri("http://" + getHost())
                .port(getMappedHttpPort())
                .path(path)
                .build();
    }

    @Override
    protected void configure(){
        var command = setupCommand();
        logger().info("With command options: {}", String.join(" ", command));
        this.setCommand(command);
    }

    private record LocalFileUnit(MountableFile file, String path){
    }
}
