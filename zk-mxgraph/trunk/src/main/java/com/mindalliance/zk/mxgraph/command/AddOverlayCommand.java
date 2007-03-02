/*
 * Created on Feb 7, 2007
 *
 */
package com.mindalliance.zk.mxgraph.command;

import org.zkoss.lang.Objects;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.UiException;

import com.mindalliance.zk.mxgraph.MxCell;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxOverlay;

public class AddOverlayCommand extends AbstractCommand {

	public AddOverlayCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	@Override
	// data :: {cell id, image, tooltip, width, height}
	protected void processRequest(MxGraph graph, String[] data) {
		if (data == null || data.length != 5)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});
		String name = data[1];
		MxCell cell = graph.getModel().getCell(name);
		MxOverlay overlay = new MxOverlay(data[1], data[2]);
		graph.addOverlay(cell, overlay);
	}

}
