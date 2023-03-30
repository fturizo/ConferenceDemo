package fish.payara.demos.conference.ui.entities;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Fabio Turizo
 */
public class Speaker implements Serializable{
    
    @NotEmpty
    private String name;

    @NotEmpty
    private String organization;

    private LocalDate registeredAt;

    public Speaker() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }
}
