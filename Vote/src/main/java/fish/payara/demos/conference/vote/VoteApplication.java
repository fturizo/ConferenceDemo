package fish.payara.demos.conference.vote;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 *
 * @author fabio
 */
@ApplicationPath("/")
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
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
