/**
 * 
 */
package com.mindalliance.zk.mxgraph.event;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.mindalliance.zk.mxgraph.MxCell;
import com.mindalliance.zk.mxgraph.MxGraph;

/**
 * @author dfeeney
 *
 */

public abstract class OverlayClickListener implements org.zkoss.zk.ui.event.EventListener {
	public void onEvent(Event event) throws UiException {
		OverlayClickEvent ocEvent = (OverlayClickEvent)event;
		MxGraph graph = (MxGraph)event.getTarget();
		MxCell cell = graph.getModel().getCell(ocEvent.getCellId());
		onClick(graph, cell);
	}
	public abstract void onClick(MxGraph graph, MxCell cell);
	public boolean isAsap() {
		return true;
	}
}
