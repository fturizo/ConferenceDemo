package fish.payara.demos.conference.ui.entities;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Fabio Turizo
 */
public class Session implements Serializable{
    
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    private String venue;
    @NotNull
    @FutureOrPresent
    private LocalDate date;
    @NotNull
    private Duration duration;
    @NotEmpty
    private List<String> speakers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<String> speakers) {
        this.speakers = speakers;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
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
