/*
 * Created on Jan 30, 2007
 *
 */
package com.mindalliance.zk.mxgraph.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * @author jf
 *
 */
public class OverlayEvent extends Event {
	
	private String cellId;
	private String overlayId;

	public OverlayEvent(String name, Component target, String cellId, String overlayId) {
		super(name, target);
		this.cellId = cellId;
		this.overlayId = overlayId;
	}

	/**
	 * @return the cellId
	 */
	public String getCellId() {
		return cellId;
	}

	public String getOverlayId() {
		return overlayId;
	}
	
}
