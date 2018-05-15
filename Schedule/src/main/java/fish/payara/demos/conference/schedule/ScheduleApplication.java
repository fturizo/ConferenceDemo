package fish.payara.demos.conference.schedule;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * 
 * @author Fabio Turizo
 */
@ApplicationPath("/")
@ApplicationScoped
public class ScheduleApplication extends Application{
}
