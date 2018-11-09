# Rating Votes Service

Handles the management of attendee data and rating sessions.

# Database Preparation

This service requires an already running **H2** database in order to persist the data. Download the JAR file and run it like this:

	java -jar h2-1.4.197.jar

Then, configure the database location in the `prepare-jdbc.asadmin`:

	set resources.jdbc-connection-pool.localH2Pool.property.URL=jdbc:h2:tcp://localhost/${DB_LOCATION}\session

# Running the Service

You can run the service directly by using the `payara-micro.jar` like this:

	java -jar payara-micro.jar --deploy target/microservice-vote.war --clusterName vote --autoBindHttp --postbootcommandfile db/prepare-jdbc.asadmin --enablerequesttracing

Or by running the `payara-micro:start` goal:

	mvn payara-micro:start