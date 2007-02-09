/*
 * Created on Feb 7, 2007
 *
 */
package com.mindalliance.zk.mxgraph.command;

import org.zkoss.lang.Objects;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.UiException;

import com.mindalliance.zk.mxgraph.MxEdge;
import com.mindalliance.zk.mxgraph.MxGraph;

public class AddEdgeCommand extends AbstractCommand {

	public AddEdgeCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	@Override
	// data :: {target id, name}
	protected void processRequest(MxGraph graph, String[] data) {
		String[] selection = graph.getSelection();
		if(data == null || data.length != 2 || selection.length != 1) 
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
					new Object[] {Objects.toString(data), this});
		String sourceId = graph.getSelection()[0];  // the source vertex is the one currently selected
		String name = data[1];
		String targetId = data[0];
		MxEdge edge = new MxEdge(name, sourceId, targetId);
		graph.addEdge(edge);
	}

}
