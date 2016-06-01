package interdroid.swan.crossdevice.swanplus;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;

import interdroid.sense.LoginActivity;
import interdroid.sense.RegistrationActivity;
import interdroid.sense.SettingsActivity;
import interdroid.swan.R;
import interdroid.swancore.crossdevice.Registry;
import interdroid.swan.crossdevice.SwanGCMConstants;
import interdroid.swan.crossdevice.swanplus.bluetooth.BTManager;
import interdroid.swan.crossdevice.swanplus.run2gether.ActivityRun2gether;
import interdroid.swan.swansong.Expression;
import interdroid.swan.ttn.TtnActivity;
import nl.sense_os.service.constants.SensePrefs;


/**
 * Created by vladimir on 9/8/15.
 * <p/>
 * TODO implement mechanism from removing nearby nodes when they are no longer around
 * TODO if peers are already connected when starting the app, they should request user details from each other
 * TODO if disconnected, attempting to unregister from remote will fail
 * TODO when disconnected, unset the IPs of the disconnected peer
 * TODO disable "Invitation to connect" popup when phone is connecting to group
 */
public class SwanLakePlusActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = "SwanLakePlusActivity";
    public static final int DIALOG_SET_NAME = 1;

    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ListFragment mContactsFragment = new ContactsListFragment();
    private ListFragment mNearbyPeersFragment = new NearbyPeersListFragment();
    private ViewPager mViewPager;

    private static ProximityManagerI mProximityManager;
    private static RegisteredSWANsAdapter mContactsAdapter;
    private static NearbySWANsAdapter mNearbyPeersAdapter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BTManager.ACTION_NEARBY_DEVICE_FOUND.equals(action)) {
                getNearbyPeersAdapter().notifyDataSetChanged();
            }
        }
    };

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return mContactsFragment;

                case 1:
                    return mNearbyPeersFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Contacts";

                case 1:
                    return "Nearby Peers";

                default:
                    return "";
            }
        }
    }

    public static class NearbyPeersListFragment extends ListFragment {

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            final ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.swanlake_nearby_peers_bar, menu);
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_save:
                            SparseBooleanArray array = getListView().getCheckedItemPositions();
                            SwanLakePlusActivity activity = (SwanLakePlusActivity) getActivity();
                            ProximityManagerI proximityManager = activity.getProximityManager();

                            for (int i = 0; i < proximityManager.getPeerCount(); i++) {
                                final SwanUser neighbor = proximityManager.getPeerAt(i);

                                if (array.get(i)) {
                                    if (!Registry.add(getActivity(), neighbor.getUsername(), neighbor.getRegId())) {
                                        Toast.makeText(getActivity(),
                                                "Duplicate '" + neighbor.getUsername() + "', to be implemented",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(),
                                                neighbor.getUsername() + " added to Contacts",
                                                Toast.LENGTH_LONG).show();
                                        mContactsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                    }
                    mode.finish();
                    return true;
                }

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    mode.setTitle(getListView().getCheckedItemCount() + " selected");
                }
            });
        }
    }

    public static class ContactsListFragment extends ListFragment {

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            final ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.swanlake_cab, menu);
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            SparseBooleanArray array = getListView().getCheckedItemPositions();
                            List<String> names = Registry.getNames(getActivity());

                            for (int i = 0; i < names.size(); i++) {
                                if (array.get(i)) {
                                    Registry.remove(getActivity(), names.get(i));
                                }
                            }

                            mContactsAdapter.notifyDataSetChanged();
                    }
                    mode.finish();
                    return true;
                }

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    mode.setTitle(getListView().getCheckedItemCount() + " selected");
                }
            });
        }
    }

    class RegisteredSWANsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Registry.getNames(SwanLakePlusActivity.this).size();
        }

        @Override
        public Object getItem(int position) {
            return Registry.getNames(SwanLakePlusActivity.this).get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SwanLakePlusActivity.this)
                        .inflate(android.R.layout.simple_list_item_multiple_choice, null);
            }

            TypedArray ta = SwanLakePlusActivity.this.obtainStyledAttributes(new int[]{android.R.attr.activatedBackgroundIndicator});
            convertView.setBackgroundDrawable(ta.getDrawable(0));
            ta.recycle();

            ((CheckedTextView) (convertView.findViewById(android.R.id.text1)))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContactsFragment.getListView().setItemChecked(position,
                                    !((CheckedTextView) v).isChecked());
                        }
                    });
            ((TextView) (convertView.findViewById(android.R.id.text1)))
                    .setText(getItem(position).toString());
            ((TextView) (convertView.findViewById(android.R.id.text1)))
                    .setPadding(20, 20, 20, 20);
            return convertView;
        }
    }

    class NearbySWANsAdapter extends BaseAdapter {

        private ProximityManagerI mProximityManager;

        public NearbySWANsAdapter(ProximityManagerI proximityManager) {
            this.mProximityManager = proximityManager;
        }

        @Override
        public int getCount() {
            return mProximityManager.getPeerCount();
        }

        @Override
        public Object getItem(int position) {
            return mProximityManager.getPeerAt(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SwanLakePlusActivity.this)
                        .inflate(android.R.layout.simple_list_item_multiple_choice, null);
            }

            TypedArray ta = SwanLakePlusActivity.this.obtainStyledAttributes(new int[]{android.R.attr.activatedBackgroundIndicator});
            convertView.setBackgroundDrawable(ta.getDrawable(0));
            ta.recycle();

            ((CheckedTextView) (convertView.findViewById(android.R.id.text1)))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNearbyPeersFragment.getListView().setItemChecked(position, !((CheckedTextView) v).isChecked());
                        }
                    });
            ((TextView) (convertView.findViewById(android.R.id.text1)))
                    .setText(getItem(position).toString());
            ((TextView) (convertView.findViewById(android.R.id.text1)))
                    .setPadding(20, 20, 20, 20);
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swanlakeplus);

        // Create the adapter that will return a fragment for each section of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        IntentFilter intentFilter = new IntentFilter(BTManager.ACTION_NEARBY_DEVICE_FOUND);
        registerReceiver(mReceiver, intentFilter);

        // setup proximity manager
//        mProximityManager = new WDManager(this);
        mProximityManager = new BTManager(this);
        mProximityManager.init();

        mContactsAdapter = new RegisteredSWANsAdapter();
        mNearbyPeersAdapter = new NearbySWANsAdapter(mProximityManager);
        mContactsFragment.setListAdapter(mContactsAdapter);
        mNearbyPeersFragment.setListAdapter(mNearbyPeersAdapter);

        onNewIntent(getIntent());
//      Registry.removeAll(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProximityManager.clean();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final EditText mNameEditText = new EditText(this);
        mNameEditText.setPadding(10, 10, 10, 10);
        mNameEditText.setText(PreferenceManager.getDefaultSharedPreferences(
                SwanLakePlusActivity.this).getString("name", "SWAN-" + System.currentTimeMillis()));
        return new AlertDialog.Builder(this)
                .setTitle("Choose a name for your device")
                .setView(mNameEditText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mNameEditText.getText().toString().contains(":")) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(
                                            SwanLakePlusActivity.this,
                                            "Character ':' is not allowed in the name, pick another name.",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }

                        PreferenceManager.getDefaultSharedPreferences(SwanLakePlusActivity.this)
                                .edit()
                                .putString("name", mNameEditText.getText().toString())
                                .commit();
                        mProximityManager.registerService();
                    }
                }).create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.swanlake, menu);
        ((Switch) (menu.findItem(R.id.action_enable).getActionView()))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked && Registry.get(SwanLakePlusActivity.this, Expression.LOCATION_SELF) == null) {
                            // first time, we should register with gcm in the background now.
                            registerBackground((Switch) buttonView);
                        } else {
                            PreferenceManager
                                    .getDefaultSharedPreferences(SwanLakePlusActivity.this).edit()
                                    .putBoolean("enabled", isChecked).commit();
                        }
                    }
                });

        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
//              updateNFC();
                break;
            case R.id.action_set_name:
                showDialog(DIALOG_SET_NAME);
                break;
            case R.id.login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.logout:
                // clear cached settings of the previous user (e.g. device id)
                SharedPreferences.Editor authEditor = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE).edit();
                authEditor.clear();
                authEditor.commit();
                // update UI
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(SwanLakePlusActivity.this, R.string.logout_success, Toast.LENGTH_LONG)
                                .show();
                    }
                });

                break;
            case R.id.signup:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_discovery:
                mProximityManager.discoverPeers();
                break;
            case R.id.action_test:
                startActivity(new Intent(this, TestActivity.class));
                break;
            case R.id.action_r2g:
                startActivity(new Intent(this, ActivityRun2gether.class));
                break;
            case R.id.action_ttn:
                startActivity(new Intent(this, TtnActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void registerBackground(final Switch switchWidget) {
        switchWidget.setEnabled(false);
        new Thread() {
            public void run() {
                try {
                    if (SwanGCMConstants.API_KEY.equals(SwanGCMConstants.EMPTY)
                            || SwanGCMConstants.SENDER_ID
                            .equals(SwanGCMConstants.EMPTY)) {
                        throw new RuntimeException("Please provide valid values in SwanGCMConstants");

                    }

                    Registry.add(SwanLakePlusActivity.this, Expression.LOCATION_SELF, GoogleCloudMessaging
                            .getInstance(SwanLakePlusActivity.this).register(SwanGCMConstants.SENDER_ID));
                    PreferenceManager.getDefaultSharedPreferences(SwanLakePlusActivity.this)
                            .edit().putBoolean("enabled", true).commit();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log("Got a registration ID: " + Registry.get(SwanLakePlusActivity.this, Expression.LOCATION_SELF), true);
                        }
                    });

                    mProximityManager.registerService();

                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switchWidget.setChecked(false);
                            log("Failed to register with Google Cloud Messaging (check internet connection)", true);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switchWidget.setEnabled(true);
                    }
                });
            }
        }.start();
    }

    public static ProximityManagerI getProximityManager() {
        return mProximityManager;
    }

    public BaseAdapter getNearbyPeersAdapter() {
        return mNearbyPeersAdapter;
    }

    public void log(String message, boolean display) {
        Log.d(TAG, message);

        if (display) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
