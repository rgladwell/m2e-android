package test.application;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import test.library.ICounterService;

public class MainActivity extends Activity {
	private ICounterService counterService;
	private CounterServiceConnection serviceConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if(serviceConnection == null) {
			serviceConnection = new CounterServiceConnection();
			bindService(new Intent("test.library.ICounterService"), serviceConnection, Context.BIND_AUTO_CREATE);
		}

		((Button) findViewById(R.id.GetCountButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(serviceConnection == null) {
					Toast.makeText(MainActivity.this, "Counter Service Not Bound", Toast.LENGTH_SHORT).show();
				}
				else {
					try {
						Toast.makeText(MainActivity.this, counterService.getCounter() + "", Toast.LENGTH_LONG).show();
					}
					catch(RemoteException re) {
						Log.e(getClass().getSimpleName(), re.toString());
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		unbindService(serviceConnection);
		serviceConnection = null;
		super.onDestroy();
	}

	class CounterServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			counterService = ICounterService.Stub.asInterface((IBinder) binder);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			counterService = null;
		}
	}
}
