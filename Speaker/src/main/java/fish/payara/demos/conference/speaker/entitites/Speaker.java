package fish.payara.demos.conference.speaker.entitites;

import java.io.Serializable;
import java.util.Objects;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifier of the speaker", required = true)
    private Integer id;
    
    @Schema(description = "Name of the speaker", required = true)
    private String name;
    
    @Schema(description = "Organization that the speaker belongs", required = true)
    private String organization;

    public Speaker() {
    }

    @JsonbCreator
    public Speaker(@JsonbProperty("name") String name,@JsonbProperty("organization")  String organization) {
        this.name = name;
        this.organization = organization;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrganization() {
        return organization;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final Speaker other = (Speaker) obj;
        return Objects.equals(this.id, other.id);
    }
}
