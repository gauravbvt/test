/*
 * Created on Jan 29, 2007
 *
 */
package com.mindalliance.zk.mxgraph.command;

import com.mindalliance.zk.mxgraph.MxGraph;


public class SelectCellsCommand  extends AbstractCommand {
	
	public SelectCellsCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}
	
	@Override
	protected void processRequest(MxGraph graph, String[] data) {
		String[] ids = (data == null ? new String[0] : data);
		graph.setSelection(ids, false); // don't update client
	}

}
