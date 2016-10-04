package interdroid.swancore.util.deserializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.Chronology;

import java.lang.reflect.Type;

public class ChronologySerializer implements JsonSerializer<Chronology> {
    @Override
    public JsonElement serialize(Chronology src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
