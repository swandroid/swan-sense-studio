package interdroid.swan.crossdevice.queueapp;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import interdroid.swan.R;
import interdroid.swan.crossdevice.bluetooth.BTLogRecord;
import interdroid.swan.crossdevice.bluetooth.BTManager;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.ValueExpression;

//TODO cancel timer after checkout
public class QueuyActivity extends Activity {

    private static final String TAG = "BLEQueuyApp";
    private final String REQUEST_QUEUE_LOCAL = "888";
    private final String REQUEST_QUEUE_REMOTE = "999";
    private final int CHECK_INTERVAL = 5000;

    private TextView tvWaitingTime = null;
    private TextView tvMaxWaitingTime = null;
    private TextView tvCheckoutProgress = null;
    private long waitingTime = 0;
    private long waitingTimestamp = 0;
    private long startTime = 0;
    private int checkoutProgress = 0;
    private TreeMap<Long, Long> waitingTimeMap = new TreeMap<>();
    private ArrayList<QueuyLogRecord> logs = new ArrayList<>();
    private Handler handler = new Handler();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BTManager.ACTION_LOG_MESSAGE.equals(action)) {
                String message = intent.getStringExtra("log");
                TextView tv = (TextView) findViewById(R.id.logBox);
                tv.setText(message + "\n" + tv.getText());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queuy);
        tvWaitingTime = (TextView) findViewById(R.id.waitingTime);
        tvMaxWaitingTime = (TextView) findViewById(R.id.maxWaitingTime);
        tvCheckoutProgress = (TextView) findViewById(R.id.checkoutProgress);
        IntentFilter intentFilter = new IntentFilter(BTManager.ACTION_LOG_MESSAGE);
        registerReceiver(mReceiver, intentFilter);
    }

    /* called when "start" button is pressed */
    public void startWaiting(View view) {
        String queueLocalExpression = "self@beaconQueue:waitingTime{ANY,0}";
        String queueRemoteExpression = "NEARBY@beaconQueue:waitingTime{ANY,0}";
        startTime = System.currentTimeMillis();

        final Runnable timeChecker = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "running timeChecker on " + waitingTimeMap);
                TreeSet<Long> timestampSet = new TreeSet<Long>(waitingTimeMap.keySet());
                long maxWaitingTime = waitingTime;
                long maxTimestamp = waitingTimestamp;

                for(long timestamp : timestampSet) {
                    long waitingTime = waitingTimeMap.get(timestamp);

                    if(waitingTime > maxWaitingTime) {
                        maxWaitingTime = waitingTime;
                        maxTimestamp = timestamp;
                    }
                    if(System.currentTimeMillis() - timestamp > CHECK_INTERVAL) {
                        waitingTimeMap.remove(timestamp);
                    }
                }

                tvMaxWaitingTime.setText("Est waiting time: " + (maxWaitingTime - waitingTime));
                logs.add(new QueuyLogRecord(maxTimestamp, maxWaitingTime - waitingTime));
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };

        handler.postDelayed(timeChecker, CHECK_INTERVAL);

        try {
            ExpressionManager.registerValueExpression(this, REQUEST_QUEUE_LOCAL,
                    (ValueExpression) ExpressionFactory.parse(queueLocalExpression),
                    new ValueExpressionListener() {
                        /* Registering a listener to process new values from the registered sensor*/
                        @Override
                        public void onNewValues(String id, TimestampedValue[] arg1) {
                            if (arg1 != null && arg1.length > 0) {
                                Log.d(TAG, "received beacon waiting time " + arg1[0].getValue());
                                waitingTime = (long) arg1[0].getValue();
                                waitingTimestamp = System.currentTimeMillis();
                                tvWaitingTime.setText("Waited time: " + waitingTime);
                            } else {
                                Log.w(TAG, "value is null");
                            }

                        }
                    });

            ExpressionManager.registerValueExpression(this, REQUEST_QUEUE_REMOTE,
                    (ValueExpression) ExpressionFactory.parse(queueRemoteExpression),
                    new ValueExpressionListener() {
                        /* Registering a listener to process new values from the registered sensor*/
                        @Override
                        public void onNewValues(String id, TimestampedValue[] arg1) {
                            if (arg1 != null && arg1.length > 0) {
                                Log.d(TAG, "received remote waiting time " + arg1[0].getValue());
                                long waitingTime = Long.parseLong(arg1[0].getValue().toString());
                                waitingTimeMap.put(System.currentTimeMillis(), waitingTime);
                            } else {
                                Log.w(TAG, "value is null");
                            }

                        }
                    });
        } catch (SwanException e) {
            e.printStackTrace();
        } catch (ExpressionParseException e) {
            e.printStackTrace();
        }
    }

    /* called when "checkout" button is pressed */
    public void checkout(View view) {
        EditText checkoutEdit = (EditText) findViewById(R.id.checkoutTime);
        double checkoutTime = Double.parseDouble(checkoutEdit.getText().toString()) * 60000;
        final double timeChunk = checkoutTime / 100;

        Runnable progressUpdater = new Runnable() {
            @Override
            public void run() {
                checkoutProgress++;
                tvCheckoutProgress.setText("Checkout progress: " + checkoutProgress + "%");

                if(checkoutProgress < 100) {
                    handler.postDelayed(this, (long)timeChunk);
                } else {
                    printLogs(System.currentTimeMillis());
                    cleanup();
                }
            }
        };

        handler.postDelayed(progressUpdater, (long)timeChunk);
        printLogs(System.currentTimeMillis());
    }

    private void cleanup() {
        ExpressionManager.unregisterExpression(QueuyActivity.this, REQUEST_QUEUE_LOCAL);
        ExpressionManager.unregisterExpression(QueuyActivity.this, REQUEST_QUEUE_REMOTE);
        // we use this workaround to stop sending values to other phones
        BluetoothAdapter.getDefaultAdapter().disable();
    }

    private void printLogs(long stopTime) {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
        String logSuffix = sdf.format(new Date());
        File logsDir = new File(this.getExternalFilesDir(null), "logs");
        File logFile = new File(logsDir, "queuy-log-" + logSuffix);
        logsDir.mkdirs();

        for(QueuyLogRecord logRec : logs) {
            sb.append(logRec.print(startTime, stopTime) + "\n");
        }

        try {
            FileWriter fw = new FileWriter(logFile);
            fw.append("\n\n" + QueuyLogRecord.printHeader());
            fw.append("\n\n" + sb.toString());
            fw.close();

            MediaScannerConnection.scanFile(this, new String[]{ logFile.getAbsolutePath() }, null, null);
            Log.i(TAG, "log printed");
        } catch (IOException e) {
            Log.e(TAG, "couldn't write log");
        }
    }
}
