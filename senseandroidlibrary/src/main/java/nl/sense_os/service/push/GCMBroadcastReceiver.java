/**************************************************************************************************
 * Copyright (C) 2010 Sense Observation Systems, Rotterdam, the Netherlands. All rights reserved. *
 *************************************************************************************************/
package nl.sense_os.service.push;

import android.content.Context;

import nl.sense_os.service.R;

/**
 * This class is just Boilerplate to use GCM.
 *
 * @author Ahmy Yulrizka <ahmy@sense-os.nl>
 */
public class GCMBroadcastReceiver extends com.google.android.gcm.GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return context.getString(R.string.action_sense_gcm_intent_service);
    }
}
