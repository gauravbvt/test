/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;

/** 
 * Spatial coordinates.
 * @author jf
 *
 */
public class LatLong implements Serializable {
	
	//	 TODO make them degrees etc.
	private String latitude; 
	private String longitude;
	
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
