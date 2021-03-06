package nl.sense_os.service.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import nl.sense_os.service.constants.SensorData.DataPoint;

/**
 * Helper class that assist in creating, opening and managing the SQLite3 database for data points.
 */
public class DbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database on the disk
     */
    private static final String DATABASE_NAME = "persistent_storage.sqlite3";

    /**
     * Version of the database. Increment this when the database structure is changed.
     */
    private static final int DATABASE_VERSION = 4;

    /**
     * Name of the table with the data points.
     */
    static final String TABLE = "persisted_values";

    private static final String TAG = "DbHelper";

    /**
     * Constructor. The database is not actually created or opened until one of
     * {@link #getWritableDatabase()} or {@link #getReadableDatabase()} is called.
     *
     * @param context to use to open or create the database
     */
    public DbHelper(Context context) {
        // if the database name is null, it will be created in-memory
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final StringBuilder sb = new StringBuilder("CREATE TABLE " + TABLE + "(");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT");
        sb.append(", " + DataPoint.SENSOR_NAME + " TEXT");    //sensor name will be the table's name
        sb.append(", " + DataPoint.DISPLAY_NAME + " TEXT");
        sb.append(", " + DataPoint.SENSOR_DESCRIPTION + " TEXT");
        sb.append(", " + DataPoint.VALUE_PATH + " TEXT");
        sb.append(", " + DataPoint.DATA_TYPE + " TEXT");
        sb.append(", " + DataPoint.TIMESTAMP + " INTEGER");
        sb.append(", " + DataPoint.VALUE + " TEXT");
        sb.append(", " + DataPoint.DEVICE_UUID + " TEXT");
        sb.append(", " + DataPoint.TRANSMIT_STATE + " INTEGER");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVers, int newVers) {
        Log.w(TAG, "Upgrading '" + DATABASE_NAME + "' database from version " + oldVers + " to " + newVers + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
