/*
 * Created on Jan 31, 2007
 *
 */
package com.mindalliance.channels.ui;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxVertex;

public class MainRichlet extends GenericRichlet {

	public void service(Page page) {
		page.setTitle("mxGraph test");
		Window w = new Window("mxGraph", "normal", false);
		MxGraph graph = new MxGraph();
		graph.setWidth("200px");
		graph.setHeight("200px");
		MxVertex vertex = new MxVertex("Test");
		graph.addCell(vertex);
		graph.setParent(w);
		w.setPage(page);
	}

}
