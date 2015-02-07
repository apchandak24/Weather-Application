package com.Weather.weatherapp.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility class to represent date in different formats
 */

public class DateUtil {

	/**
	 * Format date in Day dd-MM format
	 */
	public static String dateInDay_DD_MMFormat(Calendar date) {
		SimpleDateFormat dateFormater = new SimpleDateFormat("E dd-MMM");
		return dateFormater.format(date.getTime());
	}

	/**
	 * Format date in Day dd MMM YYYY format
	 */
	public static String dateInDay_DD_MM_YYYYFormat(Calendar date) {
		SimpleDateFormat dateFormater = new SimpleDateFormat("E',' dd MMM yyyy");
		return dateFormater.format(date.getTime());
	}

	/**
	 * Format date in Day dd MMM YYYY HH:mm format
	 */
	public static String dateInDay_DD_MM_YYYY_HH_MMFormat(Calendar date) {
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy 'at' hh:mm");
		return dateFormater.format(date.getTime());
	}

	
}
