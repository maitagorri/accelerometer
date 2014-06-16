package maita.android.PotholeLogging;

import maita.android.PotholeLogging.AccelerometerService;
import maita.android.PotholeLogging.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

public class PotholeLogActivity extends Activity {
	private static final String TAG = "PotholeLogActivity:";
	
////////////// Class-classes	
	////////// An AsyncTask for accelerometer recording
	private class AccelerometerTask extends AsyncTask<Void,Void,Void> {
		final Intent accelerometerServiceIntent = new Intent(getBaseContext(),
				AccelerometerService.class);
		
		@Override
	    protected Void doInBackground(Void...voids) {
	        getApplication().getApplicationContext().startService(accelerometerServiceIntent);
	        return(null);
	    }

	    @Override
	    protected void onCancelled() {
	        getApplication().getApplicationContext().stopService(accelerometerServiceIntent);
	        super.onCancelled();
	    }
	}
	
	////////// Another AsyncTask for location recording
	private class LocationTask extends AsyncTask<Void,Void,Void> {
		final Intent locationServiceIntent = new Intent(getBaseContext(),
				LocationService.class);
		
		@Override
	    protected Void doInBackground(Void...voids) {
	        getApplication().getApplicationContext().startService(locationServiceIntent);
	        return(null);
	    }

	    @Override
	    protected void onCancelled() {
	        getApplication().getApplicationContext().stopService(locationServiceIntent);
	        super.onCancelled();
	    }
	}

	
/////////////// Lifecycle
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Context context = getApplicationContext();
		
		// Intent used for stopping the services
		final Intent accelerometerServiceStopIntent = new Intent(context, AccelerometerService.class);
		final Intent locationServiceStopIntent = new Intent (context, LocationService.class);

		Log.i(TAG, getExternalFilesDir(null).getAbsolutePath());
		
		
		//////////////Pothole button
		final Button potholeButton = (Button) findViewById(R.id.pothole_button);
	
		potholeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {			
				// Send a broadcast: pothole detected.
				sendPothole();
				Toast.makeText(getApplicationContext(),R.string.pothole_reported_string, 
						Toast.LENGTH_SHORT).show();
				}	
			});
		////////// start/stop recording buttons
		final Button startButton = (Button) findViewById(R.id.start_button);
		final Button stopButton = (Button) findViewById(R.id.stop_button);
		
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				
				// Start the AccelerometerService using the Intent
				new AccelerometerTask().execute(null,null,null);
				new LocationTask().execute(null,null,null);
				

				Toast.makeText(getApplicationContext(),R.string.services_started_string, 
		                Toast.LENGTH_SHORT).show();
				startButton.setVisibility(View.INVISIBLE);
				stopButton.setVisibility(View.VISIBLE);
				potholeButton.setEnabled(true);
				
				Log.i(TAG, "calling services");

			}
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View src) {
				// Stop the AccelerometerService using the Intent
				context.stopService(accelerometerServiceStopIntent);
				Log.i(TAG, "stopped services");
				context.stopService(locationServiceStopIntent);
				
				Toast.makeText(getApplicationContext(),R.string.service_stopping_string, 
		                Toast.LENGTH_SHORT).show();
				startButton.setVisibility(View.VISIBLE);
				stopButton.setVisibility(View.INVISIBLE);
				potholeButton.setEnabled(false);
			}
		});

		
	}
	

////////////// Class methods	
	////////// A method for broadcasting a pothole
	private void sendPothole() {
		Intent intent = new Intent("pothole_observed");
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
		lbm.sendBroadcast(intent);
	}
	
}



	

	
