package fish.payara.demos.conference.session.api.exception;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
