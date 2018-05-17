package fish.payara.demos.conference.session.entities;

import fish.payara.demos.conference.session.converters.SpeakersConverter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Convert;
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
@NamedQuery(name = "Session.getForDay", 
            query = "select s from Session s where s.schedule.date = :date order by s.title")
public class Session implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String title;
    
    @Convert(converter = SpeakersConverter.class)
    private List<String> speakers;
    
    @ManyToOne
    private Schedule schedule;

    public Session() {
    }

    @JsonbCreator
    public Session(@JsonbProperty("title") String title, @JsonbProperty("speakers") List<String> speakers) {
        this.title = title;
        this.speakers = speakers;
    }

    private Session(String title, List<String> speakers, Schedule schedule) {
        this(title, speakers);
        this.schedule = schedule;
    }

    @JsonbProperty
    public Integer getId() {
        return id;
    }

    @JsonbProperty
    public String getTitle() {
        return title;
    }

    @JsonbProperty
    public List<String> getSpeakers() {
        return speakers;
    }
    
    @JsonbProperty
    public LocalDate getScheduledAt(){
        return schedule.getDate();
    }
    
    @JsonbProperty
    public String getVenue(){
        return schedule.getVenue();
    }
    
    public Session with(Schedule schedule){
        return new Session(title, speakers, schedule);
    }
}
