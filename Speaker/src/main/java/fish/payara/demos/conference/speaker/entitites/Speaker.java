package fish.payara.demos.conference.speaker.entitites;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author Fabio Turizo
 */
@Entity
@NamedQuery(name = "Speaker.all", query = "select sp from Speaker sp order by sp.name")
@Schema(description = "Stores speaker information")
public class Speaker implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Identifier of the speaker", required = true)
    private String id;
    
    @Schema(description = "Name of the speaker", required = true)
    private String name;
    
    @Schema(description = "Organization that the speaker belongs", required = true)
    private String organization;

    @Schema(description = "Date that the speaker was registered")
    private LocalDate registeredAt;

    public Speaker() {
    }

    @JsonbCreator
    public Speaker(@JsonbProperty("name") String name, @JsonbProperty("organization")  String organization) {
        this.name = name;
        this.organization = organization;
        this.registeredAt = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrganization() {
        return organization;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    @Override
    public int hashCode() {
        var hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @PrePersist
    public void verifyRegisteredDate(){
        if(registeredAt == null){
            this.registeredAt = LocalDate.now();
        }
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
        final Speaker other = (Speaker) obj;
        return Objects.equals(this.id, other.id);
    }
}
