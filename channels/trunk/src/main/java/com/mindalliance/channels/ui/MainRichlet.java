/*
 * Created on Jan 31, 2007
 *
 */
package com.mindalliance.channels.ui;

import java.util.Map;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

import com.mindalliance.zk.mxgraph.MxCell;
import com.mindalliance.zk.mxgraph.MxCircleLayout;
import com.mindalliance.zk.mxgraph.MxConstants;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxOverlay;
import com.mindalliance.zk.mxgraph.MxPanningHandler;
import com.mindalliance.zk.mxgraph.MxStyleSheet;
import com.mindalliance.zk.mxgraph.MxVertex;
import com.mindalliance.zk.mxgraph.command.ClickOverlayCommand;
import com.mindalliance.zk.mxgraph.dto.Menu;
import com.mindalliance.zk.mxgraph.event.OverlayClickListener;

public class MainRichlet extends GenericRichlet {

	public void service(Page page) {
		page.setTitle("mxGraph test");
		Window w = new Window("mxGraph", "normal", false);
		MxGraph graph = new MxGraph();
		graph.setLayout(new MxCircleLayout(40));
		graph.setWidth("400px");
		graph.setHeight("400px");
		
		graph.setStyle("overflow:hidden; background:url('/channels/images/grid.gif');"); // overflow:hidden not needed anymore?
		// graph.setProperty(MxGraph.BACKGROUND_IMAGE, "/channels/images/grid.gif", false);
		// graph.setProperty(MxGraph.AUTO_SIZE, true, false);
		graph.getPanningHandler().setProperty(MxPanningHandler.IS_SELECT_ON_POPUP, false, false);
		MxVertex node1 = graph.addVertex("Node A", 0, 0, 60, 30);
		MxVertex node2 = graph.addVertex("Node B", 0, 0, 60, 30);
		MxOverlay overlay = new MxOverlay("/channels/images/16x16/add2.png", "Click to create a new node", 0,0,16, 16);
		overlay.addClickListener(new OverlayClickListener() {
			public void onClick(MxGraph graph, MxCell cell) {
				MxVertex vertex = new MxVertex(cell.getId(), 0,0,60,30);
				MxOverlay overlay = new MxOverlay("/channels/images/16x16/arrow_right_green.png", "Using the same listener", 0,0,16, 16);
				overlay.addClickListener(this);
				vertex.setOverlay(overlay);
				graph.addVertex(vertex);
				if (cell instanceof MxVertex) {
					graph.addEdge("Edgy", (MxVertex)cell, vertex);
				}
			}
		});
		graph.setOverlay(node1, overlay);
		

		graph.addEdge("Edge 1", node1, node2);
		// Define default vertex menu
		Menu menu = Menu.named(Menu.CELL)
						.item("Delete", "/channels/images/16x16/delete2.png", MxConstants.COMMAND_DELETE)
						.separator()
						.item("Add vertex", "/channels/images/16x16/add2.png", MxConstants.COMMAND_ADD_VERTEX, "Node C")
						.item("Add edge", "/channels/images/16x16/arrow_right_green.png", MxConstants.COMMAND_ADD_EDGE, "New");
		graph.addMenu(menu, false);
		// Define default vertex style
		MxStyleSheet sheet = graph.getStyleSheet();
		Map<String,String> style = sheet.getDefaultVertexStyle();
		style.put(MxConstants.STYLE_SHAPE, MxConstants.SHAPE_ELLIPSE);
		style.put(MxConstants.STYLE_PERIMETER, MxConstants.STYLE_VERTEX_ELLIPSE_PERIMETER);
		style.put(MxConstants.STYLE_GRADIENTCOLOR, "white");
		style.put(MxConstants.STYLE_FONTSIZE, "10");
		graph.setParent(w);
		w.setPage(page);
	}
	
}
