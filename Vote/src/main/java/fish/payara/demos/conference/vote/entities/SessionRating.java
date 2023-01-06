package fish.payara.demos.conference.vote.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

/**
 * 
 * @author Fabio Turizo
 */
@Entity
@NamedQuery(name = "SessionRating.getForSession", 
            query = "select sr from SessionRating sr where sr.sessionId = :id order by sr.rating")
public class SessionRating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer sessionId;
    private String sessionSummary;
    
    private Integer rating;
    
    @ManyToOne
    private Attendee attendee;

    public SessionRating() {
    }
    
    @JsonbCreator
    public SessionRating(@JsonbProperty("sessionId") Integer sessionId,
                         @JsonbProperty("rating") Integer rating) {
        this.sessionId = sessionId;
        this.rating = rating;
    }
    
    private SessionRating(Integer sessionId, Integer rating, Attendee attendee) {
        this(sessionId, rating);
        this.attendee = attendee;
    }

    private SessionRating(Integer sessionId, String sessionSummary, Integer rating, Attendee attendee) {
        this(sessionId, rating, attendee);
        this.sessionSummary = sessionSummary;
    }

    @JsonbProperty
    public Integer getId() {
        return id;
    }

    @JsonbProperty
    public Integer getSessionId() {
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
