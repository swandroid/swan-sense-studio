/**************************************************************************************************
 * Copyright (C) 2010 Sense Observation Systems, Rotterdam, the Netherlands. All rights reserved. *
 *************************************************************************************************/
package nl.sense_os.service.configuration;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import nl.sense_os.service.R;
import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensePrefs.Main;

/**
 * IntentService to handle changes in the configuration. When the service is started, it will look
 * into the requirement and set the preferences accordingly.<br/>
 * <br/>
 * Example of the requirement:
 *
 * @author Ahmy Yulrizka <ahmy@sense-os.nl>
 */
public class ConfigurationService extends IntentService {

    private Integer syncRate = null;
    private SharedPreferences mainPrefs;

    public ConfigurationService() {
        super("ConfigurationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        mainPrefs = getSharedPreferences(SensePrefs.MAIN_PREFS, Context.MODE_PRIVATE);

        try {
            String reqStr = intent.getStringExtra(RequirementReceiver.EXTRA_REQUIREMENTS);
            JSONObject requirements = new JSONObject(reqStr).getJSONObject("requirements");

            if (requirements.length() == 0)
                return;

            syncRate = null;

            // update common sense sync rate if necessary
            if (syncRate != null) {
                Integer preference_rate = null;

                if (syncRate < 60) // realtime (< 1 min)
                    preference_rate = -2;
                else if (syncRate >= 60 && syncRate < 300) // often (1)
                    preference_rate = -1;
                else if (syncRate >= 300 && syncRate < 900) // normal (5-29 min)
                    preference_rate = 0;
                else if (syncRate >= 900) // Eco-mode (30 min)
                    preference_rate = 1;

                if (preference_rate != null) {
                    String oldValue = mainPrefs.getString(Main.SYNC_RATE, "0");

                    // only update if its have higher rate
                    if (preference_rate < Integer.parseInt(oldValue)) {
                        mainPrefs.edit().putString(Main.SYNC_RATE, preference_rate.toString())
                                .commit();
                    }
                }
            }

            // apply change by starting the service
            startService(new Intent(getString(R.string.action_sense_service)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
