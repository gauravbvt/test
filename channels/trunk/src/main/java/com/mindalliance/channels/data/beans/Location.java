/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;

import com.mindalliance.channels.data.Distance;
import com.mindalliance.channels.data.LatLong;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A place, position etc.
 * @author jf
 *
 */
public class Location extends ReferenceData {

	private LatLong latLong;
	private Distance radius;
	private List<Location> within;
	private List<Location> nextTo;
	
	/**
	 * @return the latLong
	 */
	public LatLong getLatLong() {
		return latLong;
	}
	/**
	 * @param latLong the latLong to set
	 */
	public void setLatLong(LatLong latLong) {
		this.latLong = latLong;
	}
	/**
	 * @return the nextTo
	 */
	public List<Location> getNextTo() {
		return nextTo;
	}
	/**
	 * @param nextTo the nextTo to set
	 */
	public void setNextTo(List<Location> nextTo) {
		this.nextTo = nextTo;
	}
	/**
	 * @return the radius
	 */
	public Distance getRadius() {
		return radius;
	}
	/**
	 * @param radius the radius to set
	 */
	public void setRadius(Distance radius) {
		this.radius = radius;
	}
	/**
	 * @return the within
	 */
	public List<Location> getWithin() {
		return within;
	}
	/**
	 * @param within the within to set
	 */
	public void setWithin(List<Location> within) {
		this.within = within;
	}
	
}
