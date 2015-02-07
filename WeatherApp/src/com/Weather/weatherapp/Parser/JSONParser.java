package com.Weather.weatherapp.Parser;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.Weather.weatherapp.Bean.LocationData;
import com.Weather.weatherapp.Bean.WeatherData;

/**
 * Helper Class to parse the response in JSON format.
 */

public class JSONParser {
	/**
	 * Parse the JSON data.
	 * @param String 
	 * @return LocationData Object
	 */
	public LocationData parseData(String JSONString){
		int i = 0;
		JSONObject mJsonObject;
	
		ArrayList<WeatherData> weatherList = new ArrayList<>();
		LocationData mLocationData = new LocationData();
		mLocationData.getWeatherData().clear();
		try {
			mJsonObject = new JSONObject(JSONString);
			JSONObject cityObj = mJsonObject.getJSONObject("city");
			
			mLocationData.setCityName(cityObj.getString("name"));
			mLocationData.setcountryName(cityObj.getString("country"));
			
			JSONArray tempObj = mJsonObject.getJSONArray("list");
			while(i<tempObj.length()){
				WeatherData weatherData = new WeatherData();
				JSONObject tempDataArray = tempObj.getJSONObject(i);
				
				String dt = tempDataArray.getString("dt");
				long unixTS = Long.parseLong(dt);
				Calendar cDate = Calendar.getInstance();
				cDate.setTimeInMillis(unixTS*1000);
				weatherData.setDate(cDate);
				
				JSONObject tObj = tempDataArray.getJSONObject("temp");
				weatherData.setTempKelvinCurrent(Float.parseFloat(tObj.getString("day")));
				weatherData.setTempKelvinMin(Float.parseFloat(tObj.getString("min")));
				weatherData.setTempKelvinMax(Float.parseFloat(tObj.getString("max")));
				
				weatherData.setHumidity(Integer.parseInt(tempDataArray.getString("humidity")));
				
				JSONArray weatherObjArray = tempDataArray.getJSONArray("weather");
				JSONObject mainWeather = weatherObjArray.getJSONObject(0);
				weatherData.setWeatherType(mainWeather.getString("main"));
				weatherData.setWeatherDescription(mainWeather.getString("description"));
				String iconType = mainWeather.getString("icon");
				if(iconType.contains("n"))
					weatherData.setNight(true);
				else if(iconType.contains("n"))
					weatherData.setNight(false);
				weatherList.add(weatherData);
				i++;
			}
			mLocationData.setWeatherData(weatherList);
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return mLocationData;
	}

}
