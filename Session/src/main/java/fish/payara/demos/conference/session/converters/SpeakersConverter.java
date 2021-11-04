package fish.payara.demos.conference.session.converters;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 *
 * @author fabio
 */
@Converter
public class SpeakersConverter implements AttributeConverter<List<String>, String>{

    private static final String SEPARATOR = ";";
    
    @Override
    public String convertToDatabaseColumn(List<String> names) {
       return names.stream().collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public List<String> convertToEntityAttribute(String text) {
        return Arrays.stream(text.split(SEPARATOR)).collect(Collectors.toList());
    }
}
