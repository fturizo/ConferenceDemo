package fish.payara.demos.conference.speaker.entitites;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Fabio Turizo
 */
public class Venue implements Serializable{
    
    private final Integer id;
    private final String name;

    public Venue(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
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
        final Venue other = (Venue) obj;
        return Objects.equals(this.id, other.id);
    }
}
