package test.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import test.library.ICounterService;

public class CounterService extends Service {
	private int counter = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return new ICounterService.Stub() {
			public int getCounter() throws RemoteException {
				return counter++;
			}
		};
	}
}
