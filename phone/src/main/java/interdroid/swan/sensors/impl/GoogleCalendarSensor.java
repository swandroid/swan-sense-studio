package interdroid.swan.sensors.impl;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.util.DateHelper;
import interdroid.swancore.models.GoogleEvent;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

public class GoogleCalendarSensor extends AbstractSwanSensor {

    public static final String START_TIME_NEXT_EVENT_FIELD = "start_time_next_event";
    public static final String END_TIME_NEXT_EVENT_FIELD = "end_time_next_event";
    public static final String TITLE_NEXT_EVENT_FIELD = "title_next_event";
    public static final String LOCATION_NEXT_EVENT_FIELD = "location_next_event";

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.TITLE,                              // 0
            CalendarContract.Events.EVENT_LOCATION,                     // 1
            CalendarContract.Events.DTSTART,                            // 2
            CalendarContract.Events.DTEND                               // 3
    };

    public static class ConfigurationActivity extends AbstractConfigurationActivity {
        @Override
        public final int getPreferencesXML() {
            return R.xml.google_calendar_preferences;
        }
    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);

        queryEvents();
    }

    @Override
    public void initDefaultConfiguration(Bundle defaults) {

    }

    @Override
    public void unregister(String id) {

    }

    @Override
    public String[] getValuePaths() {
        return new String[] {
                TITLE_NEXT_EVENT_FIELD,
                LOCATION_NEXT_EVENT_FIELD,
                START_TIME_NEXT_EVENT_FIELD,
                END_TIME_NEXT_EVENT_FIELD
        };
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Google Calendar Sensor";
    }

    @Override
    public String getModelClassName() {
        return GoogleEvent.class.getName();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return GoogleEvent.class.getConstructors()[1].getParameterTypes();
    }

    private void queryEvents() {
        // Run query
        Cursor cursor = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + " <= ?";
        String[] selectionArgs = new String[]{String.valueOf(DateHelper.getDateToday()), String.valueOf(DateHelper.getDateTodayMidnight())};

//        System.out.println(DateHelper.getDate(DateHelper.getDateToday()));
//        System.out.println(DateHelper.getDate(DateHelper.getDateTodayMidnight()));
//        cursor = cr.query(uri, EVENT_PROJECTION, null, null, null);
        cursor = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        if (cursor != null) {
            while (cursor.moveToNext()) {
                System.out.println(cursor.getString(0));
                System.out.println(cursor.getString(1));
                System.out.println(DateHelper.getDate(cursor.getLong(2)));
                System.out.println(DateHelper.getDate(cursor.getLong(3)));

                long now = System.currentTimeMillis();
                putValueTrimSize(TITLE_NEXT_EVENT_FIELD, null, now, cursor.getString(0));
                putValueTrimSize(LOCATION_NEXT_EVENT_FIELD, null, now, cursor.getString(1));
                putValueTrimSize(START_TIME_NEXT_EVENT_FIELD, null, now, DateHelper.getDate(cursor.getLong(2)));
                putValueTrimSize(END_TIME_NEXT_EVENT_FIELD, null, now, DateHelper.getDate(cursor.getLong(3)));

//                putValueTrimSize(EVENT, null, System.currentTimeMillis(),
//                        new GoogleEvent(cursor.getString(0), cursor.getString(1),
//                                DateHelper.getDate(cursor.getLong(2)), DateHelper.getDate(cursor.getLong(3))));
            }
            cursor.close();
        }
    }
}
