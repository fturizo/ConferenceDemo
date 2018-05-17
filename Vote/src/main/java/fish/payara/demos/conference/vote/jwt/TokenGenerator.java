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
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import org.eclipse.microprofile.jwt.Claims;

/**
 * @author Fabio Turizo
 */
@ApplicationScoped
public class TokenGenerator {

    private static final Logger LOG = Logger.getLogger(TokenGenerator.class.getName());
    
    public String generateFor(Attendee attendee){
        try{
            String jwt = generateJWT(attendee).toString();
            //LOG.info(jwt);
            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID("/privateKey.pem")
                    .type(JOSEObjectType.JWT).build(), JWTClaimsSet.parse(jwt));
            signedJWT.sign(new RSASSASigner(readPrivateKey("privateKey.pem")));
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
                .add(Claims.exp.name(), secondsNow)
                .add(Claims.iat.name(), secondsNow + 1_000)
                .add(Claims.iss.name(), "demos.payara.fish")
                .add(Claims.auth_time.name(), secondsNow)
                .add(Claims.groups.name(), Json.createArrayBuilder().add(attendee.getRole().name()).build())
                .build();
    }

    public static PrivateKey readPrivateKey(String resourceName) throws Exception {
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
        LOG.info(key);
        return KeyFactory.getInstance("RSA")
                         .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
    }
}
