/*
 * Created on Feb 6, 2007
 *
 */
package com.mindalliance.zk.mxgraph.command;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import com.mindalliance.zk.mxgraph.MxGraph;

abstract public class AbstractCommand extends Command {

	public AbstractCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	@Override
	public void process(AuRequest request) {
		final Component comp = request.getComponent();
		if (comp == null || !(comp instanceof MxGraph))
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		final String[] data = request.getData();
		MxGraph graph = (MxGraph)comp;
		processRequest(graph, data);
	}
	
	abstract protected void processRequest(MxGraph graph, String[] data);

}
