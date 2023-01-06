package fish.payara.demos.conference.session.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Fabio Turizo
 */
@Path("/ping")
public class PingResource {
    
    @GET
    public Response execute(){
        return Response.ok().build();
    }
}
