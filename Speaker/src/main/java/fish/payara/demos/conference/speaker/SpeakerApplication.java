package fish.payara.demos.conference.speaker;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationPath("/")
@ApplicationScoped
public class SpeakerApplication extends Application{
}
