package fish.payara.demos.conference.vote.api.providers;

import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
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
