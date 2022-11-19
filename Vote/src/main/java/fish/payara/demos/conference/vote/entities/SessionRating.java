package fish.payara.demos.conference.vote.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;

/**
 * 
 * @author Fabio Turizo
 */
@Entity
@NamedQuery(name = "SessionRating.getForSession", 
            query = "select sr from SessionRating sr where sr.sessionId = :id order by sr.rating")
public class SessionRating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String sessionId;
    private String sessionSummary;
    
    private Integer rating;
    
    @ManyToOne
    private Attendee attendee;

    public SessionRating() {
    }
    
    @JsonbCreator
    public SessionRating(@JsonbProperty("sessionId") String sessionId,
                         @JsonbProperty("rating") Integer rating) {
        this.sessionId = sessionId;
        this.rating = rating;
    }
    
    private SessionRating(String sessionId, Integer rating, Attendee attendee) {
        this(sessionId, rating);
        this.attendee = attendee;
    }

    private SessionRating(String sessionId, String sessionSummary, Integer rating, Attendee attendee) {
        this(sessionId, rating, attendee);
        this.sessionSummary = sessionSummary;
    }

    @JsonbProperty
    public String getId() {
        return id;
    }

    @JsonbProperty
    public String getSessionId() {
        return sessionId;
    }

    @JsonbProperty
    public String getSessionSummary() {
        return sessionSummary;
    }

    @JsonbProperty
    public Integer getRating() {
        return rating;
    }
    
    @JsonbProperty
    public String getAttendeeName(){
        return attendee != null ? attendee.getName() : null;
    }

    @JsonbTransient
    public Attendee getAttendee() {
        return attendee;
    }
    
    public SessionRating with(Attendee attendee){
        return new SessionRating(sessionId, rating, attendee);
    }
    
    public SessionRating with(Attendee attendee, String sessionSummary){
        return new SessionRating(sessionId, sessionSummary, rating, attendee);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final SessionRating other = (SessionRating) obj;
        return Objects.equals(this.id, other.id);
    }
}
