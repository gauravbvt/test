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
public class ZoomEvent extends Event {
	public ZoomEvent(String name, Component comp) {
		super(name, comp);
	}
}
