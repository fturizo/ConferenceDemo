package fish.payara.demos.conference.session.services;

import fish.payara.demos.conference.session.rs.clients.SpeakerServiceClient;
import fish.payara.demos.conference.session.rs.clients.VenueServiceClient;
import fish.payara.demos.conference.session.entities.Session;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author Fabio Turizo
 */
@ApplicationScoped
public class SpeakerDomainChecker {

    private static final Logger LOG = Logger.getLogger(SpeakerDomainChecker.class.getName());

    @Inject
    @RestClient
    SpeakerServiceClient speakerServiceClient;
    
    @Inject
    @RestClient
    VenueServiceClient venueServiceClient;

    public boolean checkVenue(Session session) {
        try{
            Response response = venueServiceClient.checkVenue(session.getVenue());
            return response.getStatusInfo().toEnum() == Status.OK;
        }catch(WebApplicationException ex){
            LOG.warning(ex.getLocalizedMessage());
            return false;
        }
    }

    private boolean disableSpeakersCheck = true;

    public boolean checkSpeakers(Session session) {
        if(disableSpeakersCheck){
            return true;
        }
        try{
            //TODO - Not working, see https://payara.atlassian.net/browse/FISH-62
            Response response = speakerServiceClient.checkSpeakers(session.getSpeakers());
            return response.getStatusInfo().toEnum() == Status.OK;
        }catch(WebApplicationException ex){
            LOG.warning(ex.getLocalizedMessage());
            return false;
        }
    }
}
