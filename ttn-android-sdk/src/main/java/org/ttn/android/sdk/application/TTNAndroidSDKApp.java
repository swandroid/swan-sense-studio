package org.ttn.android.sdk.application;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

public class TTNAndroidSDKApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // init joda date time
        JodaTimeAndroid.init(this);
    }
}
