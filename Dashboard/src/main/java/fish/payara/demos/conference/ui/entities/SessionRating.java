package fish.payara.demos.conference.ui.entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Fabio Turizo
 */
public class SessionRating implements Serializable {

    private String id;
    private String sessionId;
    private String sessionSummary;
    private Integer rating;

    @JsonbCreator
    public SessionRating(@JsonbProperty("id") String id,
                         @JsonbProperty("sessionId") String sessionId,
                         @JsonbProperty("sessionSummary") String sessionSummary,
                         @JsonbProperty("rating") Integer rating) {
        this.id = id;
        this.sessionId = sessionId;
        this.sessionSummary = sessionSummary;
        this.rating = rating;
    }

    public SessionRating(Session session, Integer rating) {
        this.sessionId = session.getId();
        this.sessionSummary = session.getTitle();
        this.rating = rating;
    }

    public String getId() {
        return id;
    }
    public String getSessionId() {
        return sessionId;
    }
    public String getSessionSummary() {
        return sessionSummary;
    }
    public Integer getRating() {
        return rating;
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
