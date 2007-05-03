/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.system;

import java.util.Map;

import com.mindalliance.channels.data.support.VersionHistory;
import com.mindalliance.channels.util.GUID;

/**
 * Channels' audit log and element lifecycle.
 * @author jf
 *
 */
public class History extends AbstractQueryable {

	private Map<GUID,VersionHistory> elementHistories;
	
	// TODO Access to system usage log, system metrics over time, system events etc

	/**
	 * @return the elementHistories
	 */
	public Map<GUID, VersionHistory> getElementHistories() {
		return elementHistories;
	}

	/**
	 * @param elementHistories the elementHistories to set
	 */
	public void setElementHistories(Map<GUID, VersionHistory> elementHistories) {
		this.elementHistories = elementHistories;
	}
}
