package com.Weather.weatherapp.Bean;

import java.util.Calendar;

import com.Weather.weatherapp.Util.DateUtil;

/**
 * Bean class to store Location Data Data Members: Current temperature in kelvin Minimum temperature in kelvin Maximum
 * temperature in kelvin Weather Type Weather Description Date Humidity
 */
public class WeatherData {

	private int humidity;
	private float tempKelvinMin;
	private float tempKelvinMax;
	private float tempKelvinCurrent;
	private String weatherDescription;
	private String weatherType;
	private Calendar date;
	private boolean isNight;

	public float getTempKelvinMin() {
		return tempKelvinMin;
	}

	public void setTempKelvinMin(float tempKelvinMin) {
		this.tempKelvinMin = tempKelvinMin;
	}

	public float getTempKelvinMax() {
		return tempKelvinMax;
	}

	public void setTempKelvinMax(float tempKelvinMax) {
		this.tempKelvinMax = tempKelvinMax;
	}

	public String getWeatherDescription() {
		return weatherDescription;
	}

	public void setWeatherDescription(String weatherDescription) {
		this.weatherDescription = weatherDescription;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getWeatherType() {
		return weatherType;
	}

	public void setWeatherType(String mainWeather) {
		this.weatherType = mainWeather;
	}

	@Override
	public String toString() {
		return tempKelvinMin + " " + tempKelvinMax + " " + weatherType + " " + weatherDescription + " "
				+ DateUtil.dateInDay_DD_MM_YYYYFormat(date);
	}

	public float getTempKelvinCurrent() {
		return tempKelvinCurrent;
	}

	public void setTempKelvinCurrent(float tempKelvinCurrent) {
		this.tempKelvinCurrent = tempKelvinCurrent;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public boolean isNight() {
		return isNight;
	}

	public void setNight(boolean isNight) {
		this.isNight = isNight;
	}
}
