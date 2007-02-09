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
public class VertexAddedEvent extends Event {
	
	private String cellId;

	public VertexAddedEvent(String name, Component target, String cellId) {
		super(name, target);
		this.cellId = cellId;
	}

	/**
	 * @return the cellId
	 */
	public String getCellId() {
		return cellId;
	}

}
