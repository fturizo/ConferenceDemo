package fish.payara.demos.conference.session.entities;

import fish.payara.demos.conference.session.converters.SpeakersConverter;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

/**
 *
 * @author Fabio Turizo
 */
@Entity
@NamedQuery(name = "Session.getForDay", 
            query = "select s from Session s where s.date = :date order by s.title")
public class Session implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String title;
    private String venue;
    private LocalDate date;
    private Duration duration;
    
    @Convert(converter = SpeakersConverter.class)
    private List<String> speakers;

    public Session() {
    }

    @JsonbCreator
    public Session(@JsonbProperty("title") String title,
                   @JsonbProperty("venue") String venue,
                   @JsonbProperty("date") LocalDate date,
                   @JsonbProperty("duration") Duration duration,
                   @JsonbProperty("speakers") List<String> speakers) {
        this.title = title;
        this.date = date;
        this.venue = venue;
        this.duration = duration;
        this.speakers = speakers;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public String getVenue() {
        return venue;
    }

    public LocalDate getDate() {
        return date;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final Session other = (Session) obj;
        return Objects.equals(this.id, other.id);
    }
}
