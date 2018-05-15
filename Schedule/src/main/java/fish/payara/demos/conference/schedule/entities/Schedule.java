package fish.payara.demos.conference.schedule.entities;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 *
 * @author Fabio Turizo
 */
@Entity
@NamedQuery(name = "Schedule.getAll", query = "select sch from Schedule sch order by sch.date")
public class Schedule implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String venue;
    private Integer venueId;
    
    private LocalDate date;
    private Duration duration;

    public Schedule() {
    }

    @JsonbCreator
    public Schedule(@JsonbProperty("venue-id") Integer venueId, 
                    @JsonbProperty("venue") String venue, 
                    @JsonbProperty("date") LocalDate date, 
                    @JsonbProperty("duration") Duration duration) {
        this.venue = venue;
        this.venueId = venueId;
        this.date = date;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public String getVenue() {
        return venue;
    }

    @JsonbProperty("venue-id")
    public Integer getVenueId() {
        return venueId;
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
        hash = 61 * hash + Objects.hashCode(this.id);
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
        final Schedule other = (Schedule) obj;
        return Objects.equals(this.id, other.id);
    }
}
