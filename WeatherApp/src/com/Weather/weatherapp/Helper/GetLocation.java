package com.Weather.weatherapp.Helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * The class gets current location of the user
 */

public class GetLocation {

	private double latitude, longitude;
	LocationManager locationManager;
	MyLocationListner locationListener;
	LocationAvailableListener mListener;
	Context mContext;

	/**
	 * Interface Location Listener
	 */
	public interface LocationAvailableListener {
		/**
		 * Method called when application fetches the device location successfully
		 */
		public void isLocationAvailable(boolean isAvailable);
	}

	public GetLocation(Context applicationContext, LocationAvailableListener locationAvailableListner) {
		mContext = applicationContext;
		this.locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListner();
		this.mListener = locationAvailableListner;
	}

	/**
	 * Make Request by Latitude and Longitude of current location.
	 * 
	 * @return boolean Return False when both GPS and Network Provider are not enabled
	 */
	public boolean getCurrentLocation() {
		boolean gpsEnabled = false, networkEnabled = false;
		try {
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if ((!gpsEnabled) && (!networkEnabled))
			return false;
		String provider = null;
		if (networkEnabled)
			provider = LocationManager.NETWORK_PROVIDER;
		else if (gpsEnabled) {
			provider = LocationManager.GPS_PROVIDER;
			Toast.makeText(mContext, "Only GPS Enabled, enable High Accuracy option for better result ",
					Toast.LENGTH_LONG).show();
		}
		locationListener = new MyLocationListner();
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			locationListener.onLocationChanged(location);
		}
		locationManager.requestLocationUpdates(provider, 0, 10, locationListener);

		return true;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	/**
	 * Class implementing LocationListener interface
	 */
	class MyLocationListner implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			mListener.isLocationAvailable(true);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	}

}
