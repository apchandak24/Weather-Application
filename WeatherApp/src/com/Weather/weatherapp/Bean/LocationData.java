package com.Weather.weatherapp.Bean;

import java.util.ArrayList;
/**
 * Bean class to store Location Data
 * Data Members:
 * Location Name
 * Country Name
 * ArrayList of WeatherData depending upon request count
 */
public class LocationData {
	
	private String cityName;
	private String countryName;
	private ArrayList<WeatherData> weatherData = new ArrayList<>();
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getcountryName() {
		return countryName;
	}
	public void setcountryName(String countryName) {
		this.countryName = countryName;
	}
	public ArrayList<WeatherData> getWeatherData() {
		return weatherData;
	}
	public void setWeatherData(ArrayList<WeatherData> weatherData) {
		this.weatherData = weatherData;
	}
	@Override
	public String toString() {
		return cityName+" "+countryName+" "+weatherData.toString()+"\n";
	}

}
