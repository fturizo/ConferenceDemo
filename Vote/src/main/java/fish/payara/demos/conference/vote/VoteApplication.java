package fish.payara.demos.conference.vote;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 *
 * @author fabio
 */
@ApplicationPath("/")
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT", realmName = "MP-JWT")
@DeclareRoles("CAN_VOTE")
public class VoteApplication extends Application{
}
