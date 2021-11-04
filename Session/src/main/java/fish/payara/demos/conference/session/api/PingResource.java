package fish.payara.demos.conference.session.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

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
