package fish.payara.demos.conference.vote.entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 *
 * @author Fabio Turizo
 */
public class Credentials {
    
    private String email;
    private String password;

    @JsonbCreator
    public Credentials(@JsonbProperty("email") String email, @JsonbProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
