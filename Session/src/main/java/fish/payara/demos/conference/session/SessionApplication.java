package fish.payara.demos.conference.session;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationPath("/")
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
public class SessionApplication extends Application{
}
