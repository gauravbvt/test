/**
 * 
 */
package com.mindalliance.zk.mxgraph.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * @author dfeeney
 *
 */
public class AddListenerEvent extends Event {
	public String eventID;
	
	public AddListenerEvent(String name, Component comp, String eventID) {
		super(name, comp);
		this.eventID = eventID;
	}
	
	public String getEventID() {
		return eventID;
	}
	
}
