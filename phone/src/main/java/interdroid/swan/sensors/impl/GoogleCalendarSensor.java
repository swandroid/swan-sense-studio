package interdroid.swan.sensors.impl;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swan.util.DateHelper;
import interdroid.swancore.models.GoogleEvent;
import interdroid.swancore.sensors.AbstractConfigurationActivity;

public class GoogleCalendarSensor extends AbstractSwanSensor {

    public static final String EVENTS = "events";

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
        return new String[] {EVENTS};
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Google Calendar Sensor";
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
        cursor = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        List<GoogleEvent> events = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
//                System.out.println(cursor.getString(0));
//                System.out.println(cursor.getString(1));
//                System.out.println(DateHelper.getDate(cursor.getLong(2)));
//                System.out.println(DateHelper.getDate(cursor.getLong(3)));


                GoogleEvent event = new GoogleEvent(cursor.getString(0), cursor.getString(1),
                        DateHelper.getDate(cursor.getLong(2)), DateHelper.getDate(cursor.getLong(3)));
                events.add(event);

            }

            cursor.close();
        }

        long now = System.currentTimeMillis();
        putValueTrimSize(EVENTS, null, now, events);
    }
}
