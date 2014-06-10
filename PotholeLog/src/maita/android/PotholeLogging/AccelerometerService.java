package maita.android.PotholeLogging;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class AccelerometerService extends Service implements SensorEventListener {

	@SuppressWarnings("unused")
	private final String TAG = "AccelerometerService";

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	private long timestamp;
	//private long readable_time;
	private String xString;
	private String yString;
	private String zString;
	
	File file;
	

	
	////////// Service lifecycle
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "service instance created");

		
		// Get reference to SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get reference to Accelerometer
		mAccelerometer = (Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		//Log.i(TAG, "service actually got started");
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_GAME);
		//Log.i(TAG, "registered accelerometer");
		Toast.makeText(getApplicationContext(),R.string.accelerometer_registered_string, 
                Toast.LENGTH_SHORT).show();


		file = new File(getExternalFilesDir(null), "records.txt");
		
		// Do automatically restart this Service if it is killed
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		mSensorManager.unregisterListener(this);
		Toast.makeText(getApplicationContext(),R.string.service_stopping_string, 
                Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	// Can't bind to this Service
	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}
	
	
	/////// SensorEventListener methods
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// N/A
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			
			timestamp = System.currentTimeMillis();
			
			float x = event.values[0], y = event.values[1], z = event.values[2];

			xString = String.valueOf(x);
			yString = String.valueOf(y);
			zString = String.valueOf(z);
			String text = timestamp + "," + xString + "," + yString + "," + zString;
			
			   try
			   {
			      BufferedWriter buf = new BufferedWriter(new FileWriter(file, true)); 
			      buf.append(text);
			      buf.newLine();
			      buf.close();
			   }
			   catch (IOException e)
			   {
			      // TODO Auto-generated catch block
			      e.printStackTrace();
			   }
			
		}
	}

}
