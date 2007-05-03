/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.Map;

import com.mindalliance.channels.util.GUID;

/**
 * Channels' audit log and element lifecycle.
 * @author jf
 *
 */
public class History extends AbstractQueryable {

	private Map<GUID,ElementHistory> elementHistories;
	
	// TODO Access to system usage log, system metrics over time, system events etc

	/**
	 * @return the elementHistories
	 */
	public Map<GUID, ElementHistory> getElementHistories() {
		return elementHistories;
	}

	/**
	 * @param elementHistories the elementHistories to set
	 */
	public void setElementHistories(Map<GUID, ElementHistory> elementHistories) {
		this.elementHistories = elementHistories;
	}
}
