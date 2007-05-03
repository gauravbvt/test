/*
 * Created on Apr 30, 2007
 *
 */
package com.mindalliance.channels.data;

/**
 * A distance
 * @author jf
 *
 */
public class Distance {
	
	enum Unit {METER, FOOT, KILOMETER, MILE};
	
	private Double value;
	private Unit unit;
	
	/**
	 * @return the unit
	 */
	public Unit getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}

}
