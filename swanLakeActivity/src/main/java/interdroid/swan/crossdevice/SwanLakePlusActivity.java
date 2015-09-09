package interdroid.swan.crossdevice;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.res.TypedArray;
import android.net.wifi.p2p.WifiP2pDevice;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import interdroid.swan.R;
import interdroid.swan.swansong.Expression;


/**
 * Created by vladimir on 9/8/15.
 */
public class SwanLakePlusActivity extends FragmentActivity implements WDPeerToPeerI, ActionBar.TabListener {

    private static final String TAG = "SwanLakePlusActivity";

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    ListView listView;
    private RegisteredSWANsAdapter mAdapter;

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    Fragment fragment = new PeerListFragment(mAdapter);
                    return fragment;

                case 1:
                    fragment = new PeerListFragment(mAdapter);
                    return fragment;

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
                    return "Browse";

                default:
                    return "";
            }
        }
    }
    public static class PeerListFragment extends ListFragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        RegisteredSWANsAdapter mAdapter;

        public PeerListFragment(RegisteredSWANsAdapter mAdapter) {
            this.mAdapter = mAdapter;
        }

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
                            SparseBooleanArray array = getListView()
                                    .getCheckedItemPositions();
                            List<String> names = Registry
                                    .getNames(getActivity());
                            for (int i = 0; i < names.size(); i++) {
                                if (array.get(i)) {
                                    Registry.remove(getActivity(), names.get(i));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                    }
                    mode.finish();
                    return true;
                }

                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {
                    mode.setTitle(getListView().getCheckedItemCount() + " selected");
                }
            });

            setListAdapter(mAdapter);
        }

        /* @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.hello_world, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }*/
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
                            listView.setItemChecked(position,
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

        mAdapter = new RegisteredSWANsAdapter();
        onNewIntent(getIntent());
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

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    public void updatePeers(Collection<WifiP2pDevice> deviceList) {

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
