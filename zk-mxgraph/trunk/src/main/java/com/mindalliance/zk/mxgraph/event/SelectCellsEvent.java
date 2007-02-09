/*
 * Created on Jan 30, 2007
 *
 */
package com.mindalliance.zk.mxgraph.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class SelectCellsEvent extends Event {

	private String[] selection;
	
	public SelectCellsEvent(String name, Component target, String[] selection) {
		super(name, target);
		this.selection = selection;
	}

	/**
	 * @return the selection
	 */
	public String[] getSelection() {
		return selection;
	}

}
