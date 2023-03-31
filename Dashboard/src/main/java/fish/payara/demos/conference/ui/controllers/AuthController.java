package fish.payara.demos.conference.ui.controllers;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;

@Named
@RequestScoped
public class AuthController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    @Inject
    SecurityContext securityContext;

    @Inject
    OpenIdContext openIdContext;

    public boolean isAuthenticated(){
        return Optional.ofNullable(securityContext.getCallerPrincipal()).isPresent();
    }

    public String getCurrentIdentityName(){
        var nameClaim = openIdContext.getClaims().getName();
        return nameClaim.isPresent() ? nameClaim.get() : null;
    }

    public boolean hasSpeakerRole(){
        return securityContext.isCallerInRole("speaker");
    }

    public boolean hasAttendeeRole(){
        return securityContext.isCallerInRole("attendee");
    }

    public boolean hasAdminRole(){
        return securityContext.isCallerInRole("admin");
    }

    public void login(){
        LOGGER.info("Signing in");
        var request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        var response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
        securityContext.authenticate(request, response, new AuthenticationParameters());
    }

    public void logout() throws ServletException {
        LOGGER.info("Logging out");
        var request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.logout();
        FacesContext.getCurrentInstance().responseComplete();
    }
}
