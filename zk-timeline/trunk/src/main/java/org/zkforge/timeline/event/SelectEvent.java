/**
 * 
 */
package org.zkforge.timeline.event;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * @author dfeeney
 *
 */
public class SelectEvent extends Event {
	private String[] ids;
	public SelectEvent(String name, Component target, List<Object> data, String[] ids) {
		super(name, target, data);
		this.ids=ids;
	}
	
	public String[] getIds() {
		return ids;
	}
}
