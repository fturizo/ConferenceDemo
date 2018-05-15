package fish.payara.demos.conference.schedule.api;

import fish.payara.demos.conference.schedule.entities.Schedule;
import fish.payara.demos.conference.schedule.services.ScheduleService;
import java.net.URI;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Fabio Turizo
 */
@Path("/schedule")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class ScheduleResource {
    
    @Inject
    ScheduleService scheduleService;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(Schedule schedule){
        Schedule result = scheduleService.save(schedule);
        return Response.created(URI.create("/" + result.getId()))
                .entity(result)
                .build();
    }
    
    @GET
    @Path("/all")
    public List<Schedule> all(){
        return scheduleService.getAllSchedules();
    }
}
