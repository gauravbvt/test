/**
 * 
 */
package com.mindalliance.zk.mxgraph.command;

import com.mindalliance.zk.mxgraph.MxCell;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxOverlay;
import com.mindalliance.zk.mxgraph.event.OverlayClickListener;

/**
 * @author dfeeney
 *
 */
public class ClickOverlayCommand extends AbstractCommand {

	/**
	 * @param evtnm
	 * @param flags
	 */
	public ClickOverlayCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.zk.mxgraph.command.AbstractCommand#processRequest(com.mindalliance.zk.mxgraph.MxGraph, java.lang.String[])
	 */
	@Override
	protected void processRequest(MxGraph graph, String[] data) {
		String cellName = data[0];
		MxCell cell = graph.getModel().getCell(cellName);
		MxOverlay ol = cell.getOverlay();
		if (ol != null) {
			for (OverlayClickListener ocl : ol.getClickListeners()) {
				ocl.onClick(graph, cell);
			}
		}
	}
}
