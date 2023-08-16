package fish.payara.demos.conference.ui.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.build.compatible.spi.ScopeInfo;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.ClaimsDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.LogoutDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.PromptType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@OpenIdAuthenticationMechanismDefinition(
        clientId = "${oidConfig.clientID}",
        clientSecret = "${oidConfig.clientSecret}",
        redirectURI = "${baseURL}/index.xhtml",
        scope = {"openid", "email", "profile", "offline_access"},
        prompt = PromptType.LOGIN,
        providerURI = "${oidConfig.providerUri}",
        jwksReadTimeout = 10_000,
        jwksConnectTimeout = 10_000,
        claimsDefinition = @ClaimsDefinition(callerGroupsClaim = "${oidConfig.callerGroupsClaim}"),
        extraParameters = "audience=https://api.payara.fish/",
        logout = @LogoutDefinition(redirectURI = "${baseURL}/index.xhtml")
)
@Named("oidConfig")
@ApplicationScoped
public class OpenIDSecurityConfiguration {

    private static final String ROLES_CLAIM = "/roles";

    @Inject
    @ConfigProperty(name = "fish.payara.demos.conference.security.openid.clientId")
    private String clientID;

    @Inject
    @ConfigProperty(name = "fish.payara.demos.conference.security.openid.clientSecret")
    private String clientSecret;

    @Inject
    @ConfigProperty(name = "fish.payara.demos.conference.security.openid.provider.uri")
    private String providerUri;

    @Inject
    @ConfigProperty(name = "fish.payara.demos.conference.security.openid.custom.namespace")
    private String customNamespace;

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getProviderUri() {
        return providerUri;
    }

    public String getCallerGroupsClaim() {
        return customNamespace + ROLES_CLAIM;
    }
}
