package fish.payara.demos.conference.ui.controllers;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;

public interface BaseController extends Serializable {

    default void addMessage(String summary, String message, FacesMessage.Severity severity){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, message));
    }

    default void addSuccessMessage(String summary, String message){
        addMessage(summary, message, FacesMessage.SEVERITY_INFO);
    }

    default void addErrorMessage(String summary, String message){
        addMessage(summary, message, FacesMessage.SEVERITY_ERROR);
    }
}
