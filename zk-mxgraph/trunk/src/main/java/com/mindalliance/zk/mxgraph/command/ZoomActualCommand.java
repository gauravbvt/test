/**
 * 
 */
package com.mindalliance.zk.mxgraph.command;

import com.mindalliance.zk.mxgraph.MxGraph;

/**
 * @author dfeeney
 *
 */
public class ZoomActualCommand extends AbstractCommand {

	/**
	 * @param evtnm
	 * @param flags
	 */
	public ZoomActualCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	/* (non-Javadoc)
	 * @see com.mindalliance.zk.mxgraph.command.AbstractCommand#processRequest(com.mindalliance.zk.mxgraph.MxGraph, java.lang.String[])
	 */
	@Override
	protected void processRequest(MxGraph graph, String[] data) {
		graph.zoomToActual();
	}

}
