package fish.payara.demos.conference.session.converters;

import java.time.Duration;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 *
 * @author Fabio Turizo
 */
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String>{

    @Override
    public String convertToDatabaseColumn(Duration value) {
        return value.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String text) {
        return Duration.parse(text);
    }
}
