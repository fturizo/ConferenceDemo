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
@SuppressWarnings("injection")
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
        return nameClaim.orElse(null);
    }

    public String getCurrentIdentityEmail(){
        var emailClaim = openIdContext.getClaims().getEmail();
        return emailClaim.orElse(null);
    }

    public String getAccessToken(){
        if(isAuthenticated()){
            var token = openIdContext.getAccessToken();
            return token.getToken();
        }else {
            return "NONE";
        }
    }

    public boolean hasRole(String role){
        return securityContext.isCallerInRole(role);
    }

    public void login(){
        LOGGER.info("Signing in");
        var context = FacesContext.getCurrentInstance().getExternalContext();
        var request = (HttpServletRequest)context.getRequest();
        var response = (HttpServletResponse)context.getResponse();
        securityContext.authenticate(request, response, new AuthenticationParameters());
    }

    public void logout() throws ServletException {
        LOGGER.info("Logging out");
        var context = FacesContext.getCurrentInstance().getExternalContext();
        var request = (HttpServletRequest)context.getRequest();
        request.logout();
        FacesContext.getCurrentInstance().responseComplete();
    }
}
