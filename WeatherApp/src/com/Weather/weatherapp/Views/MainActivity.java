package com.Weather.weatherapp.Views;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.Weather.weatherapp.R;
import com.Weather.weatherapp.Application.WeatherApplication;
import com.Weather.weatherapp.Bean.LocationData;
import com.Weather.weatherapp.Bean.WeatherData;
import com.Weather.weatherapp.Constants.Constants;
import com.Weather.weatherapp.Helper.GetLocation;
import com.Weather.weatherapp.Helper.GetLocation.LocationAvailableListener;
import com.Weather.weatherapp.Parser.JSONParser;
import com.Weather.weatherapp.Util.DateUtil;
import com.Weather.weatherapp.Util.NetworkUtil;
import com.Weather.weatherapp.Util.TemperatureConversionUtil;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends Activity {
	String TAG = MainActivity.class.getSimpleName();
	private ProgressDialog mpProgressDialog;
	private GetLocation mGetLocation;
	private double lat, lng;
	private LocationData mlLocationData;
	private LinearLayout horizontalViewLayout;
	private TextView textViewCityName, textViewCurrentTemp, textViewMaxTemp, textViewMinTemp, textViewHumidity,
			textViewWeatherType, textViewWeatherDescription, textViewDate, textViewLastUpdated;
	private ImageView weatherImage;
	private String locationName, prevLocationName;
	private boolean isSearchByLocation = false;
	private boolean isCelsius = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		// If the app is in background and activity is recreated, it checks the saved instance and if the last user
		// preference was search by location name then data is retrieved for that location
		if (savedInstanceState != null) {
			isSearchByLocation = savedInstanceState.getBoolean(Constants.KEY_IS_SEARCH_BY_LOCATION);
			if (isSearchByLocation) {
				String LName = savedInstanceState.getString(Constants.KEY_LOCATION);
				if (LName != null)
					makeRequestByName(LName);
			} else
				getLocation();
		} else {
			getLocation();
		}
	}

	/**
	 * Initialize the view and data objects
	 */
	private void init() {
		mpProgressDialog = new ProgressDialog(this);
		mpProgressDialog.setMessage(getString(R.string.progress_dialog_message));
		mpProgressDialog.setCanceledOnTouchOutside(false);
		mpProgressDialog.setCancelable(true);
		horizontalViewLayout = (LinearLayout) findViewById(R.id.horizontal_layout);
		textViewCityName = (TextView) findViewById(R.id.location_name);
		textViewCurrentTemp = (TextView) findViewById(R.id.current_temperature);
		textViewMaxTemp = (TextView) findViewById(R.id.max_temperature);
		textViewMinTemp = (TextView) findViewById(R.id.min_temperature);
		textViewWeatherType = (TextView) findViewById(R.id.weather_type);
		textViewWeatherDescription = (TextView) findViewById(R.id.weather_decription);
		textViewHumidity = (TextView) findViewById(R.id.humidity);
		textViewDate = (TextView) findViewById(R.id.date_field);
		weatherImage = (ImageView) findViewById(R.id.weather_status_image);
		textViewLastUpdated = (TextView) findViewById(R.id.last_updated);
		mpProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// If back is pressed cancel any pending requests in the queue
				WeatherApplication.getInstance().cancelPendingRequests(Constants.REQ_TAG);
			}
		});
	}

	/**
	 * Get the current location of the user when the application is launched If Location not available show proper Toast
	 * message
	 */
	private void getLocation() {
		if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
			mpProgressDialog.show();
			mGetLocation = new GetLocation(this.getApplicationContext(), new LocationAvailableListener() {
				@Override
				public void isLocationAvailable(boolean isAvailable) {
					if (!isSearchByLocation && isAvailable
							&& (mGetLocation.getLatitude() != lat || mGetLocation.getLongitude() != lng)) {
						makeRequestByLatLong(mGetLocation.getLatitude(), mGetLocation.getLongitude());
						lat = mGetLocation.getLatitude();
						lng = mGetLocation.getLongitude();
					}
				}
			});
			if (!mGetLocation.getCurrentLocation()) {
				showErrorTaost(getString(R.string.error_string_location_service_not_enabled));
				Log.v(TAG, "Location service not enabled");
				textViewCityName.setText("Try Searching by Location");
				mpProgressDialog.dismiss();
			}
		} else {
			showErrorTaost(getString(R.string.error_string_network_not_available));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_search_by_location:
			showSearchDialog();
			return true;
		case R.id.action_f_to_c:
			if (item.getTitle().toString().compareTo(getString(R.string.fahrenheit)) == 0) {
				isCelsius = false;
				item.setIcon(getResources().getDrawable(R.drawable.celsius_white));
				item.setTitle(R.string.celsius);
				convertTemperature();
			} else if (item.getTitle().toString().compareTo(getString(R.string.celsius)) == 0) {
				isCelsius = true;
				item.setIcon(getResources().getDrawable(R.drawable.fahrenheit_white));
				item.setTitle(getString(R.string.fahrenheit));
				convertTemperature();
			}
			return true;
		case R.id.action_refresh:
			if (isSearchByLocation)
				makeRequestByName(prevLocationName);
			else {
				lat = 0;
				lng = 0;
				getLocation();
			}
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Get the current location of the user when the application is launched If Location not available show proper Toast
	 * message
	 */

	private void showSearchDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		View dView = this.getLayoutInflater().inflate(R.layout.search_dialog_layout, null);
		dialog.setView(dView);
		dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
		wmlp.gravity = Gravity.TOP;
		final EditText lName = (EditText) dView.findViewById(R.id.add_city_name);
		ImageView search = (ImageView) dView.findViewById(R.id.search_by_location);
		final RadioButton searchByCurrentLocation = (RadioButton) dView.findViewById(R.id.search_by_current_location);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String locationName = lName.getText().toString().trim();
				if (locationName.compareTo("") == 0)
					showErrorTaost(getString(R.string.message_please_enter_location_name));
				else {
					isSearchByLocation = true;
					dialog.dismiss();
					makeRequestByName(locationName);
				}
			}
		});
		searchByCurrentLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchByCurrentLocation.isChecked())
					isSearchByLocation = false;
				dialog.dismiss();
				lat = 0;
				lng = 0;
				getLocation();
			}
		});

		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();

	}

	/**
	 * Make Request by Latitude and Longitude of current location.
	 * 
	 * @param Double
	 *            Latitude
	 * @param Double
	 *            Longitude
	 */
	private void makeRequestByLatLong(double lat, double lng) {
		String url = Constants.WEATHER_LAT_LNG_URL;
		url = String.format(url, lat, lng);
		makeJSONRequest(url);
	}

	/**
	 * Make Request by Name of the location.
	 * 
	 * @param String
	 *            LocationName
	 */
	private void makeRequestByName(String lName) {
		if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
			mpProgressDialog.show();
			String url = Constants.WEATHER_LOCATION_URL;
			if (lName != null) {
				locationName = lName;
				url = String.format(url, lName);
				makeJSONRequest(url);
			}
		} else {
			showErrorTaost(getString(R.string.error_string_network_not_available));
		}

	}

	/**
	 * Make JSON Request using Volley, If request is successful data is provided to parser for parsing JSON Otherwise
	 * show error toast
	 * 
	 * @param String
	 *            URL
	 */
	private void makeJSONRequest(String url) {

		JsonObjectRequest req = new JsonObjectRequest(Method.GET, url, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mlLocationData = new LocationData();
				mlLocationData = new JSONParser().parseData(response.toString());
				if (mlLocationData.getWeatherData().size() != 0) {
					prevLocationName = locationName;
					populateData(mlLocationData.getWeatherData().get(0));
					createHorizontalView();
				} else {
					showErrorTaost(getString(R.string.error_string_data_cannot_be_retrieved));
				}

				mpProgressDialog.dismiss();
			}

		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.v(TAG, error.toString());
				showErrorTaost(getString(R.string.error_string_data_cannot_be_retrieved));
				mpProgressDialog.dismiss();
			}
		});
		WeatherApplication.getInstance().addToRequestQueue(req, Constants.REQ_TAG);
	}

	/**
	 * Dynamically create the horizontal scroll view for 10 days weather forecast Since data is for 10 days created
	 * view's without recycling Horizontal List view can be implemented when data size increases
	 */
	private void createHorizontalView() {
		int id = 0;
		clickListner mClickListner = new clickListner();
		horizontalViewLayout.removeAllViews();
		for (WeatherData data : mlLocationData.getWeatherData()) {
			LinearLayout weatherForecast = new LinearLayout(this);
			LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelOffset(
					R.dimen.horizontal_layout_tile_width), LayoutParams.MATCH_PARENT);
			llParams.setMargins(0, 0, 5, 0);
			weatherForecast.setLayoutParams(llParams);
			weatherForecast.setOrientation(LinearLayout.VERTICAL);
			weatherForecast.setId(id);
			weatherForecast.setOnClickListener(mClickListner);
			weatherForecast.setBackgroundResource(R.drawable.horizontal_list_item_selector);
			weatherForecast.setClickable(true);
			weatherForecast.setGravity(Gravity.CENTER_VERTICAL);

			TextView date = new TextView(this);
			date.setGravity(Gravity.CENTER_HORIZONTAL);
			date.setTextColor(Color.WHITE);
			date.setText(DateUtil.dateInDay_DD_MMFormat(data.getDate()));
			date.setPadding(getResources().getDimensionPixelOffset(R.dimen.horizontal_tile_date_padding), 0,
					getResources().getDimensionPixelOffset(R.dimen.horizontal_tile_date_padding), 0);
			weatherForecast.addView(date);

			ImageView weatherIcon = new ImageView(this);
			Drawable image;
			if (id == 0)
				image = getSmallImage(data.getWeatherType(), data.isNight());
			else
				image = getSmallImage(data.getWeatherType(), false);
			weatherIcon.setImageDrawable(image);
			weatherForecast.addView(weatherIcon);

			TextView weatherType = new TextView(this);
			weatherType.setGravity(Gravity.CENTER_HORIZONTAL);
			weatherType.setText(data.getWeatherType());
			weatherType.setTextColor(Color.WHITE);
			weatherForecast.addView(weatherType);

			horizontalViewLayout.addView(weatherForecast);
			id++;
		}
	}

	/**
	 * Helper method to populate data on main view
	 */
	private void populateData(WeatherData data) {
		weatherImage.setImageDrawable(getLargeImage(data.getWeatherType(), data.isNight()));
		textViewCityName.setText(mlLocationData.getCityName() + ", " + mlLocationData.getcountryName());
		if (isCelsius) {
			String temp = TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil
					.kelvinToCelsius(data.getTempKelvinCurrent()));
			textViewCurrentTemp.setText(temp + getString(R.string.celsius_symbol));
			temp = TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil.kelvinToCelsius(data
					.getTempKelvinMax()));
			textViewMaxTemp.setText(temp + getString(R.string.celsius_symbol));
			temp = TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil.kelvinToCelsius(data
					.getTempKelvinMin()));
			textViewMinTemp.setText(temp + getString(R.string.celsius_symbol));
		} else if (!isCelsius) {
			String temp = TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil
					.kelvinToFahrenheit(data.getTempKelvinCurrent()));
			textViewCurrentTemp.setText(temp + getString(R.string.fahrenheit_symbol));
			temp = TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil
					.kelvinToFahrenheit(data.getTempKelvinMax()));
			textViewMaxTemp.setText(temp + getString(R.string.fahrenheit_symbol));
			temp = TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil
					.kelvinToFahrenheit(data.getTempKelvinMin()));
			textViewMinTemp.setText(temp + getString(R.string.fahrenheit_symbol));
		}
		textViewWeatherType.setText(data.getWeatherType());
		textViewWeatherDescription.setText(data.getWeatherDescription());
		textViewHumidity.setText(data.getHumidity() + "");
		String date = DateUtil.dateInDay_DD_MM_YYYYFormat(data.getDate());
		textViewDate.setText(date);
		textViewLastUpdated.setText(DateUtil.dateInDay_DD_MM_YYYY_HH_MMFormat(Calendar.getInstance()));
	}

	/**
	 * On click listener class
	 */
	private class clickListner implements OnClickListener {
		@Override
		public void onClick(View v) {
			WeatherData data = mlLocationData.getWeatherData().get(v.getId());
			populateData(data);
		}
	}

	/**
	 * Helper method to get the large image to be displayed on main view
	 * 
	 * @param String
	 *            Weather Type
	 * @param boolean isNight
	 */
	private Drawable getLargeImage(String weatherType, boolean isNight) {
		Drawable imageDrawable = null;

		if (Constants.TYPE_CLOUD.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_cloud_large);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_cloud_large);
		} else if (Constants.TYPE_CLEAR.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_clear_large);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_clear_large);
		} else if (Constants.TYPE_RAIN.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_rain_large);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_rain_large);
		} else if (Constants.TYPE_SNOW.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_snow_large);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_snow_large);
		} else if (Constants.TYPE_THUNDERSTORM.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_rain_large);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_rain_large);
		} else {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_cloud_large);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_cloud_large);
		}
		return imageDrawable;
	}

	/**
	 * Helper method to get the small image to be displayed on horizontal scroll view
	 * 
	 * @param String
	 *            Weather Type
	 * @param boolean isNight
	 * @return Drawable Image
	 */
	private Drawable getSmallImage(String weatherType, boolean isNight) {
		Drawable imageDrawable = null;

		if (Constants.TYPE_CLOUD.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_cloud_small);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_cloud_small);
		} else if (Constants.TYPE_CLEAR.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_clear_small);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_clear_small);
		} else if (Constants.TYPE_RAIN.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_rain_small);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_rain_small);
		} else if (Constants.TYPE_SNOW.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_snow_small);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_snow_small);
		} else if (Constants.TYPE_THUNDERSTORM.contains(weatherType)) {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_rain_small);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_rain_small);
		} else {
			if (isNight)
				imageDrawable = getResources().getDrawable(R.drawable.night_cloud_small);
			else
				imageDrawable = getResources().getDrawable(R.drawable.day_cloud_small);
		}
		return imageDrawable;
	}

	/**
	 * Helper method to show the error Toast
	 * 
	 * @param String
	 *            message
	 */

	private void showErrorTaost(String message) {
		Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Helper method that converts the temperature from Fahrenheit to Celsius and vice versa
	 */
	private void convertTemperature() {
		try {
			String val = textViewCurrentTemp.getText().toString().split(" ")[0];
			if (val != null)
				setTempValues(textViewCurrentTemp, Float.parseFloat(val));
			val = textViewMaxTemp.getText().toString().split(" ")[0];
			if (val != null)
				setTempValues(textViewMaxTemp, Float.parseFloat(val));
			val = textViewMinTemp.getText().toString().split(" ")[0];
			if (val != null)
				setTempValues(textViewMinTemp, Float.parseFloat(val));

		} catch (NumberFormatException e) {
			// showErrorTaost(getString(R.string.error_string_some_error_has_occurred));
		}
	}

	/**
	 * Helper method for convert temperature to calculate and set the values
	 */
	private void setTempValues(TextView textView, float value) {
		if (!isCelsius)
			textView.setText(TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil
					.CelsiusToFahrenheit(value)) + getString(R.string.fahrenheit_symbol));
		else
			textView.setText(TemperatureConversionUtil.convertToTwoDecimalPlaces(TemperatureConversionUtil
					.FahrenheitToCelsius(value)) + getString(R.string.celsius_symbol));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(Constants.KEY_LOCATION, prevLocationName);
		outState.putBoolean(Constants.KEY_IS_SEARCH_BY_LOCATION, isSearchByLocation);
		super.onSaveInstanceState(outState);
	}
}
