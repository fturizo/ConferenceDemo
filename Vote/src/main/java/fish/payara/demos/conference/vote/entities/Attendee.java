package fish.payara.demos.conference.vote.entities;

import java.io.Serializable;
import java.util.Objects;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

/**
 *
 * @author Fabio Turizo
 */
@Entity
@NamedQuery(name = "Attendee.fromEmail", query = "select at from Attendee at where at.email = :email")
@NamedQuery(name = "Attendee.getAll", query = "select at from Attendee at order by at.email")
public class Attendee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String email;

    public Attendee() {
    }

    @JsonbCreator
    public Attendee(@JsonbProperty("name") String name,
            @JsonbProperty("email") String email) {
        this.name = name;
        this.email = email;
    }

    @JsonbProperty
    public String getId() {
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
