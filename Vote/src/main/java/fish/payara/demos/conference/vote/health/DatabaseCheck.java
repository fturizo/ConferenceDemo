package fish.payara.demos.conference.vote.health;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

/**
 *
 * @author Fabio Turizo
 */
@Health
@ApplicationScoped
public class DatabaseCheck implements HealthCheck{

    private static final Logger LOG = Logger.getLogger(DatabaseCheck.class.getName());
    
    //Resource injection not working correctly
    /*@Resource(lookup = "jdbc/voteDS")
    DataSource dataSource;*/
    
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("database")
                                 .withData("name", "session")
                                 .state(checkOnDatabase())
                                 .build();
    }

    private boolean checkOnDatabase() {
        try(Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/C:\\Users\\fabio\\db\\session", "session", "session");
                Statement statement = connection.createStatement()){
            return statement.execute("select 1 from dual");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error pinging database", ex);
            return false;
        }
    }
}
