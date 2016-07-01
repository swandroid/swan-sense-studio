package interdroid.cuckoo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import interdroid.swan.ICuckooService;

/**
 * Add local implementation of the offloadable method here
 */
public class CuckooServiceLocal extends Service {

	private final ICuckooService.Stub mBinder = new ICuckooService.Stub () {
	};

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}

