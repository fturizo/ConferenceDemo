package fish.payara.demos.conference.session;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationPath("/")
@ApplicationScoped
public class SessionApplication extends Application{
}
