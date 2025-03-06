package fish.payara.demos.conference.ui.controllers;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Logger;

@RequestScoped
@Named
public class TokenPrinterController {

    private static final Logger logger = Logger.getLogger(TokenPrinterController.class.getName());

    @Inject
    AuthController authController;

    public String getPrintedToken() {
        if (authController.isAuthenticated()) {
            var token = authController.getAccessToken();
            logger.info("Access Token = %s".formatted(token));
        }
        return null;
    }
}
