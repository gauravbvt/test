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
import com.mindalliance.zk.mxgraph.MxEdge;
import com.mindalliance.zk.mxgraph.MxGraph;
import com.mindalliance.zk.mxgraph.MxOverlay;
import com.mindalliance.zk.mxgraph.MxPanningHandler;
import com.mindalliance.zk.mxgraph.MxStyleHelper;
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
		graph.setStyle("overflow:hidden; background:url('/system/images/grid.gif');"); // overflow:hidden not needed anymore?
		// graph.setProperty(MxGraph.BACKGROUND_IMAGE, "/system/images/grid.gif", false);
		// graph.setProperty(MxGraph.AUTO_SIZE, true, false);
		graph.getPanningHandler().setProperty(MxPanningHandler.IS_SELECT_ON_POPUP, false, false);
		graph.getPanningHandler().setProperty(MxPanningHandler.IS_USE_SHIFT_KEY, true, false);
		graph.getPanningHandler().setProperty(MxPanningHandler.IS_PAN_ENABLED, true, false);
		//MxVertex node1 = graph.addVertex("Node A", 0, 0, 60, 30);
		MxVertex node1 = new MxVertex("Node A", 0,0,60,30);
		node1.setStyle("actor");
		graph.addVertex(node1);
		MxVertex node2 = graph.addVertex("Node B", 100, 0, 60, 30);

		MxEdge edge1 = graph.addEdge("Edge 1", node1, node2);
		
		final MxOverlay addOverlay = new MxOverlay("/system/images/16x16/add2.png", "Create a new node", 0,0,16, 16);
		final MxOverlay deleteOverlay = new MxOverlay("/system/images/16x16/delete2.png", "Delete this node", 0,16,16, 16);
		final MxOverlay zoomOverlay = new MxOverlay("/system/images/16x16/arrow_right_green.png", "Zoom In", 16,0,16,16);
		final MxOverlay groupOverlay = new MxOverlay("/system/images/16x16/arrow_right_green.png", "Group nodes", 16,16,16,16);
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
				vertex.setParent(cell.getParent());
				graph.addVertex(vertex);
				if (cell instanceof MxVertex) {
					graph.addEdge("", (MxVertex)cell, vertex);
				}
			}
		});
		zoomOverlay.addClickListener(new MxCellListener() {
			public void onEvent(MxGraph graph, MxCell cell) {
				graph.zoomIn();
			}
		});
		final String node1Id = node1.getId();
		final String node2Id = node2.getId();
		final String edge1Id = edge1.getId();
		groupOverlay.addClickListener(new MxCellListener() {
			public void onEvent(MxGraph graph, MxCell cell) {
				final MxVertex group = new MxVertex("");
				//group.setStyle("swimlane");
				graph.groupCells(group, new String[] {node1Id, node2Id, edge1Id});
				MxOverlay ungroupOverlay = new MxOverlay("/system/images/16x16/delete2.png", "Ungroup nodes", 0,0,16,16);
				ungroupOverlay.addClickListener(new MxCellListener() {
					public void onEvent(MxGraph graph, MxCell cell) {
						graph.ungroupCells(group);
						
					}
				});
				graph.addOverlay(group, ungroupOverlay);
			}
		});
		
		
		//node1.addOverlay(overlay);
		//graph.addVertex(node1);
		graph.addOverlay(node1, addOverlay);
		graph.addOverlay(node1, deleteOverlay);
		graph.addOverlay(node1, groupOverlay);
		graph.addOverlay(node2, addOverlay);
		graph.addOverlay(node2, deleteOverlay);
		graph.addOverlay(node2, zoomOverlay);

		// Define default vertex menu
		
		Menu menu = Menu.named(Menu.CELL)
						.item("Delete", "/system/images/16x16/delete2.png", MxConstants.COMMAND_DELETE)
						.separator()
						.item("Add vertex", "/system/images/16x16/add2.png", MxConstants.COMMAND_ADD_VERTEX, "Node C")
						.item("Add edge", "/system/images/16x16/arrow_right_green.png", MxConstants.COMMAND_ADD_EDGE, "New");
		graph.addMenu(menu, false);
		menu = Menu.named(Menu.ALL).separator()
						.item("Zoom In","/system/images/16x16/arrow_right_green.png", MxConstants.COMMAND_ZOOM_IN)
						.item("Zoom Out","/system/images/16x16/arrow_right_green.png", MxConstants.COMMAND_ZOOM_OUT)
						.item("Zoom Fit","/system/images/16x16/arrow_right_green.png", MxConstants.COMMAND_ZOOM_FIT)
						.item("Zoom Actual","/system/images/16x16/arrow_right_green.png", MxConstants.COMMAND_ZOOM_ACTUAL);
		graph.addMenu(menu, false);
		// Define default vertex style
		MxStyleSheet sheet = graph.getStyleSheet();
		Map<String,String> style = sheet.getDefaultVertexStyle();
		style.put(MxConstants.STYLE_SHAPE, MxConstants.SHAPE_RECTANGLE);
		style.put(MxConstants.STYLE_PERIMETER, MxConstants.STYLE_VERTEX_ELLIPSE_PERIMETER);
		style.put(MxConstants.STYLE_GRADIENTCOLOR, "white");
		style.put(MxConstants.STYLE_FONTSIZE, "10");
		
		style = MxStyleHelper.getEllipseStyle();
		
		style.put(MxConstants.STYLE_VERTICAL_ALIGN, MxConstants.ALIGN_BOTTOM);
		sheet.putCellStyle("group", style);
		graph.setParent(w);
		w.setPage(page);
	}
	
}
