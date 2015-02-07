package com.Weather.weatherapp.Constants;
/**
 * Class storing Constants used in the application
 */
public class Constants {
	
	public static String WEATHER_LAT_LNG_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&cnt=10&mode=json";
	public static String WEATHER_LOCATION_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&cnt=10&mode=json";
	public static String TYPE_CLOUD = "Clouds";
	public static String TYPE_RAIN = "Rain";
	public static String TYPE_CLEAR = "Clear";
	public static String TYPE_SNOW = "Snow";
	public static String TYPE_THUNDERSTORM = "Thunderstorm";
	public static String KEY_LOCATION = "LOCATION NAME";
	public static String KEY_IS_SEARCH_BY_LOCATION = "IS SEARCH_BY LOCATION";
	public static String REQ_TAG = "WEATHER_INFO";

}
