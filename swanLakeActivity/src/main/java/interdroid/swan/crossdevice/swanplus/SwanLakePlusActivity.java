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
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.sense.LoginActivity;
import interdroid.sense.RegistrationActivity;
import interdroid.sense.SettingsActivity;
import interdroid.swan.R;
import interdroid.swan.crossdevice.Registry;
import interdroid.swan.crossdevice.SwanGCMConstants;
import interdroid.swan.swansong.Expression;
import nl.sense_os.service.constants.SensePrefs;


/**
 * Created by vladimir on 9/8/15.
 *
 * TODO implement mechanism from removing nearby nodes when they are no longer around
 */
public class SwanLakePlusActivity extends FragmentActivity implements WDPeerToPeerI, ActionBar.TabListener {

    private static final String TAG = "SwanLakePlusActivity";
    private final int PEER_DISCOVERY_INTERVAL = 10000;
    private static final int DIALOG_SET_NAME = 1;

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    ListView listView;

    ListFragment mContactsFragment = new ContactsListFragment();
    ListFragment mNearbyPeersFragment = new NearbyPeersListFragment();

    public static WifiP2pManager p2pManager;

    private static WifiP2pManager.Channel p2pChannel;
    private BroadcastReceiver p2pReceiver;
    private IntentFilter p2pIntentFilter;
    private Handler handler;
    private WifiP2pDnsSdServiceInfo serviceInfo;
    private WifiDirectAutoAccept wdAutoAccept;

    public static List<SwanUser> nearbyPeers = new ArrayList<SwanUser>();
    private static RegisteredSWANsAdapter mContactsAdapter;
    private static NearbySWANsAdapter mNearbyPeersAdapter;

    /* we schedule peer discovery to take place at regular intervals */
    Runnable nearbyPeersChecker = new Runnable() {
        public void run() {
            discover();
            handler.postDelayed(nearbyPeersChecker, PEER_DISCOVERY_INTERVAL);
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

        public static final String ARG_SECTION_NUMBER = "section_number";

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

                            for (int i = 0; i < nearbyPeers.size(); i++) {
                                final String username = nearbyPeers.get(i).getUsername();
                                final String regId = nearbyPeers.get(i).getRegId();

                                if (array.get(i)) {
                                    if (!Registry.add(getActivity(), username, regId)) {
                                        // pop up duplicate dialog
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),
                                                        "Duplicate '" + username + "', to be implemented",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(),
                                                username + " added to Contacts",
                                                Toast.LENGTH_LONG).show();
                                        mContactsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

//                            ((SwanLakePlusActivity) getActivity()).getNearbyPeersAdapter().notifyDataSetChanged();
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

        public static final String ARG_SECTION_NUMBER = "section_number";

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
                convertView = LayoutInflater
                        .from(SwanLakePlusActivity.this)
                        .inflate(
                                android.R.layout.simple_list_item_multiple_choice,
                                null);
            }
            TypedArray ta = SwanLakePlusActivity.this
                    .obtainStyledAttributes(new int[]{android.R.attr.activatedBackgroundIndicator});
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

        @Override
        public int getCount() {
            return nearbyPeers.size();
        }

        @Override
        public Object getItem(int position) {
            return nearbyPeers.get(position);
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

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
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

        mContactsAdapter = new RegisteredSWANsAdapter();
        mNearbyPeersAdapter = new NearbySWANsAdapter();
        mContactsFragment.setListAdapter(mContactsAdapter);
        mNearbyPeersFragment.setListAdapter(mNearbyPeersAdapter);

        onNewIntent(getIntent());

        /* initialize WifiDirect service */
        p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        p2pChannel = p2pManager.initialize(this, getMainLooper(), null);
        p2pReceiver = new WDBroadcastReceiver(p2pManager, p2pChannel, this);
        p2pIntentFilter = new IntentFilter();
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wdAutoAccept = new WifiDirectAutoAccept(this);
        handler = new Handler();

        initPeersDiscovery();
        registerService();

//        Registry.removeAll(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(p2pReceiver, p2pIntentFilter);
        wdAutoAccept.intercept(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(p2pReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wdAutoAccept.intercept(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wdAutoAccept.intercept(false);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final EditText mNameEditText = new EditText(this);
        mNameEditText.setPadding(10, 10, 10, 10);
        mNameEditText.setText(PreferenceManager.getDefaultSharedPreferences(
                SwanLakePlusActivity.this).getString("name",
                "SWAN-" + System.currentTimeMillis()));
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

                        PreferenceManager
                                .getDefaultSharedPreferences(
                                        SwanLakePlusActivity.this)
                                .edit()
                                .putString("name",
                                        mNameEditText.getText().toString())
                                .commit();
                        registerService();
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
                        if (isChecked
                                && Registry.get(SwanLakePlusActivity.this,
                                Expression.LOCATION_SELF) == null) {
                            // first time, we should register with gcm in the
                            // background now.
                            registerBackground((Switch) buttonView);
                        } else {
                            PreferenceManager
                                    .getDefaultSharedPreferences(
                                            SwanLakePlusActivity.this).edit()
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerService() {
        //  Create a string map containing information about your service.
        Map<String, String> userAttribMap = new HashMap<String, String>();
        String regId = Registry.get(this, Expression.LOCATION_SELF);

        if (regId == null) {
            Log.d(TAG,
                    "Not registered with Google Cloud Messaging, cannot share");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            SwanLakePlusActivity.this,
                            "Not registered with Google Cloud Messaging, cannot share",
                            Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        String userFriendlyName = PreferenceManager
                .getDefaultSharedPreferences(SwanLakePlusActivity.this)
                .getString("name", null);
        if (userFriendlyName == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SwanLakePlusActivity.this,
                            "Please set a name for your device",
                            Toast.LENGTH_SHORT).show();
                    showDialog(DIALOG_SET_NAME);
                }
            });
            return;
        }

        userAttribMap.put("name", userFriendlyName);
        userAttribMap.put("regId", regId);

        if(serviceInfo != null) {
            unregisterService();
        }

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("run2gether", "presence", userAttribMap);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        p2pManager.addLocalService(p2pChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "service registered");
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
            }

            @Override
            public void onFailure(int arg0) {
                Log.e(TAG, "cannot register service");
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }

    private void unregisterService() {
        p2pManager.removeLocalService(p2pChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "service unregistered");
            }

            @Override
            public void onFailure(int arg0) {
                Log.e(TAG, "cannot unregister service");
            }
        });
    }

    private void initPeersDiscovery() {
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance("run2gether", "presence");

        p2pManager.addServiceRequest(p2pChannel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "success");
                    }

                    @Override
                    public void onFailure(int code) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                });

        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map<String, String> userAttribMap, WifiP2pDevice device) {
                final SwanUser nearbyUser = new SwanUser(userAttribMap.get("name"), userAttribMap.get("regId"), device);

                if(!nearbyPeers.contains(nearbyUser)) {
                    nearbyPeers.add(nearbyUser);
                    mNearbyPeersAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Found new nearby user " + nearbyUser);
                } else {
                    SwanUser user = nearbyPeers.get(nearbyPeers.indexOf(nearbyUser));

                    if(!user.getRegId().equals(nearbyUser.getRegId())) {
                        // TODO this could have other implications later
                        // (for example if there is an active connection between the 2 users)
                        nearbyPeers.remove(user);
                        nearbyPeers.add(nearbyUser);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SwanLakePlusActivity.this,
                                        "Updated nearby user " + nearbyUser,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d(TAG, "Updated nearby user " + nearbyUser);
                    }
                }
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice resourceType) {
                // nothing to do here
                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
            }
        };

        p2pManager.setDnsSdResponseListeners(p2pChannel, servListener, txtListener);
        nearbyPeersChecker.run();
    }

    public void discover() {
        p2pManager.discoverServices(p2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                /*Log.d(TAG, "peer discovery was successful");*/
            }

            @Override
            public void onFailure(int code) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                if (code == WifiP2pManager.P2P_UNSUPPORTED) {
                    Log.d(TAG, "P2P isn't supported on this device.");
                } else {
                    Log.d(TAG, "some other error");
                }
            }
        });
    }

    public static void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        Log.d("connect", "connecting to peer");
        p2pManager.connect(p2pChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("connect", "connection succesful");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Connect failed. Retry.");

                switch (reason) {
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Log.e("connect failed", "p2p unsupported");
                        break;
                    case WifiP2pManager.ERROR:
                        Log.e("connect failed", "internal error");
                        break;
                    case WifiP2pManager.BUSY:
                        Log.e("connect failed", "busy");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    protected void registerBackground(final Switch switchWidget) {
        switchWidget.setEnabled(false);
        new Thread() {
            public void run() {
                try {
                    if (SwanGCMConstants.API_KEY.equals(SwanGCMConstants.EMPTY)
                            || SwanGCMConstants.SENDER_ID
                            .equals(SwanGCMConstants.EMPTY)) {
                        throw new RuntimeException(
                                "Please provide valid values in SwanGCMConstants");

                    }
                    Registry.add(SwanLakePlusActivity.this,
                            Expression.LOCATION_SELF, GoogleCloudMessaging
                                    .getInstance(SwanLakePlusActivity.this)
                                    .register(SwanGCMConstants.SENDER_ID));
                    PreferenceManager
                            .getDefaultSharedPreferences(SwanLakePlusActivity.this)
                            .edit().putBoolean("enabled", true).commit();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(
                                    SwanLakePlusActivity.this,
                                    "Got a registration ID: "
                                            + Registry.get(
                                            SwanLakePlusActivity.this,
                                            Expression.LOCATION_SELF),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    registerService();

                } catch (IOException e) {
                    Log.d(TAG,
                            "Failed to register with Google Cloud Messaging", e);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            switchWidget.setChecked(false);
                            Toast.makeText(
                                    SwanLakePlusActivity.this,
                                    "Failed to register with Google Cloud Messaging",
                                    Toast.LENGTH_LONG).show();
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

    public BaseAdapter getContactsAdapter() {
        return mContactsAdapter;
    }

    public BaseAdapter getNearbyPeersAdapter() {
        return mNearbyPeersAdapter;
    }

    @Override
    public void updatePeers(Collection<WifiP2pDevice> deviceList) {
        Log.d(TAG, "[peer list changed] " + deviceList.size() + " peers in list");
    }

    @Override
    public void processWDUpdate(String update) {

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
