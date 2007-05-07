/**
 * 
 */
package org.zkforge.timeline.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * @author dfeeney
 *
 */
public class SelectEvent extends Event {
	private String[] ids;
	public SelectEvent(String name, Component target, String[] ids) {
		super(name, target, ids);
		this.ids=ids;
	}
	
	public String[] getIds() {
		return ids;
	}
}
