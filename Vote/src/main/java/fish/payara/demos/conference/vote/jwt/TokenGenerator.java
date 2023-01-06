package fish.payara.demos.conference.vote.jwt;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fish.payara.demos.conference.vote.entities.Attendee;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

/**
 * @author Fabio Turizo
 */
@ApplicationScoped
public class TokenGenerator {
    
    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    private String issuer;
    
    public String generateFor(Attendee attendee){
        try{
            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID("/privateKey.pem")
                    .type(JOSEObjectType.JWT).build(), JWTClaimsSet.parse(generateJWT(attendee).toString()));
            signedJWT.sign(new RSASSASigner(readPrivateKey("/META-INF/keys/privateKey.pem")));
            return signedJWT.serialize();
        } catch(Exception ex){
            throw new RuntimeException("Failed generating JWT", ex);
        }
    }

    private JsonObject generateJWT(Attendee attendee) {
        
        long secondsNow = System.currentTimeMillis() / 1_000;
        return Json.createObjectBuilder()
                .add(Claims.aud.name(), "attendees")
                .add(Claims.jti.name(), "att-" + attendee.getId())
                .add(Claims.sub.name(), attendee.getName())
                .add(Claims.upn.name(), attendee.getEmail())
                .add(Claims.exp.name(), secondsNow + 10_000)
                .add(Claims.iat.name(), secondsNow)
                .add(Claims.iss.name(), issuer)
                .add(Claims.auth_time.name(), secondsNow)
                .add(Claims.groups.name(), Json.createArrayBuilder().add(attendee.getRole().name()).build())
                .build();
    }

    private static PrivateKey readPrivateKey(String resourceName) throws Exception {
        return KeyFactory.getInstance("RSA")
                         .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(readKey(resourceName))));
    }
    
    private static String readKey(String resourceName) throws Exception{
        byte[] byteBuffer = new byte[16384]; 
        int length = Thread.currentThread().getContextClassLoader()
                .getResource(resourceName)
                .openStream()
                .read(byteBuffer);

        String key = new String(byteBuffer, 0, length).replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .trim();
        return key;
    }
}
