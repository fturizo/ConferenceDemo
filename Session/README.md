# Session Service

Handles the management of sessions (and their schedules).

# Running the Service

You can run the service directly by using the `payara-micro.jar` like this:

	java -jar payara-micro.jar --deploy target/microservice-speaker.war --clusterName speaker --autoBindHttp

Or by running the `payara-micro:start` goal:

	mvn payara-micro:start