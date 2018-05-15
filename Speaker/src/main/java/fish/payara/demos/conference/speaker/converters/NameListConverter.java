package fish.payara.demos.conference.speaker.converters;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Priority;
import org.eclipse.microprofile.config.spi.Converter;

/**
 *
 * @author Fabio Turizo
 */
@Priority(250)
public class NameListConverter implements Converter<List<String>>{

    @Override
    public List<String> convert(String text) {
        return Arrays.stream(text.split(",")).map(String::trim).collect(Collectors.toList());
    }
    
}
