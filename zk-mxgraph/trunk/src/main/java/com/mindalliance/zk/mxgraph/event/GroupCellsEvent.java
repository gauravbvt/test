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
public class GroupCellsEvent extends Event {
	private String groupId;
	private String[] cells;
	public GroupCellsEvent(String name, Component target, String groupId, String[] cells) {
		super(name, target);
		this.groupId = groupId;
		this.cells = cells;
	}
	/**
	 * @return the cells
	 */
	public String[] getCells() {
		return cells;
	}
	/**
	 * @param cells the cells to set
	 */
	public void setCells(String[] cells) {
		this.cells = cells;
	}
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
