package fish.payara.demos.conference.vote.health;

import fish.payara.demos.conference.vote.services.AttendeeService;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
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

    /*@PersistenceContext(unitName = "Vote")
    EntityManager em;*/
    
    @Resource(lookup = "jdbc/voteDS")
    DataSource dataSource;
    
    @Inject
    AttendeeService attendeeService;
    
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("persistence")
                                 .withData("persistence-unit", "vote")
                                 .state(checkOnDatabase())
                                 .build();
    }

    private boolean checkOnDatabase() {
        try{
            Connection connection = dataSource.getConnection();
            connection.getMetaData();
            //attendeeService.getAllAttendees();
            return true;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error retrieving metadata", ex);
            return false;
        }
    }
}
