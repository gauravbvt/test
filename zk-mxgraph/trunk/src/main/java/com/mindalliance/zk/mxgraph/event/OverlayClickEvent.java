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
public class OverlayClickEvent extends Event {
	private String cellId;
	public OverlayClickEvent(String name, Component comp, String cellId) {
		super(name, comp);
		this.cellId = cellId;
	}
	
	public String getCellId() {
		return cellId;
	}
}
