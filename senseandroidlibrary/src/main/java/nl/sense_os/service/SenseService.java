/**************************************************************************************************
 * Copyright (C) 2010 Sense Observation Systems, Rotterdam, the Netherlands. All rights reserved. *
 *************************************************************************************************/
package nl.sense_os.service;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;

import nl.sense_os.service.commonsense.SenseApi;
import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensePrefs.Auth;
import nl.sense_os.service.constants.SensePrefs.Main.Advanced;
import nl.sense_os.service.constants.SensePrefs.Status;
import nl.sense_os.service.constants.SenseUrls;
import nl.sense_os.service.ctrl.Controller;
import nl.sense_os.service.provider.SNTP;
import nl.sense_os.service.scheduler.ScheduleAlarmTool;

/**
 * Main Sense service class.<br/>
 * <br/>
 * Activities can bind to this service and call functions to:
 * <ul>
 * <li>log in;</li>
 * <li>register;</li>
 * <li>start sensing;</li>
 * <li>start/stop individual sensor modules;</li>
 * <li>set and get properties;</li>
 * </ul>
 * When the {@link #toggleMain(boolean)} method is called to start the sensing, the service starts
 * itself and registers itself as a foreground service so it does not get easily killed by Android.
 *
 * @author Steven Mulder <steven@sense-os.nl>
 */
public class SenseService extends Service {

    /**
     * Class used for the client Binder. Because we know this service always runs in the same
     * process as its clients, we don't need to deal with IPC.
     *
     * @see http://developer.android.com/guide/components/bound-services.html
     */
    public class SenseBinder extends Binder {

        public SenseServiceStub getService() {
            return new SenseServiceStub(SenseService.this);
        }
    }

    private static final String TAG = "Sense Service";

    /**
     * Intent action to force a re-login attempt when the service is started.
     */
    public static final String EXTRA_RELOGIN = "relogin";

    /**
     * Intent action to notify that the service is started,
     * boolean extra for of the status changed broadcast  R.string.action_sense_service_broadcast.
     */
    public static final String EXTRA_SERVICE_STARTED = "service_started";

    private IBinder binder = new SenseBinder();

    private ServiceStateHelper state;

    private Controller controller;
    private DataTransmitter transmitter;

    /**
     * Handler on main application thread to display toasts to the user.
     */
    private static Handler toastHandler = new Handler(Looper.getMainLooper());
    private static Handler initHandler;

    /**
     * Changes login of the Sense service. Removes "private" data of the previous user from the
     * preferences. Can be called by Activities that are bound to the service.
     *
     * @param username Username
     * @param password Hashed password
     * @return 0 if login completed successfully, -2 if login was forbidden, and -1 for any other
     * errors.
     */
    int changeLogin(String username, String password) {
        Log.v(TAG, "Change login");

        logout();

        // save new username and password in the preferences
        Editor authEditor = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE).edit();
        authEditor.putString(Auth.LOGIN_USERNAME, username);
        authEditor.putString(Auth.LOGIN_PASS, password);
        authEditor.commit();

        return login();
    }

    /**
     * Checks if the installed Sense Platform application has an update available, alerting the user
     * via a Toast message.
     */
    private void checkVersion() {
        try {
            String packageName = getPackageName();
            if ("nl.sense_os.app".equals(packageName)) {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
                String versionName = URLEncoder.encode(packageInfo.versionName, "UTF-8");
                Log.i(TAG, "Running Sense App version '" + versionName + "'");

                if (versionName.contains("unstable") || versionName.contains("testing")) {
                    return;
                }

                String url = SenseUrls.VERSION + "?version=" + versionName;
                Map<String, String> response = SenseApi.request(this, url, null, null);
                JSONObject content = new JSONObject(response.get(SenseApi.RESPONSE_CONTENT));

                if (content.getString("message").length() > 0) {
                    Log.i(TAG, "Newer Sense App version available: " + content.toString());
                    showToast(content.getString("message"));
                }
            } else {
                // this is a third party app
            }

        } catch (Exception e) {
            Log.w(TAG, "Failed to get Sense App version: " + e);
        }
    }

    /**
     * Tries to login using the username and password from the private preferences and updates the
     * {@link #isLoggedIn} status accordingly. Can also be called from Activities that are bound to
     * the service.
     *
     * @return 0 if login completed successfully, -2 if login was forbidden, and -1 for any other
     * errors.
     */
    synchronized int login() {

        if (state.isLoggedIn()) {
            // we are already logged in
            Log.v(TAG, "Skip login: already logged in");
            return 0;
        }

        // check that we are actually allowed to log in
        SharedPreferences mainPrefs = getSharedPreferences(SensePrefs.MAIN_PREFS, MODE_PRIVATE);
        boolean allowed = mainPrefs.getBoolean(Advanced.USE_COMMONSENSE, true);
        if (!allowed) {
            Log.w(TAG, "Not logging in. Use of CommonSense is disabled.");
            return -1;
        }

        Log.v(TAG, "Try to log in");

        // get login parameters from the preferences
        SharedPreferences authPrefs = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE);
        final String username = authPrefs.getString(Auth.LOGIN_USERNAME, null);
        final String pass = authPrefs.getString(Auth.LOGIN_PASS, null);

        // try to log in
        int result = -1;
        if ((username != null) && (pass != null)) {
            try {
                result = SenseApi.login(this, username, pass);
            } catch (Exception e) {
                Log.w(TAG, "Exception during login! " + e + ": '" + e.getMessage() + "'");
                // handle result below
            }
        } else {
            Log.w(TAG, "Cannot login: username or password unavailable");
        }

        // handle the result
        switch (result) {
            case 0: // logged in successfully
                onLogIn();
                break;
            case -1: // error
                Log.w(TAG, "Login failed!");
                onLogOut();
                break;
            case -2: // forbidden
                Log.w(TAG, "Login forbidden!");
                onLogOut();
                break;
            default:
                Log.e(TAG, "Unexpected login result: " + result);
                onLogOut();
        }

        return result;
    }

    void logout() {
        Log.v(TAG, "Log out");

        // clear cached settings of the previous user (e.g. device id)
        Editor authEditor = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE).edit();
        authEditor.clear();
        authEditor.commit();

        // log out before changing to a new user
        onLogOut();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "Some component is binding to Sense Platform service");
        return binder;
    }

    /**
     * Does nothing except poop out a log message. The service is really started in onStart,
     * otherwise it would also start when an activity binds to it.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        Log.v(TAG, "Sense Platform service is being created");
        state = ServiceStateHelper.getInstance(this);
    }

    /**
     * Stops sensing, logs out, removes foreground status.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        Log.v(TAG, "Sense Platform service is being destroyed");

        // update login status
//        onLogOut();

        // stop the main service
        stopForeground(true);

        super.onDestroy();
    }

    /**
     * Performs tasks after successful login: update status bar notification; start transmitting
     * collected sensor data and register the gcm_id.
     */
    private void onLogIn() {
        Log.i(TAG, "Logged in.");
        // update ntp time
        SNTP.getInstance().requestTime(SNTP.HOST_WORLDWIDE, 2000);

        // update login status
        state.setLoggedIn(true);
        state.setStarted(true);

        // store this login
        SharedPreferences prefs = getSharedPreferences(SensePrefs.MAIN_PREFS, MODE_PRIVATE);
        prefs.edit().putLong(SensePrefs.Main.LAST_LOGGED_IN, System.currentTimeMillis()).commit();

        checkVersion();

        onSyncRateChange(); //called to start the scheduler
    }

    /**
     * Performs cleanup tasks when the service is logged out: updates the status bar notification;
     * stops the periodic alarms for data transmission.
     */
    private void onLogOut() {
        Log.i(TAG, "Logged out");

        // update login status
        state.setLoggedIn(false);

        transmitter = DataTransmitter.getInstance(this);
        transmitter.stopTransmissions();

        // completely stop the MsgHandler service
        stopService(new Intent(this, MsgHandler.class));
    }

    void onSampleRateChange() {
        Log.v(TAG, "Sample rate changed");
        if (state.isStarted()) {
            ScheduleAlarmTool.getInstance(this).resetNextExecution();
        }
    }

    /**
     * Starts the Sense service. Tries to log in and start sensing; starts listening for network
     * connectivity broadcasts.
     *
     * @param intent  The Intent supplied to {@link Activity#startService(Intent)}. This may be null if
     *                the service is being restarted after its process has gone away.
     * @param flags   Additional data about this start request. Currently either 0,
     *                {@link Service#START_FLAG_REDELIVERY} , or {@link Service#START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to start. Use with
     *                {@link #stopSelfResult(int)}.
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(TAG, "Sense Platform service is being started");

        if (null == initHandler) {
            HandlerThread startThread = new HandlerThread("Start thread");
            startThread.start();
            initHandler = new Handler(startThread.getLooper());
        }
        initHandler.post(new Runnable() {

            @Override
            public void run() {

                boolean mainStatus = getSharedPreferences(SensePrefs.STATUS_PREFS, MODE_PRIVATE)
                        .getBoolean(Status.MAIN, true);
                if (false == mainStatus) {
                    Log.w(TAG, "Sense service was started when the main status is not set!");
                    AliveChecker.stopChecks(SenseService.this);
                    stopForeground(true);
                    state.setForeground(false);

                } else {
                    // make service as important as regular activities
                    if (false == state.isForeground()) {
                        Notification n = state.getStateNotification();
                        startForeground(ServiceStateHelper.NOTIF_ID, n);
                        state.setForeground(true);
                        AliveChecker.scheduleChecks(SenseService.this);
                    }

                    // re-login if necessary
                    boolean relogin = !state.isLoggedIn();
                    relogin |= (null == intent); // intent is null when Service
                    // was killed
                    relogin |= (null != intent) && intent.getBooleanExtra(EXTRA_RELOGIN, false);
                    if (relogin) {
                        login();
                    } else {
                        checkVersion();
                    }
                }
            }
        });

        return START_STICKY;
    }

    void onSyncRateChange() {
        Log.v(TAG, "Sync rate changed");
        if (state.isStarted()) {
            controller = Controller.getController(this);
            transmitter = DataTransmitter.getInstance(this);
            transmitter.stopTransmissions();
            ScheduleAlarmTool.getInstance(this).resetNextExecution();
            controller.scheduleTransmissions();
        }

        // update any widgets
//        startService(new Intent(getString(R.string.action_widget_update)));
    }

    /**
     * Tries to register a new user using the username and password from the private preferences and
     * updates the {@link #isLoggedIn} status accordingly. Can also be called from Activities that
     * are bound to the service.
     *
     * @param username
     * @param password Hashed password
     * @param email
     * @param address
     * @param zipCode
     * @param country
     * @param name
     * @param surname
     * @param mobile
     * @return 0 if registration completed successfully, -2 if the user already exists, and -1 for
     * any other unexpected responses.
     */
    synchronized int register(String username, String password, String email, String address,
                              String zipCode, String country, String name, String surname, String mobile) {
        Log.v(TAG, "Try to register new user");

        // log out before registering a new user
        logout();

        // save username and password in preferences
        Editor authEditor = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE).edit();
        authEditor.putString(Auth.LOGIN_USERNAME, username);
        authEditor.putString(Auth.LOGIN_PASS, password);
        authEditor.commit();

        // try to register
        int registered = -1;
        if ((null != username) && (null != password)) {
            // Log.v(TAG, "Registering: " + username +
            // ", password hash: " + hashPass);

            try {
                registered = SenseApi.registerUser(this, username, password, name, surname, email,
                        mobile);
            } catch (Exception e) {
                Log.w(TAG, "Exception during registration: '" + e.getMessage()
                        + "'. Connection problems?");
                // handle result below
            }
        } else {
            Log.w(TAG, "Cannot register: username or password unavailable");
        }

        // handle result
        switch (registered) {
            case 0:
                Log.i(TAG, "Successful registration for '" + username + "'");
                login();
                break;
            case -1:
                Log.w(TAG, "Registration failed");
                state.setLoggedIn(false);
                break;
            case -2:
                Log.w(TAG, "Registration failed: user already exists");
                state.setLoggedIn(false);
                break;
            default:
                Log.w(TAG, "Unexpected registration result: " + registered);
        }

        return registered;
    }

    /**
     * Displays a Toast message using the process's main Thread.
     *
     * @param message Toast message to display to the user
     */
    private void showToast(final String message) {
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SenseService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
