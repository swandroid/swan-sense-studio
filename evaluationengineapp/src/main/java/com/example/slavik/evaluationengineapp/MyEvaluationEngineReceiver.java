package com.example.slavik.evaluationengineapp;

import android.content.Context;
import android.content.Intent;

import interdroid.swancore.engine.EvaluationEngineReceiverBase;

/**
 * Created by Veaceslav Munteanu on 6/1/16.
 *
 * @email veaceslav.munteanu90@gmail.com
 */
public class MyEvaluationEngineReceiver extends EvaluationEngineReceiverBase {
    public void onReceive(Context context, Intent intent) {
        // forward the intent to the service
        intent.setClass(context, MyEvaluationEngine.class);
        context.startService(intent);
    }
}
