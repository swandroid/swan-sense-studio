package interdroid.swancore.util.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadablePeriod;

import java.lang.reflect.Type;

public class ChronologyDeserializer implements JsonDeserializer<Chronology> {

    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"};

    @Override
    public Chronology deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context) throws JsonParseException {
//        for (String format : DATE_FORMATS) {
//            try {
//                return new SimpleDateFormat(format, Locale.getDefault()).parse(jsonElement.getAsString());
//            } catch (ParseException e) {
//                //Do nothing
//            }
//        }
//        throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString() + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
        return new Chronology() {
            @Override
            public DateTimeZone getZone() {
                return null;
            }

            @Override
            public Chronology withUTC() {
                return null;
            }

            @Override
            public Chronology withZone(DateTimeZone zone) {
                return null;
            }

            @Override
            public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int millisOfDay) {
                return 0;
            }

            @Override
            public long getDateTimeMillis(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
                return 0;
            }

            @Override
            public long getDateTimeMillis(long instant, int hourOfDay, int minuteOfHour, int secondOfMinute, int millisOfSecond) {
                return 0;
            }

            @Override
            public void validate(ReadablePartial partial, int[] values) {

            }

            @Override
            public int[] get(ReadablePartial partial, long instant) {
                return new int[0];
            }

            @Override
            public long set(ReadablePartial partial, long instant) {
                return 0;
            }

            @Override
            public int[] get(ReadablePeriod period, long startInstant, long endInstant) {
                return new int[0];
            }

            @Override
            public int[] get(ReadablePeriod period, long duration) {
                return new int[0];
            }

            @Override
            public long add(ReadablePeriod period, long instant, int scalar) {
                return 0;
            }

            @Override
            public long add(long instant, long duration, int scalar) {
                return 0;
            }

            @Override
            public DurationField millis() {
                return null;
            }

            @Override
            public DateTimeField millisOfSecond() {
                return null;
            }

            @Override
            public DateTimeField millisOfDay() {
                return null;
            }

            @Override
            public DurationField seconds() {
                return null;
            }

            @Override
            public DateTimeField secondOfMinute() {
                return null;
            }

            @Override
            public DateTimeField secondOfDay() {
                return null;
            }

            @Override
            public DurationField minutes() {
                return null;
            }

            @Override
            public DateTimeField minuteOfHour() {
                return null;
            }

            @Override
            public DateTimeField minuteOfDay() {
                return null;
            }

            @Override
            public DurationField hours() {
                return null;
            }

            @Override
            public DateTimeField hourOfDay() {
                return null;
            }

            @Override
            public DateTimeField clockhourOfDay() {
                return null;
            }

            @Override
            public DurationField halfdays() {
                return null;
            }

            @Override
            public DateTimeField hourOfHalfday() {
                return null;
            }

            @Override
            public DateTimeField clockhourOfHalfday() {
                return null;
            }

            @Override
            public DateTimeField halfdayOfDay() {
                return null;
            }

            @Override
            public DurationField days() {
                return null;
            }

            @Override
            public DateTimeField dayOfWeek() {
                return null;
            }

            @Override
            public DateTimeField dayOfMonth() {
                return null;
            }

            @Override
            public DateTimeField dayOfYear() {
                return null;
            }

            @Override
            public DurationField weeks() {
                return null;
            }

            @Override
            public DateTimeField weekOfWeekyear() {
                return null;
            }

            @Override
            public DurationField weekyears() {
                return null;
            }

            @Override
            public DateTimeField weekyear() {
                return null;
            }

            @Override
            public DateTimeField weekyearOfCentury() {
                return null;
            }

            @Override
            public DurationField months() {
                return null;
            }

            @Override
            public DateTimeField monthOfYear() {
                return null;
            }

            @Override
            public DurationField years() {
                return null;
            }

            @Override
            public DateTimeField year() {
                return null;
            }

            @Override
            public DateTimeField yearOfEra() {
                return null;
            }

            @Override
            public DateTimeField yearOfCentury() {
                return null;
            }

            @Override
            public DurationField centuries() {
                return null;
            }

            @Override
            public DateTimeField centuryOfEra() {
                return null;
            }

            @Override
            public DurationField eras() {
                return null;
            }

            @Override
            public DateTimeField era() {
                return null;
            }

            @Override
            public String toString() {
                return null;
            }
        };
    }


}
