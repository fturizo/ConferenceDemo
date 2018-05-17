/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fish.payara.demos.conference.vote.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
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
    public SessionRating(@JsonbProperty("session-id") Integer sessionId, 
                         @JsonbProperty("summary") String sessionSummary, 
                         @JsonbProperty("rating") Integer rating) {
        this.sessionId = sessionId;
        this.sessionSummary = sessionSummary;
        this.rating = rating;
    }

    private SessionRating(Integer sessionId, String sessionSummary, Integer rating, Attendee attendee) {
        this(sessionId, sessionSummary, rating);
        this.attendee = attendee;
    }

    @JsonbProperty
    public Integer getId() {
        return id;
    }

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
        return attendee.getName();
    }

    public Attendee getAttendee() {
        return attendee;
    }
    
    public SessionRating with(Attendee attendee){
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