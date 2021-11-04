package fish.payara.demos.conference.session.api.exception;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 *
 * @author fabio
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException>{

    @Override
    public Response toResponse(RuntimeException ex) {
        ResponseBuilder rbuilder = null;
        if(ex instanceof IllegalArgumentException){
            rbuilder = Response.status(Status.BAD_REQUEST);
        }else if(ex instanceof IllegalStateException){
            rbuilder = Response.status(Status.CONFLICT);
        }else {
            ex.printStackTrace();
            rbuilder = Response.serverError();
        }
        return rbuilder.type(MediaType.APPLICATION_JSON).entity(wrapException(ex)).build();
    }
    
    private JsonObject wrapException(RuntimeException ex){
        return Json.createObjectBuilder().add("error", ex.getMessage()).build();
    } 
}
