package fish.payara.demos.conference.vote.health;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.sql.DataSource;

/**
 *
 * @author Fabio Turizo
 */
@Liveness
@ApplicationScoped
public class DatabaseCheck implements HealthCheck{

    private static final Logger LOG = Logger.getLogger(DatabaseCheck.class.getName());
    
    //Resource injection not working correctly
    /*@Resource(name = "jdbc/voteDS")
    DataSource dataSource;*/
    
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("DatabaseCheck")
                                 .withData("name", "session")
                                 .status(checkOnDatabase())
                                 .build();
    }

    private boolean checkOnDatabase() {
        //FIXME - Driver load fails when included in the WAR file
        try(Connection connection = DriverManager.getConnection(System.getenv("DB_JDBC_URL"),
                                                                System.getenv("DB_USER"),
                                                                System.getenv("DB_PASSWORD"));
                Statement statement = connection.createStatement()){
            return statement.execute("select 1 from dual");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error pinging database", ex);
            return false;
        }
    }
}
