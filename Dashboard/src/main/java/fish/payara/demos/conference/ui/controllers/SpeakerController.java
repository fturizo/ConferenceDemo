package fish.payara.demos.conference.ui.controllers;

import fish.payara.demos.conference.ui.clients.SpeakerServiceClient;
import fish.payara.demos.conference.ui.entities.Speaker;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.primefaces.PrimeFaces;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class SpeakerController implements BaseController{

    private static final Logger LOGGER = Logger.getLogger(SpeakerController.class.getName());

    private List<Speaker> speakers;
    private Speaker currentSpeaker;

    @Inject
    @RestClient
    SpeakerServiceClient speakerService;

    @PostConstruct
    public void init(){
        try {
            speakers = speakerService.getSpeakers();
        }catch (Exception exception){
            LOGGER.log(Level.SEVERE, "Error", exception);
            addErrorMessage("Cannot load speakers!", exception.getMessage());
        }
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public Speaker getCurrentSpeaker() {
        return currentSpeaker;
    }

    public void saveNewSpeaker(){
        try {
            speakerService.addSpeaker(currentSpeaker);
            this.init();
            PrimeFaces.current().executeScript("PF('newSpeakerDialog').hide()");
            addSuccessMessage("Success!", "New speaker added");
        }catch(Exception exception){
            addErrorMessage("An error has occurred!", exception.getMessage());
        }
    }

    public void prepareNewSpeaker(){
        LOGGER.info("Preparing new speaker");
        this.currentSpeaker = new Speaker();
    }
}
