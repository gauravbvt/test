/*
 * Created on Feb 7, 2007
 *
 */
package com.mindalliance.zk.mxgraph.command;

import org.zkoss.lang.Objects;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.UiException;

import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxVertex;

public class AddVertexCommand extends AbstractCommand {

	public AddVertexCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	@Override
	// data :: {cell id, name}
	protected void processRequest(MxGraph graph, String[] data) {
		if (data == null || data.length != 2)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});
		String name = data[1];
		MxVertex vertex = new MxVertex(name);
		graph.addVertex(vertex);
	}

}
