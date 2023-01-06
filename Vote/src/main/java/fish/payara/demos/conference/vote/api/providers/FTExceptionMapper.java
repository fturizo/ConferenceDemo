package fish.payara.demos.conference.vote.api.providers;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

/**
 *
 * @author Fabio Turizo
 */
@Provider
public class FTExceptionMapper implements ExceptionMapper<FaultToleranceException>{

    @Override
    public Response toResponse(FaultToleranceException ex) {
       return Response.serverError()
                      .entity(Json.createObjectBuilder()
                                  .add("message", "Fault Tolerance Error: " + ex.getClass().getCanonicalName())
                                  .build())
                      .build();
    }
    
}
