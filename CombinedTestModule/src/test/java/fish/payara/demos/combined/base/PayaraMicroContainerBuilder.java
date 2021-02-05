package fish.payara.demos.combined.base;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.*;

public final class PayaraMicroContainerBuilder {

    private static final int DEFAULT_HTTP_PORT = 8080;
    private static final String DEFAULT_ARTIFACT_DIR = "target/wars/";
    private static final String DEFAULT_LOCAL_LIBS_DIR = "target/libs/";
    private static final String DEFAULT_DEPLOYMENT_DIR = "/opt/payara/deployments/";
    private static final String DEFAULT_CONTAINER_LIBS_DIR = "/opt/payara/libs/";

    private String imageName;
    private String artifactName;
    private boolean enableClustering;
    private StringBuilder startupArgs = new StringBuilder();

    private Network network;
    private String networkAlias;
    private HashMap<String, String> environment = new HashMap<>(10);
    private List<GenericContainer> dependencies = new ArrayList<>(5);
    private List<String> libraryBindings = new ArrayList<>(5);

    private PayaraMicroContainerBuilder(Optional<String> version){
        this.imageName = String.format("payara/micro:%s", version.orElse("5.2020.7-jdk11"));
        this.enableClustering = false;
    }

    public PayaraMicroContainerBuilder withClustering(){
        this.enableClustering = true;
        return this;
    }

    public PayaraMicroContainerBuilder withStartupArg(String arg){
        this.startupArgs.append(arg).append(" ");
        return this;
    }

    public PayaraMicroContainerBuilder withArtifact(String artifactName){
        this.artifactName = artifactName;
        return this;
    }

    public PayaraMicroContainerBuilder withNetwork(Network network, String alias){
        this.network = network;
        this.networkAlias = alias;
        return this;
    }

    public PayaraMicroContainerBuilder withEnv(String variable, String value){
        this.environment.put(variable, value);
        return this;
    }

    public PayaraMicroContainerBuilder withDependencies(GenericContainer... containers){
        this.dependencies.addAll(List.of(containers));
        return this;
    }

    public PayaraMicroContainerBuilder withLibBinding(String... bindings){
        this.libraryBindings.addAll(List.of(bindings));
        return this;
    }

    public GenericContainer build(){
        GenericContainer container = new GenericContainer(DockerImageName.parse(imageName))
                .dependsOn(dependencies)
                .withExposedPorts(DEFAULT_HTTP_PORT);
        if(network != null){
            container.withNetwork(network);
            container.withNetworkAliases(networkAlias);
        }
        if(artifactName != null){
            container.withFileSystemBind(DEFAULT_ARTIFACT_DIR + artifactName, DEFAULT_DEPLOYMENT_DIR + artifactName, BindMode.READ_WRITE);
        }
        libraryBindings.forEach(binding -> container.withFileSystemBind(DEFAULT_LOCAL_LIBS_DIR + binding, DEFAULT_CONTAINER_LIBS_DIR+ binding, BindMode.READ_WRITE));
        container.withEnv(this.environment);
        container.waitingFor(Wait.forHttp("/application.wadl").forStatusCode(200))
                 .withCommand(buildCommand());
        return container;
    }

    private String buildCommand(){
        StringBuilder command = new StringBuilder();
        if(!enableClustering){
            command.append("--noCluster ");
        }
        if(artifactName != null){
            command.append("--deploy ")
                    .append(DEFAULT_DEPLOYMENT_DIR)
                    .append(artifactName)
                    .append(" --contextRoot / ");
        }
        libraryBindings.forEach(binding -> command.append("--addLibs ")
                                                .append(DEFAULT_CONTAINER_LIBS_DIR)
                                                .append(binding)
                                                .append(" "));
        return command.append(startupArgs).toString();
    }

    public static PayaraMicroContainerBuilder from(String version){
        return new PayaraMicroContainerBuilder(Optional.ofNullable(version));
    }

    public static PayaraMicroContainerBuilder fromDefault(){
        return new PayaraMicroContainerBuilder(Optional.empty());
    }
}
