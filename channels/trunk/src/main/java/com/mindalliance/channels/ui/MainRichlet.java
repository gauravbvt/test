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
import com.mindalliance.zk.mxgraph.MxCellListener;
import com.mindalliance.zk.mxgraph.MxCompactTreeLayout;
import com.mindalliance.zk.mxgraph.MxConstants;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxOverlay;
import com.mindalliance.zk.mxgraph.MxPanningHandler;
import com.mindalliance.zk.mxgraph.MxStyleSheet;
import com.mindalliance.zk.mxgraph.MxVertex;
import com.mindalliance.zk.mxgraph.dto.Menu;

public class MainRichlet extends GenericRichlet {

	public void service(Page page) {
		page.setTitle("mxGraph test");
		Window w = new Window("mxGraph", "normal", false);
		MxGraph graph = new MxGraph();
		//graph.setLayout(new MxCircleLayout(40));
		//graph.setLayout(new MxFastOrganicLayout());
		graph.setLayout(new MxCompactTreeLayout());
		graph.setWidth("800px");
		graph.setHeight("800px");
		graph.setProperty(MxGraph.AUTO_SIZE, "true", true);
		graph.setStyle("overflow:hidden; background:url('/channels/images/grid.gif');"); // overflow:hidden not needed anymore?
		// graph.setProperty(MxGraph.BACKGROUND_IMAGE, "/channels/images/grid.gif", false);
		// graph.setProperty(MxGraph.AUTO_SIZE, true, false);
		graph.getPanningHandler().setProperty(MxPanningHandler.IS_SELECT_ON_POPUP, false, false);
		MxVertex node1 = graph.addVertex("Node A", 0, 0, 60, 30);
		//MxVertex node1 = new MxVertex("Node A", 0,0,60,30);
		
		MxVertex node2 = graph.addVertex("Node B", 100, 0, 60, 30);
		final MxOverlay addOverlay = new MxOverlay("/channels/images/16x16/add2.png", "Create a new node", 0,0,16, 16);
		final MxOverlay deleteOverlay = new MxOverlay("/channels/images/16x16/delete2.png", "Delete this node", 0,16,16, 16);
		deleteOverlay.addClickListener(new MxCellListener() {
			public void onEvent(MxGraph graph, MxCell cell) {
				graph.deleteCell(cell.getId());
			}
		});
		addOverlay.addClickListener(new MxCellListener() {
			public void onEvent(MxGraph graph, MxCell cell) {
				MxVertex vertex = new MxVertex(cell.getId(), 0,0,60,30);
				vertex.addOverlay(addOverlay);
				vertex.addOverlay(deleteOverlay);
				graph.addVertex(vertex);
				if (cell instanceof MxVertex) {
					graph.addEdge("Edgy", (MxVertex)cell, vertex);
				}
			}
		});


		
		//node1.addOverlay(overlay);
		//graph.addVertex(node1);
		graph.addOverlay(node1, addOverlay);
		graph.addOverlay(node1, deleteOverlay);
		graph.addOverlay(node2, addOverlay);
		graph.addOverlay(node2, deleteOverlay);		

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
