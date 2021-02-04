package fish.payara.demos.conference.vote;

import javax.annotation.security.DeclareRoles;
import javax.annotation.sql.DataSourceDefinition;
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
@LoginConfig(authMethod = "MP-JWT")//, realmName = "MP-JWT")
@DeclareRoles("CAN_VOTE")
@DataSourceDefinition(
        name = "java:global/voteDS",
        className = "com.mysql.cj.jdbc.MysqlDataSource",
        user = "${ENV=DB_USER}",        
        password = "${ENV=DB_PASSWORD}",
        url = "${ENV=DB_JDBC_URL}",
        properties = {
            "allowPublicKeyRetrieval=true",
            "useSSL=false",
            "requireSSL=false"
        }
)
public class VoteApplication extends Application{
}
