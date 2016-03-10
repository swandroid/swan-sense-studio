package interdroid.swan.sensors.impl.wear.shared.events;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public final class BusProvider
{
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }


    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static void postOnMainThread(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BUS.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    BUS.post(event);
                }
            });
        }
    }
    private BusProvider() {
    }
}
