package maita.android.PotholeLogging;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class LocationService extends Service implements LocationListener {

	private final String TAG = "PotholeLogActivity:LocationService";

	private LocationManager mLocationManager;
	private Location recentLocation;
	
	private long LOCATION_TIME_INTERVAL = 900;
	private float LOCATION_SPACE_SEPARATION = 0;
	//private long readable_time;
	
	File locfile;
	File potfile;

	////////// Service lifecycle
	
	@Override
	public void onCreate() {
		super.onCreate();

		// Get reference to LocationManager
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//Wait for pothole detections
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
		lbm.registerReceiver(mPotholeReceiver, new IntentFilter("pothole_observed"));
		
		Log.i(TAG, "service instance created");
		
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_TIME_INTERVAL,LOCATION_SPACE_SEPARATION,this);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,LOCATION_TIME_INTERVAL,LOCATION_SPACE_SEPARATION,this);
		
		Log.i(TAG, "requested location updates");
		
		Time time = new Time();
		time.setToNow();
		String nowTime = time.format2445();
		
		String locfilename = "loc_" + nowTime + ".txt";
		locfile = new File(getExternalFilesDir(null), locfilename);
		String potfilename = "pot_" + nowTime + ".txt";
		potfile = new File(getExternalFilesDir(null), potfilename);
		
		/// Write a first location entry
		recentLocation = lastKnownLocation();
		writeLocationToFile(recentLocation,locfile);
		
		
		
		// Do automatically restart this Service if it is killed
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		mLocationManager.removeUpdates(this);
		mLocationManager = null;
		//Toast.makeText(getApplicationContext(),R.string.service_stopping_string, 
          //      Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	// Can't bind to this Service
	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}
	
	/////// LocationListener methods
	
	
	@Override
	public void onLocationChanged(Location location) {
		
		recentLocation = location;
		writeLocationToFile(location, locfile);	
			   		
		}
	
	public void onStatusChanged(String provider, int status,
			Bundle extras) {
		// NA
	}

	public void onProviderEnabled(String provider) {
		// NA
	}

	public void onProviderDisabled(String provider) {
		// NA
	}
	
	//////// Pothole logging machinery
	
	public class PotholeReceiver extends BroadcastReceiver {
	
		   @Override
		   public void onReceive(Context context, Intent intent) {
		      writeLocationToFile(recentLocation, potfile);
		   }

		};
		
	PotholeReceiver mPotholeReceiver = new PotholeReceiver();
	
	////////// getting the last known location
	private Location lastKnownLocation() {

		Location bestResult = null;
		long bestAge = 0;

		List<String> matchingProviders = mLocationManager.getAllProviders();

		for (String provider : matchingProviders) {

			Location location = mLocationManager.getLastKnownLocation(provider);

			if (location != null) {

				long time = location.getTime();

				if (time > bestAge) {

					bestResult = location;

				}
			}
		}
		return bestResult;
		}
	
	//////////// writing location to file
	private void writeLocationToFile(Location location, File file) {
		
		long timestamp = location.getTime();
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		float acc = location.getAccuracy();
		
		String locText = String.valueOf(timestamp) + "," + String.valueOf(lat) + "," + String.valueOf(lon) + "," + String.valueOf(acc); 
		try
		   {
		      BufferedWriter buf = new BufferedWriter(new FileWriter(file, true)); 
		      buf.append(locText);
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