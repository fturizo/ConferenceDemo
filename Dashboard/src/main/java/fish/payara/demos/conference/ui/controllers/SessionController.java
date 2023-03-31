package fish.payara.demos.conference.ui.controllers;

import fish.payara.demos.conference.ui.clients.SessionServiceClient;
import fish.payara.demos.conference.ui.clients.VenueServiceClient;
import fish.payara.demos.conference.ui.entities.Session;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
/*
  TODO - features to add:
  - Vote on sessions
  - Auto register attendees the first time (so they can vote on sessions)
 */
public class SessionController implements BaseController{

    private static final Logger LOGGER = Logger.getLogger(SessionController.class.getName());

    private List<Session> sessions;
    private List<String> venueNames;
    private Session currentSession;

    @Inject
    @RestClient
    SessionServiceClient sessionServiceClient;

    @Inject
    @RestClient
    VenueServiceClient venueServiceClient;

    @PostConstruct
    public void init(){
        try {
            loadSessions();
            loadVenues();
        }catch (Exception exception){
            LOGGER.log(Level.SEVERE, "Error", exception);
            addErrorMessage("Cannot load sessions and/or venues!", exception.getMessage());
        }
    }

    private void loadSessions(){
        sessions = sessionServiceClient.getSessions();
    }

    private void loadVenues(){
        venueNames = venueServiceClient.getVenues();
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<String> getVenueNames() {
        return venueNames;
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void saveNewSession(){
        try {
            sessionServiceClient.create(currentSession);
            loadSessions();
            hidePFDialog("newSessionDialog");
            addSuccessMessage("Success!", "New session created");
        }catch(Exception exception){
            addErrorMessage("An error has occurred!", exception.getMessage());
        }
    }

    public void prepareNewSession(){
        this.currentSession = new Session();
        this.currentSession.setDuration(Duration.ofHours(1));
    }

    public void removeSession(String id){
        try {
            sessionServiceClient.delete(id);
            loadSessions();
            addSuccessMessage("Success!", "Session deleted");
        }catch(Exception exception){
            addErrorMessage("An error has occurred!", exception.getMessage());
        }
    }
}
