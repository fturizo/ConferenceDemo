package fish.payara.demos.conference.ui.controllers;

import fish.payara.demos.conference.ui.clients.AttendeeServiceClient;
import fish.payara.demos.conference.ui.clients.SessionServiceClient;
import fish.payara.demos.conference.ui.clients.SessionVoteServiceClient;
import fish.payara.demos.conference.ui.clients.VenueServiceClient;
import fish.payara.demos.conference.ui.entities.Attendee;
import fish.payara.demos.conference.ui.entities.Session;
import fish.payara.demos.conference.ui.entities.SessionRating;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.primefaces.event.RateEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named
@ViewScoped
/*
  TODO - features to add:
  - Give session summary
 */
public class SessionController implements BaseController{

    private static final Logger LOGGER = Logger.getLogger(SessionController.class.getName());

    private final Map<String, Boolean> ratingChecks = new HashMap<>();
    private Map<String, Object> ratings;
    private List<Session> sessions;
    private List<String> venueNames;
    private Session currentSession;
    private Attendee currentAttendee;

    @Inject
    @RestClient
    SessionServiceClient sessionServiceClient;

    @Inject
    @RestClient
    VenueServiceClient venueServiceClient;

    @Inject
    @RestClient
    AttendeeServiceClient attendeeServiceClient;

    @Inject
    @RestClient
    SessionVoteServiceClient sessionVoteServiceClient;

    @Inject
    AuthController authController;

    @PostConstruct
    public void init(){
        try {
            loadSessions();
            loadVenues();
            checkRegistration();
            loadRatings();
        }catch (Exception exception){
            LOGGER.log(Level.SEVERE, "Error", exception);
            addErrorMessage("Cannot load sessions and/or venues!", exception.getMessage());
        }
    }

    private Attendee retrieveAttendee(String email){
        try(var response = attendeeServiceClient.getAttendee(email)){
            if(response.getStatus() == 200) {
                return this.currentAttendee = response.readEntity(Attendee.class);
            }else{
                addErrorMessage("Attendee Information", "Couldn't retrieve attendee information");
                return null;
            }
        }catch (WebApplicationException exception){
            throw exception;
        }
    }

    private void checkRegistration(){
        var email = authController.getCurrentIdentityEmail();
        try{
            this.currentAttendee = retrieveAttendee(email);
        }catch (WebApplicationException exception){
            if(exception.getResponse().getStatus() == 404) {
                var newAttendee = new Attendee(authController.getCurrentIdentityName(), email);
                try(var registerResponse = attendeeServiceClient.register(newAttendee)){
                    if (registerResponse.getStatus() == 200) {
                        addSuccessMessage("Registration successful!", "You have been automatically registered as an attendee!");
                        this.currentAttendee = retrieveAttendee(email);
                    }else{
                        addErrorMessage("Error on registration", "Couldn't register yourself as an attendee");
                    }
                }
            }
        }
    }

    private void loadSessions(){
        sessions = sessionServiceClient.getSessions();
    }

    private void loadVenues(){
        venueNames = venueServiceClient.getVenues();
    }

    private void loadRatings(){
        if(currentAttendee != null) {
            var personalRatings = sessionVoteServiceClient.getRatingsByAttendee(currentAttendee.getId())
                    .stream()
                    .collect(Collectors.toMap(SessionRating::getSessionId, Function.identity()));
            this.ratings = sessions.stream().collect(Collectors.toMap(Session::getId, session -> checkRatings(personalRatings, session)));
        }
    }

    private Integer checkRatings(Map<String, SessionRating> currentRatings, Session session){
        if(currentRatings.containsKey(session.getId())){
            this.ratingChecks.put(session.getId(), true);
            return currentRatings.get(session.getId()).getRating();
        }else {
            this.ratingChecks.put(session.getId(), false);
            return 0;
        }
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<String> getVenueNames() {
        return venueNames;
    }

    public Map<String, Object> getRatings() {
        return ratings;
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public Map<String, Boolean> getRatingChecks() {
        return ratingChecks;
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

    public void submitRating(Session session){
        var rating = processRating(session);
        if(rating != null && rating > 0){
            var response = this.sessionVoteServiceClient.rate(new SessionRating(session, rating));
            if(response.getStatus() == 200) {
                ratingChecks.put(session.getId(), true);
                addSuccessMessage("Rating Successful", "Thank you for rating this session!");
            }else{
                addErrorMessage("Rating error - %s".formatted(response.getStatus()), "Couldn't rate this session");
            }
        }
    }

    //Need to do this as the rating component assigns wrong type to the map's entry value
    private Integer processRating(Session session){
        var rating = ratings.get(session.getId());
        if(rating instanceof String sRating){
            return Integer.parseInt(sRating);
        }else if(rating instanceof Integer iRating){
            return iRating;
        }
        return 0;
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
