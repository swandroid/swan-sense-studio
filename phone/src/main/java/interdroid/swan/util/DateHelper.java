package interdroid.swan.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateHelper {
    private static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
            ISO_8601_DATE_FORMAT, Locale.getDefault());

    public static String getDate(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return FORMATTER.format(calendar.getTime());
    }

    public static long getDateToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getDateTodayMidnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.AM_PM, Calendar.PM);
        cal.set(Calendar.HOUR, 11);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
