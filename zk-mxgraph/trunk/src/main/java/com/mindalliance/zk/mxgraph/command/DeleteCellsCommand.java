/*
 * Created on Feb 4, 2007
 *
 */
package com.mindalliance.zk.mxgraph.command;

import org.zkoss.lang.Objects;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.UiException;

import com.mindalliance.zk.mxgraph.MxGraph;


public class DeleteCellsCommand extends AbstractCommand {

	public DeleteCellsCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	@Override
	protected void processRequest(MxGraph graph, String[] data) {
		if (data == null || data.length == 0)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});
		graph.deleteCells(data);
	}



}
