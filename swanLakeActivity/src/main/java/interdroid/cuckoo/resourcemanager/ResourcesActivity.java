package interdroid.cuckoo.resourcemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import interdroid.cuckoo.client.ResourceManager;
import interdroid.cuckoo.client.ResourceManager.Resources;
import interdroid.swan.R;

public class ResourcesActivity extends ListActivity {
	private static final String TAG = "ResourcesActivity";

	// Menu item ids
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_ADD = Menu.FIRST + 1;
	public static final int MENU_ITEM_SHARE = Menu.FIRST + 2;
	public static final int MENU_ITEM_ADD_MANUALLY = Menu.FIRST + 3;
	public static final int MENU_ITEM_DELETE_ALL = Menu.FIRST + 4;

	// dialogs
	public static final int DIALOG_ADD_MANUALLY = 1;

	/**
	 * The columns we are interested in from the database
	 */
	private static final String[] PROJECTION = new String[] { Resources._ID,
			Resources.IDENTIFIER, Resources.HOSTNAME, Resources.UPLOAD,
			Resources.UPLOAD_VARIANCE, Resources.DOWNLOAD,
			Resources.DOWNLOAD_VARIANCE, Resources.BSSIDS,
			Resources.LOCATION_LATITUDE, Resources.LOCATION_LONGITUDE,
			Resources.HUB_ADDRESS };

	/** The index of the title column */
	private static final int COLUMN_INDEX_TITLE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(Resources.CONTENT_URI);
		}

		// Inform the list we provide context menus for items
		getListView().setOnCreateContextMenuListener(this);

		// Perform a managed query. The Activity will handle closing and
		// requerying the cursor
		// when needed.
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null,
				null, Resources.DEFAULT_SORT_ORDER);

		// Used to map notes entries from the database to views
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.cuckoo_resources_list, cursor, new String[] {
						Resources.HOSTNAME, Resources.UPLOAD,
						Resources.DOWNLOAD }, new int[] { R.id.text1,
						R.id.text2, R.id.text3 });
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// This is our one standard application action -- inserting a
		// new note into the list.
		menu.add(0, MENU_ITEM_ADD, 0, R.string.menu_insert)
				.setShortcut('3', 'a').setIcon(android.R.drawable.ic_menu_add);
		menu.add(1, MENU_ITEM_ADD_MANUALLY, 0, "Add Manually").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(1, MENU_ITEM_DELETE_ALL, 0, "Delete All").setIcon(
				android.R.drawable.ic_menu_delete);
		// Generate any additional actions that can be performed on the
		// overall list. In a normal install, there are no additional
		// actions found here, but this allows other applications to extend
		// our menu with their own actions.
		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
				new ComponentName(this, ResourcesActivity.class), null, intent, 0,
				null);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		final boolean haveItems = getListAdapter().getCount() > 0;

		// If there are any notes in the list (which implies that one of
		// them is selected), then we need to generate the actions that
		// can be performed on the current selection. This will be a combination
		// of our own specific actions along with any extensions that can be
		// found.
		if (haveItems) {
			// This is the selected item.
			Uri uri = ContentUris.withAppendedId(getIntent().getData(),
					getSelectedItemId());

			// Build menu... always starts with the EDIT action...
			Intent[] specifics = new Intent[1];
			specifics[0] = new Intent(Intent.ACTION_EDIT, uri);
			MenuItem[] items = new MenuItem[1];

			// ... is followed by whatever other actions are available...
			Intent intent = new Intent(null, uri);
			intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
			menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null,
					specifics, intent, 0, items);

			// Give a shortcut to the edit action.
			if (items[0] != null) {
				items[0].setShortcut('1', 'e');
			}
		} else {
			menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_ADD:
			// Launch activity to insert a new item
			IntentIntegrator.initiateScan(this);
			return true;
		case MENU_ITEM_ADD_MANUALLY:
			showDialog(DIALOG_ADD_MANUALLY);
			return true;
		case MENU_ITEM_DELETE_ALL:
			// delete all
			getContentResolver().delete(Resources.CONTENT_URI,
					"", new String[] {});
			getContentResolver().delete(
					ResourceManager.ResourceBindings.CONTENT_URI, "",
					new String[] {});
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != null) {
			for (int i = 0; i < 1; i++) {
				storeServerInDB(scanResult.getContents());
			}
		}
	}

	private void storeServerInDB(String serverString) {
		// handle scan result, put it in the database
		ContentValues values = new ContentValues();
		String[] contents = serverString.split(";");
		if (contents.length < 9)
			return;

		values.put(Resources.HOSTNAME, contents[0]);
		values.put(Resources.BSSIDS, contents[1]);
		values.put(Resources.UPLOAD,
				Float.parseFloat(contents[2]));
		values.put(Resources.UPLOAD_VARIANCE,
				Float.parseFloat(contents[3]));
		values.put(Resources.DOWNLOAD,
				Float.parseFloat(contents[4]));
		values.put(Resources.DOWNLOAD_VARIANCE,
				Float.parseFloat(contents[5]));
		values.put(Resources.LOCATION_LATITUDE,
				Float.parseFloat(contents[6].split(",")[0]));
		values.put(Resources.LOCATION_LONGITUDE,
				Float.parseFloat(contents[6].split(",")[1]));
		values.put(Resources.HUB_ADDRESS, contents[7]);
		values.put(Resources.IDENTIFIER, contents[8]);

		getContentResolver().insert(Resources.CONTENT_URI,
				values);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}

		// Setup the menu header
		menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

		// Add a menu item to delete the note
		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
		menu.add(1, MENU_ITEM_SHARE, 0, R.string.menu_share);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case MENU_ITEM_DELETE: {
			// Delete the note that the context menu is for
			Uri noteUri = ContentUris.withAppendedId(getIntent().getData(),
					info.id);
			getContentResolver().delete(noteUri, null, null);
			return true;
		}
		case MENU_ITEM_SHARE: {
			// Share the note that the context menu is for
			shareIt(info.id);
			return true;
		}
		}
		return false;
	}

	private void shareIt(long id) {
		Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), id);
		Cursor cursor = getContentResolver().query(noteUri, PROJECTION,
				"_id = ?", new String[] { "" + id }, null);
		if (cursor.moveToFirst()) {
			String shareString = cursor.getString(2) + ";"
					+ cursor.getString(7) + ";" + cursor.getFloat(3) + ";"
					+ cursor.getFloat(4) + ";" + cursor.getFloat(5) + ";"
					+ cursor.getFloat(6) + ";" + cursor.getFloat(8) + ","
					+ cursor.getFloat(9) + ";" + cursor.getString(10) + ";"
					+ cursor.getString(1);
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Share Resource " + cursor.getString(2));
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					shareString);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ADD_MANUALLY:
			final EditText serverStringField = new EditText(this);
			serverStringField.setLines(5);
			return new AlertDialog.Builder(this)
					.setTitle("Enter Server String").setView(serverStringField)
					.setPositiveButton("OK", new Dialog.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							storeServerInDB(serverStringField.getText()
									.toString());
						}
					}).create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

		String action = getIntent().getAction();
		if (Intent.ACTION_PICK.equals(action)
				|| Intent.ACTION_GET_CONTENT.equals(action)) {
			// The caller is waiting for us to return a note selected by
			// the user. The have clicked on one, so return it now.
			setResult(RESULT_OK, new Intent().setData(uri));
		} else {
			// Launch activity to view/edit the currently selected item
		}
	}
}
