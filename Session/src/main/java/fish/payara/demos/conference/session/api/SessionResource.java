package fish.payara.demos.conference.session.api;

import fish.payara.demos.conference.session.entities.Session;
import fish.payara.demos.conference.session.services.SessionService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.metrics.annotation.Metered;

/**
 *
 * @author Fabio Turizo
 */
@Path("/session")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {

    @Inject
    SessionService sessionService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Metered(name = "session.creation.tries", absolute = true)
    public Response create(Session session, @Context UriInfo uriInfo) {
        session = sessionService.register(session);
        return Response.created(UriBuilder.fromPath(uriInfo.getPath()).path("{id}").build(session.getId()))
                .entity(session).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        return sessionService.retrieve(id).map(Response::ok)
                .orElse(Response.status(Status.NOT_FOUND)).build();
    }

    @DELETE
    @Path("/{id}")
    @Metered(name = "session.deletion.tries", absolute = true)
    public Response delete(@PathParam("id") String id) {
        Optional<Session> session = sessionService.retrieve(id);
        if (session.isPresent()) {
            sessionService.delete(session.get());
            return Response.accepted().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/date/{date}")
    public List<Session> forDate(@PathParam("date") String date) {
        return sessionService.retrieve(LocalDate.parse(date));
    }
}
