package fish.payara.demos.conference.ui.util;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.Duration;

/**
 *
 * @author Fabio Turizo
 */
@FacesConverter("java.time.Duration")
public class DurationConverter implements Converter<Duration> {

    @Override
    public Duration getAsObject(FacesContext facesContext, UIComponent uiComponent, String text) {
        return Duration.parse(text);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Duration duration) {
        return duration.toString();
    }
}
