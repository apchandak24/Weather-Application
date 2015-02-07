package com.Weather.weatherapp.Application;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class WeatherApplication extends Application {

	private static WeatherApplication appInstance;
	private RequestQueue mRequestQueue;

	@Override
	public void onCreate() {
		super.onCreate();
		appInstance = this;
	}

	public RequestQueue getmRequestQueue() {
		if (mRequestQueue == null)
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		return mRequestQueue;
	}

	/**
	 * Get the application instance
	 * 
	 * @return WeatherApplication
	 */
	public static WeatherApplication getInstance() {
		return appInstance;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(tag);
		getmRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}
