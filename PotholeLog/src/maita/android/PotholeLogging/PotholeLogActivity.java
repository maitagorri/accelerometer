package maita.android.PotholeLogging;

import maita.android.PotholeLogging.AccelerometerService;
import maita.android.PotholeLogging.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.Log;

public class PotholeLogActivity extends Activity {
	private static final String TAG = "PotholeLogActivity:";
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	////////// An AsyncTask for accelerometer recording
/*	private class AccelerometerTask extends AsyncTask {

	    @Override
	    protected Object doInBackground(Object... objects) {
	        return getApplication().getApplicationContext().startService(intent);
	    }

	    @Override
	    protected void onCancelled() {
	        getActivity().getApplicationContext().stopService(intent);
	        super.onCancelled();
	    }
	}*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/// I would like this to restart to display the proper button...
		/// later.
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Context context = getApplicationContext();
		// Intent used for starting the AccelerometerService
		final Intent accelerometerServiceIntent = new Intent(context,
				AccelerometerService.class);
		//accelerometerServiceIntent.putExtra("somekey", "somevalue");
		

		Log.i(TAG, getExternalFilesDir(null).getAbsolutePath());
		
		////////// start/stop recording buttons
		final Button startButton = (Button) findViewById(R.id.start_button);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
				// Start the AccelerometerService using the Intent
				context.startService(accelerometerServiceIntent);
				Log.i(TAG, "calling accelerometer service");

			}
		});
		

		final Button stopButton = (Button) findViewById(R.id.stop_button);
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				// Stop the AccelerometerService using the Intent
				context.stopService(accelerometerServiceIntent);
				Log.i(TAG, "stopped accelerometer service");
			}
		});


	}

	// May want to do something about this, too...
	@Override
	protected void onResume() {
		super.onResume();


	}

	// May want to fix...
	@Override
	protected void onPause() {
		super.onPause();
	}

	

	
}