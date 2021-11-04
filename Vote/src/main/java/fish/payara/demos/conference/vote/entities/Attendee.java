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
@NamedQuery(name = "Attendee.checkCredentials", 
            query = "select at from Attendee at where at.email = :email and at.password = :password")
@NamedQuery(name = "Attendee.fromEmail", query = "select at from Attendee at where at.email = :email")
@NamedQuery(name = "Attendee.getAll", query = "select at from Attendee at order by at.email")
public class Attendee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    public Attendee() {
    }

    @JsonbCreator
    public Attendee(@JsonbProperty("name") String name,
            @JsonbProperty("email") String email,
            @JsonbProperty("password") String password,
            @JsonbProperty("role") Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
