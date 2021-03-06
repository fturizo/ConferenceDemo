# Rating Votes Service

Handles the management of attendee data and rating sessions.

# Database Preparation

This service requires an already running **MYSQL** database in order to persist the data. The easiest way to prepare one is to use Docker:

    docker run --name=test-mysql -e MYSQL_ROOT_PASSWORD=test -e MYSQL_USER=test -e MYSQL_PASSWORD=test -e MYSQL_DATABASE=test -p 3306:3306 --rm -d mysql

Then, configure the following environment variables in your local environment:

* `DB_USER`: The user of the database
* `DB_PASSWORD`: The password of the corresponding user
* `DB_JDBC_URL`: The full JDBC url used to connect to the database.

In addition, a proper MySQL Connector driver will have to be added to the classpath of the micro instance when started to correctly work as well.

# Running the Service

You can run the service directly by using the `payara-micro.jar` like this:

	java -jar payara-micro.jar --deploy target/microservice-vote.war --clusterName vote --autoBindHttp --postbootcommandfile db/prepare-jdbc.asadmin --enablerequesttracing --addLibs /path/to/mysql-connector.jar

Or by running the `payara-micro:start` goal:

	mvn payara-micro:start
