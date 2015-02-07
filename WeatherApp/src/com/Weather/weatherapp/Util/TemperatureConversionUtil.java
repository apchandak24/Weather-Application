package com.Weather.weatherapp.Util;

/**
 * Utility class to convert temperature in different formats 
 */


public class TemperatureConversionUtil {
	
	/**
	 * Static method to convert kelvin to celsius
	 * @param float, value in kelvin
	 * @return float, value in celsius 
	 */	
	public static float kelvinToCelsius(float value){
		return value-273.15f;
	}
	
	public static String convertToTwoDecimalPlaces(float value){
		return String.format("%.2f", value);
	}
	/**
	 * Static method to convert kelvin to Fahrenheit
	 * @param float, value in kelvin
	 * @return float, value in fahrenheit 
	 */	
	public static float kelvinToFahrenheit(float value){
		float fHeit = 9/5*(value - 273) + 32;
		return fHeit;
	}

	/**
	 * Static method to convert Celsius to Fahrenheit
	 * @param float, value in celsius
	 * @return float, value in fahrenheit 
	 */	
	public static float CelsiusToFahrenheit(float value){
		float fHeit = (9/5.0f)*(value)   + 32;
		return fHeit;
	}
	/**
	 * Static method to convert Fahrenheit to celsius
	 * @param float, value in fahrenheit
	 * @return float, value in  celsius
	 */	
	public static float FahrenheitToCelsius(float value){
		float celsius = (5/9f)*(value - 32);
		return celsius;
	}
}
