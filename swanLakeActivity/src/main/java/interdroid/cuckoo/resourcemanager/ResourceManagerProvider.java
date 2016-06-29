package interdroid.cuckoo.resourcemanager;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import interdroid.cuckoo.client.ResourceManager;

/**
 * Provides access to a database of resources.
 */
public class ResourceManagerProvider extends ContentProvider {

	private static final String TAG = "ResourceManagerProvider";

	private static final String DATABASE_NAME = "resources.db";
	private static final int DATABASE_VERSION = 5;
	private static final String RESOURCES_TABLE_NAME = "resources";
	private static final String RESOURCE_BINDINGS_TABLE_NAME = "resourcebindings";

	private static HashMap<String, String> sResourcesProjectionMap;
	private static HashMap<String, String> sResourceBindingsProjectionMap;

	private static final int RESOURCES = 1;
	private static final int RESOURCE_ID = 2;
	private static final int RESOURCE_BINDINGS = 3;
	private static final int RESOURCE_BINDING_ID = 4;

	private static final UriMatcher sUriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + RESOURCES_TABLE_NAME + " ("
					+ ResourceManager.Resources._ID + " INTEGER PRIMARY KEY,"
					+ ResourceManager.Resources.IDENTIFIER + " TEXT,"
					+ ResourceManager.Resources.HUB_ADDRESS + " TEXT,"
					+ ResourceManager.Resources.HOSTNAME + " TEXT,"
					+ ResourceManager.Resources.BSSIDS + " TEXT,"
					+ ResourceManager.Resources.UPLOAD + " REAL,"
					+ ResourceManager.Resources.UPLOAD_VARIANCE + " REAL,"
					+ ResourceManager.Resources.DOWNLOAD + " REAL,"
					+ ResourceManager.Resources.DOWNLOAD_VARIANCE + " REAL,"
					+ ResourceManager.Resources.LOCATION_LATITUDE + " REAL,"
					+ ResourceManager.Resources.LOCATION_LONGITUDE + " REAL"
					+ ");");
			db.execSQL("CREATE TABLE " + RESOURCE_BINDINGS_TABLE_NAME + " ("
					+ ResourceManager.ResourceBindings._ID
					+ " INTEGER PRIMARY KEY,"
					+ ResourceManager.ResourceBindings.IDENTIFIER + " TEXT,"
					+ ResourceManager.ResourceBindings.BIND_ID + " INTEGER"
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + RESOURCES_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + RESOURCE_BINDINGS_TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case RESOURCE_BINDINGS:
			qb.setTables(RESOURCE_BINDINGS_TABLE_NAME);
			qb.setProjectionMap(sResourceBindingsProjectionMap);
			break;

		case RESOURCE_BINDING_ID:
			qb.setTables(RESOURCE_BINDINGS_TABLE_NAME);
			qb.setProjectionMap(sResourceBindingsProjectionMap);
			qb.appendWhere(ResourceManager.ResourceBindings._ID + "="
					+ uri.getPathSegments().get(2)); // is the get(2) right?
			break;

		case RESOURCES:
			qb.setTables(RESOURCES_TABLE_NAME);
			qb.setProjectionMap(sResourcesProjectionMap);
			break;

		case RESOURCE_ID:
			qb.setTables(RESOURCES_TABLE_NAME);
			qb.setProjectionMap(sResourcesProjectionMap);
			qb.appendWhere(ResourceManager.Resources._ID + "="
					+ uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ResourceManager.Resources.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case RESOURCES:
			return ResourceManager.Resources.CONTENT_TYPE;

		case RESOURCE_ID:
			return ResourceManager.Resources.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) == RESOURCE_BINDINGS) {
			ContentValues values;
			if (initialValues != null) {
				values = new ContentValues(initialValues);
			} else {
				values = new ContentValues();
			}

			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			long rowId = db.insert(RESOURCE_BINDINGS_TABLE_NAME, null, values);
			if (rowId > 0) {
				Uri resourceUri = ContentUris.withAppendedId(
						ResourceManager.ResourceBindings.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(resourceUri,
						null);
				return resourceUri;
			}

			throw new SQLException("Failed to insert row into " + uri);

		} else if (sUriMatcher.match(uri) == RESOURCES) {
			ContentValues values;
			if (initialValues != null) {
				values = new ContentValues(initialValues);
			} else {
				values = new ContentValues();
			}

			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			long rowId = db.insert(RESOURCES_TABLE_NAME, null, values);
			if (rowId > 0) {
				Uri resourceUri = ContentUris.withAppendedId(
						ResourceManager.Resources.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(resourceUri,
						null);
				return resourceUri;
			}

			throw new SQLException("Failed to insert row into " + uri);

		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case RESOURCE_BINDINGS:
			count = db.delete(RESOURCE_BINDINGS_TABLE_NAME, where, whereArgs);
			break;

		case RESOURCE_BINDING_ID:
			String resourceBindingId = uri.getPathSegments().get(2); // get(2)
			// is
			// right?
			count = db.delete(RESOURCE_BINDINGS_TABLE_NAME,
					ResourceManager.ResourceBindings._ID
							+ "="
							+ resourceBindingId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		case RESOURCES:
			count = db.delete(RESOURCES_TABLE_NAME, where, whereArgs);
			break;

		case RESOURCE_ID:
			String resourceId = uri.getPathSegments().get(1);
			count = db.delete(RESOURCES_TABLE_NAME,
					ResourceManager.Resources._ID
							+ "="
							+ resourceId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case RESOURCE_BINDINGS:
			count = db.update(RESOURCE_BINDINGS_TABLE_NAME, values, where,
					whereArgs);
			break;

		case RESOURCE_BINDING_ID:
			String resourceBindingId = uri.getPathSegments().get(1);
			count = db.update(RESOURCE_BINDINGS_TABLE_NAME, values,
					ResourceManager.ResourceBindings._ID
							+ "="
							+ resourceBindingId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case RESOURCES:
			count = db.update(RESOURCES_TABLE_NAME, values, where, whereArgs);
			break;

		case RESOURCE_ID:
			String resourceId = uri.getPathSegments().get(1);
			count = db.update(RESOURCES_TABLE_NAME, values,
					ResourceManager.Resources._ID
							+ "="
							+ resourceId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(ResourceManager.AUTHORITY, "resources/bindings",
				RESOURCE_BINDINGS);
		sUriMatcher.addURI(ResourceManager.AUTHORITY, "resources/bindings/#",
				RESOURCE_BINDING_ID);
		sUriMatcher.addURI(ResourceManager.AUTHORITY, "resources", RESOURCES);
		sUriMatcher.addURI(ResourceManager.AUTHORITY, "resources/#",
				RESOURCE_ID);

		sResourceBindingsProjectionMap = new HashMap<String, String>();
		sResourceBindingsProjectionMap.put(
				ResourceManager.ResourceBindings._ID,
				ResourceManager.ResourceBindings._ID);
		sResourceBindingsProjectionMap.put(
				ResourceManager.ResourceBindings.IDENTIFIER,
				ResourceManager.ResourceBindings.IDENTIFIER);
		sResourceBindingsProjectionMap.put(
				ResourceManager.ResourceBindings.BIND_ID,
				ResourceManager.ResourceBindings.BIND_ID);

		sResourcesProjectionMap = new HashMap<String, String>();
		sResourcesProjectionMap.put(ResourceManager.Resources._ID,
				ResourceManager.Resources._ID);
		sResourcesProjectionMap.put(ResourceManager.Resources.IDENTIFIER,
				ResourceManager.Resources.IDENTIFIER);
		sResourcesProjectionMap.put(ResourceManager.Resources.HOSTNAME,
				ResourceManager.Resources.HOSTNAME);
		sResourcesProjectionMap.put(ResourceManager.Resources.BSSIDS,
				ResourceManager.Resources.BSSIDS);
		sResourcesProjectionMap.put(ResourceManager.Resources.UPLOAD,
				ResourceManager.Resources.UPLOAD);
		sResourcesProjectionMap.put(ResourceManager.Resources.UPLOAD_VARIANCE,
				ResourceManager.Resources.UPLOAD_VARIANCE);
		sResourcesProjectionMap.put(ResourceManager.Resources.DOWNLOAD,
				ResourceManager.Resources.DOWNLOAD);
		sResourcesProjectionMap.put(
				ResourceManager.Resources.DOWNLOAD_VARIANCE,
				ResourceManager.Resources.DOWNLOAD_VARIANCE);
		sResourcesProjectionMap.put(
				ResourceManager.Resources.LOCATION_LATITUDE,
				ResourceManager.Resources.LOCATION_LATITUDE);
		sResourcesProjectionMap.put(
				ResourceManager.Resources.LOCATION_LONGITUDE,
				ResourceManager.Resources.LOCATION_LONGITUDE);
		sResourcesProjectionMap.put(ResourceManager.Resources.HUB_ADDRESS,
				ResourceManager.Resources.HUB_ADDRESS);
	}
}
