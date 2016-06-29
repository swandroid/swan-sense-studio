package interdroid.cuckoo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import interdroid.cuckoo.client.Cuckoo;
import interdroid.cuckoo.client.Oracle;
import interdroid.cuckoo.client.Statistics;
import interdroid.swan.ICuckooService;
import interdroid.cuckoo.remote.CuckooServiceRemote;

public class CuckooManager {
    private static final String TAG = "CuckooManager";

    // Add method name here
    public static final String INVOKE_METHOD = "<ADD_METHOD_NAME_HERE>";

    private static CuckooManager mManager;
    private ICuckooService mComputeService;
    private Context mContext;

    private CuckooManager(Context context){
        mContext = context;
        Intent intent = new Intent(context, CuckooServiceLocal.class);
        intent.setAction(CuckooServiceLocal.class.getName());
        ServiceConnection mConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.d(TAG, "Service has connected");
                mComputeService = ICuckooService.Stub.asInterface(service);
            }

            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                Log.e(TAG, "Service has unexpectedly disconnected");
                mComputeService = null;
            }
        };
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public static CuckooManager getInstance(Context context) {
        if (mManager == null)
            mManager = new CuckooManager(context);
        return mManager;
    }

    public void invokeMethod(final String methodName, final Object[] parameters, final Class<?>[] parameterTypes, final CuckooCallback callback) {
        invokeMethodRemote(methodName, parameters, parameterTypes, callback);
    }

    private void invokeMethodLocal(String methodName, Object[] parameters, CuckooCallback cuckooCallback){
        Log.d(TAG, "Fall back on local implementation");

        Object result = null;
        switch (methodName) {

            // Add method type invocation here
            case INVOKE_METHOD:

                try {
                    while(mComputeService == null) {
                        Log.d(TAG, "Service connection not completed yet");
                        Thread.sleep(1000);
                    }

                    // Call local method invoke here

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    cuckooCallback.onFailure(e);
                    return;
                }
                break;
        }
        cuckooCallback.onSuccess(methodName, result);
    }

    private void invokeMethodRemote(final String methodName, final Object[] parameters, final Class<?>[] parameterTypes, final CuckooCallback cuckooCallback) {
        Log.d(TAG, "Remote implementation");

        new Thread() {
            @Override
            public void run() {
                try {
                    Object result = Cuckoo.invokeMethod(mContext, new Statistics(),
                            CuckooServiceRemote.class.getName(),
                            methodName, parameterTypes, new boolean[]{false},
                            parameters, Oracle.STRATEGY_ENERGY_SPEED, 1, parameters.length, 1, true);
                    cuckooCallback.onSuccess(methodName, result);
                } catch (Exception e) {
                    e.printStackTrace();
                    invokeMethodLocal(methodName, parameters, cuckooCallback);
                }
            }
        }.start();
    }

    public interface CuckooCallback{
        void onSuccess(String methodName, Object response);
        void onFailure(Exception e);
    }
}
