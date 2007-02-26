/*
 * Created on Jan 30, 2007
 *
 */
package com.mindalliance.zk.mxgraph.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class DeleteCellsEvent extends Event {
	
	private String[] ids;

	public DeleteCellsEvent(String name, Component comp, String[] ids) {
		super(name, comp);
		this.ids=ids;
	}

	/**
	 * @return the ids
	 */
	public String[] getIds() {
		return ids;
	}

}
