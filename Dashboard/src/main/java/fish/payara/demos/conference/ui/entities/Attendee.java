package fish.payara.demos.conference.ui.entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Fabio Turizo
 */
public class Attendee implements Serializable {

    private Integer id;
    private String name;
    private String email;

    public Attendee(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @JsonbCreator
    public Attendee(@JsonbProperty("id") Integer id,
                    @JsonbProperty("name") String name,
                    @JsonbProperty("email") String email) {
        this(name, email);
        this.id = id;
    }

    @JsonbProperty
    public Integer getId() {
        return id;
    }

    @JsonbProperty
    public String getName() {
        return name;
    }

    @JsonbProperty
    public String getEmail() {
        return email;
    }

    @Override
    public int hashCode() {
        var hash = 5;
        hash = 23 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attendee other = (Attendee) obj;
        return Objects.equals(this.id, other.id);
    }
}
