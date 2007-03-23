package org.zkforge.timeline.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimelineUtil {
	private final static SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzzZ", Locale.US);

	public static String formatDateTime(Date date) {
		String temp=sdf.format(date);
		String ret=temp.substring(0,26)+"GMT"+temp.substring(29);
		return ret;
		//return date.toGMTString();
		//return sdf.format(date);

	}

	public static String convertIntervalUnitFromName(String unitName) {
		String ret = null;
		unitName = unitName.toUpperCase();
		if ("MILLISECOND".equals(unitName))
			ret = "0";
		else if ("SECOND".equals(unitName))
			ret = "1";
		else if ("MINUTE".equals(unitName))
			ret = "2";
		else if ("HOUR".equals(unitName))
			ret = "3";
		else if ("DAY".equals(unitName))
			ret = "4";
		else if ("WEEK".equals(unitName))
			ret = "5";
		else if ("MONTH".equals(unitName))
			ret = "6";
		else if ("YEAR".equals(unitName))
			ret = "7";
		else if ("DECADE".equals(unitName))
			ret = "8";
		else if ("CENTURY".equals(unitName))
			ret = "9";
		else if ("MILLENNIUM".equals(unitName))
			ret = "10";
		else if ("EPOCH".equals(unitName))
			ret = "-1";
		else if ("ERA".equals(unitName))
			ret = "-2";
		return ret;
	}
}
