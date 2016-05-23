/**************************************************************************************************
 * Copyright (C) 2010 Sense Observation Systems, Rotterdam, the Netherlands. All rights reserved. *
 *************************************************************************************************/
package nl.sense_os.service.storage;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.nio.BufferOverflowException;
import java.util.ArrayList;

import nl.sense_os.service.R;
import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensePrefs.Main;
import nl.sense_os.service.constants.SensorData.DataPoint;
import nl.sense_os.service.provider.SNTP;

/**
 * Storage for recent sensor data. The data is initially stored in the device's RAM memory. In case
 * the memory becomes too full, the data is offloaded into a persistent database in the flash
 * memory. This process is hidden to the end user, so you do not have to worry about which data is
 * where.
 *
 * @author Steven Mulder <steven@sense-os.nl>
 * @see ParserUtils
 * @see DataPoint
 */
public class LocalStorage {

    /**
     * Minimum time to retain data points. If data is not sent to CommonSense, it will be retained
     * longer.
     */
    private static final long RETENTION_TIME = 1000l * 60 * 60 * 24;

    /**
     * Default projection for rows of data points
     */
    private static final String[] DEFAULT_PROJECTION = new String[]{BaseColumns._ID,
            DataPoint.SENSOR_NAME, DataPoint.DISPLAY_NAME, DataPoint.SENSOR_DESCRIPTION, DataPoint.VALUE_PATH,
            DataPoint.DATA_TYPE, DataPoint.VALUE, DataPoint.TIMESTAMP, DataPoint.DEVICE_UUID,
            DataPoint.TRANSMIT_STATE};

    private static final int LOCAL_VALUES_URI = 1;
    private static final int REMOTE_VALUES_URI = 2;

    private static final String TAG = "LocalStorage";

    private static final int DEFAULT_LIMIT = 10000;

    private static LocalStorage instance;

    /**
     * @param context Context for lazy creating the LocalStorage.
     * @return Singleton instance of the LocalStorage
     */
    public static LocalStorage getInstance(Context context) {
        if (null == instance) {
            instance = new LocalStorage(context.getApplicationContext());
            Log.d(TAG, " Local storage has not been created yet");
        }
        return instance;
    }

    private final RemoteStorage commonSense;
    private final SQLiteStorage persisted;

    private Context context;

    private LocalStorage(Context context) {
        Log.i(TAG, "Construct new local storage instance");
        this.context = context;
        persisted = new SQLiteStorage(context);
        commonSense = new RemoteStorage(context);
    }

    public int delete(Uri uri, String where, String[] selectionArgs) {
        switch (matchUri(uri)) {
            case LOCAL_VALUES_URI:
                int nrDeleted = 0;
                nrDeleted += persisted.delete(where, selectionArgs);
                return nrDeleted;
            case REMOTE_VALUES_URI:
                throw new IllegalArgumentException("Cannot delete values from CommonSense!");
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * Removes old data from the persistent storage.
     *
     * @return The number of data points deleted
     */
    private int deleteOldData() {
        Log.i(TAG, "Delete old data points from persistent storage");

        // set max retention time
        long retentionLimit = SNTP.getInstance().getTime() - RETENTION_TIME;

        // check preferences to see if the data needs to be sent to CommonSense
        SharedPreferences prefs = context.getSharedPreferences(SensePrefs.MAIN_PREFS,
                Context.MODE_PRIVATE);
        boolean useCommonSense = prefs.getBoolean(Main.Advanced.USE_COMMONSENSE, true);

        String where = null;
        if (useCommonSense) {
            // delete data older than maximum retention time if it had been transmitted
            where = DataPoint.TIMESTAMP + "<" + retentionLimit + " AND " + DataPoint.TRANSMIT_STATE
                    + "==1";
        } else {
            // not using CommonSense: delete all data older than maximum retention time
            where = DataPoint.TIMESTAMP + "<" + retentionLimit;
        }
        int deleted = persisted.delete(where, null);

        return deleted;
    }

    public String getType(Uri uri) {
        int uriType = matchUri(uri);
        if (uriType == LOCAL_VALUES_URI || uriType == REMOTE_VALUES_URI) {
            return DataPoint.CONTENT_TYPE;
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues values) {

        // check the URI
        switch (matchUri(uri)) {
            case LOCAL_VALUES_URI:
                // implementation below
                break;
            case REMOTE_VALUES_URI:
                throw new IllegalArgumentException(
                        "Cannot insert into CommonSense through this ContentProvider");
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // insert in the database
        long rowId = 0;
        try {
            rowId = persisted.insert(values);
        } catch (BufferOverflowException e) {
            // storage is full!
            deleteOldData();

            // try again
            rowId = persisted.insert(values);
        }

        // notify any listeners (does this work properly?)
        Uri contentUri = Uri.parse("content://"
                + context.getString(R.string.local_storage_authority) + DataPoint.CONTENT_URI_PATH);
        Uri rowUri = ContentUris.withAppendedId(contentUri, rowId);
        context.getContentResolver().notifyChange(rowUri, null);

        return rowUri;
    }

    public int bulkInsert(ArrayList<ContentValues> values) {
        return persisted.bulkInsert2(values);
    }

    private int matchUri(Uri uri) {
        if (DataPoint.CONTENT_URI_PATH.equals(uri.getPath())) {
            return LOCAL_VALUES_URI;
        } else if (DataPoint.CONTENT_REMOTE_URI_PATH.equals(uri.getPath())) {
            return REMOTE_VALUES_URI;
        } else {
            return -1;
        }
    }

    public Cursor query(Uri uri, String[] projection, String where, String[] selectionArgs,
                        String sortOrder) {
        return query(uri, projection, where, selectionArgs, DEFAULT_LIMIT, sortOrder);
    }

    public Cursor query(Uri uri, String[] projection, String where, String[] selectionArgs,
                        int limit, String sortOrder) {
        // check URI
        switch (matchUri(uri)) {
            case LOCAL_VALUES_URI:
                // implementation below
                break;
            case REMOTE_VALUES_URI:
                try {
                    return commonSense.query(uri, projection, where, selectionArgs, limit, sortOrder);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to query the CommonSense data points", e);
                    return null;
                }
            default:
                Log.e(TAG, "Unknown URI: " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // use default projection if needed
        if (projection == null) {
            projection = DEFAULT_PROJECTION;
        }

        Cursor persistedCursor = persisted.query(projection, where, selectionArgs, sortOrder);
        if (persistedCursor.getCount() == 0) {
            persistedCursor.close();
            return null;
        }
        return persistedCursor;
    }

    public int update(Uri uri, ContentValues newValues, String where, String[] selectionArgs) {

        // check URI
        switch (matchUri(uri)) {
            case LOCAL_VALUES_URI:
                return persisted.update(newValues, where, selectionArgs);
            case REMOTE_VALUES_URI:
                throw new IllegalArgumentException("Cannot update data points in CommonSense");
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
