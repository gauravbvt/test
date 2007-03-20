/*
 * Created on Jan 26, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;

import com.mindalliance.zk.mxgraph.command.AddEdgeCommand;
import com.mindalliance.zk.mxgraph.command.AddOverlayCommand;
import com.mindalliance.zk.mxgraph.command.AddVertexCommand;
import com.mindalliance.zk.mxgraph.command.ClickOverlayCommand;
import com.mindalliance.zk.mxgraph.command.DeleteCellsCommand;
import com.mindalliance.zk.mxgraph.command.SelectCellsCommand;
import com.mindalliance.zk.mxgraph.command.ZoomActualCommand;
import com.mindalliance.zk.mxgraph.command.ZoomFitCommand;
import com.mindalliance.zk.mxgraph.command.ZoomInCommand;
import com.mindalliance.zk.mxgraph.command.ZoomOutCommand;
import com.mindalliance.zk.mxgraph.dto.Menu;
import com.mindalliance.zk.mxgraph.event.DeleteCellsEvent;
import com.mindalliance.zk.mxgraph.event.EdgeAddedEvent;
import com.mindalliance.zk.mxgraph.event.GroupCellsEvent;
import com.mindalliance.zk.mxgraph.event.OverlayEvent;
import com.mindalliance.zk.mxgraph.event.SelectCellsEvent;
import com.mindalliance.zk.mxgraph.event.StyleAddedEvent;
import com.mindalliance.zk.mxgraph.event.UngroupCellsEvent;
import com.mindalliance.zk.mxgraph.event.VertexAddedEvent;
import com.mindalliance.zk.mxgraph.event.ZoomEvent;

/**
 * A ZK component that wraps an mxGraph diagram.  In order to maintain consistency between the client graph instance and the server model,
 * all model and graph state changes are routed through this class.
 * 
 * <code>
 *      // Construct a new 800x800 graph<br>
 * 		MxGraph graph = new MxGraph();<br>
 * 		graph.setLayout(new MxCompactTreeLayout());<br>
 * 		graph.setWidth("800px");<br>
 *		graph.setHeight("800px");<br>
 *  </code>
 * <p>When a graph is created, it must be configured with a layout and .
 *
 * <p><b>Panning</b>
 * <p>Configuring graph panning is handled by setting properties on the MxPanningHandler class.  The graph's panningHandler instance should 
 * be retrieved via {@link #getPanningHandler()}.
 * MxPanningHandler is also used to configure the pop up menus for the graph and individual nodes.
 * 
 * <p><code>
 *      // Enable panning and associate it with shift+left click<br>
 *		graph.getPanningHandler().setProperty(MxPanningHandler.IS_USE_SHIFT_KEY, true, false);<br>
 * 		graph.getPanningHandler().setProperty(MxPanningHandler.IS_PAN_ENABLED, true, false);<br>
 * </code>
 * 
 * <p><b>Styles</b>
 * <p>An MxGraph maintains the set of available cell styles in a stylesheet.  The stylesheet maintains a list of named styles.  Setting the
 * style attribute on a particular cell to one of these names will cause the cell to be rendered using the corresponding style.  Each style consists of a Map 
 * whose keys correspond to the values in {@link MxConstants}  that start with STYLE_.  
 * 
 * <p>The shape of a cell can be specified by setting the MxConstants.STYLE_SHAPE style.  The available shapes are specified in {@link MxConstants} as well.
 * A set of styles corresponding to particular shapes are available from static methods {@link MxStyleHelper}.
 */
@SuppressWarnings("serial")
public class MxGraph extends HtmlBasedComponent {
	
	//register the mxGraph events
	static {	
		new SelectCellsCommand(MxConstants.COMMAND_SELECT, Command.IGNORE_OLD_EQUIV);
		new DeleteCellsCommand(MxConstants.COMMAND_DELETE, Command.IGNORE_OLD_EQUIV);
		new AddVertexCommand(MxConstants.COMMAND_ADD_VERTEX, Command.IGNORE_OLD_EQUIV);
		new AddEdgeCommand(MxConstants.COMMAND_ADD_EDGE, Command.IGNORE_OLD_EQUIV);
		new AddOverlayCommand(MxConstants.COMMAND_ADD_OVERLAY, Command.IGNORE_OLD_EQUIV);
		new ClickOverlayCommand(MxConstants.COMMAND_CLICK_OVERLAY, Command.IGNORE_OLD_EQUIV);
		new ZoomInCommand(MxConstants.COMMAND_ZOOM_IN, Command.IGNORE_OLD_EQUIV);
		new ZoomOutCommand(MxConstants.COMMAND_ZOOM_OUT, Command.IGNORE_OLD_EQUIV);
		new ZoomFitCommand(MxConstants.COMMAND_ZOOM_FIT, Command.IGNORE_OLD_EQUIV);
		new ZoomActualCommand(MxConstants.COMMAND_ZOOM_ACTUAL, Command.IGNORE_OLD_EQUIV);
	}

	
	// Tolerance for a move to be handled as a single click.
	static public final String TOLERANCE = "tolerance"; // 4
	// Specifies the factor used for zoomIn and zoomOut.
	static public final String ZOOM_FACTOR = "zoomFactor"; // 1.2
	// Specifies the grid size of the graph.
	static public final String GRID_SIZE = "gridSize"; // 10
	// Specifies the background image for the graph.
	static public final String BACKGROUND_IMAGE = "backgroundImage";
	// Specifies the width of the background image.
	static public final String BACKGROUND_IMAGE_WIDTH = "backgroundImageWidth";
	// Specifies the height of the background image.
	static public final String BACKGROUND_IMAGE_HEIGHT = "backgroundImageHeight";
	// Specifies if the graph should allow any interaction.
	static public final String ENABLED = "enabled"; // true
	// Specifies if the graph should allow in-place editing.
	static public final String EDITABLE = "editable"; // true
	// Specifies if the graph should allow moving of cells.
	static public final String MOVABLE = "movable"; // true
	// Specifies if the graph should allow sizing of cells.
	static public final String SIZABLE = "sizable"; // true
	// Specifies if the graph should allow selecting of cells.
	static public final String SELECTABLE = "selectable";
	// Specifies if the graph should automatically update the cell size after in-place editing.
	static public final String AUTO_SIZE = "autoSize"; // false
	// Specifies if automatic layout should be carried out if a non-null value is returned from getLayout.
	static public final String AUTO_LAYOUT = "autoLayout"; // true
	// Specifies if the grid is enabled.
	static public final String IS_GRID_ENABLED = "isGridEnabled"; // true
	// Specifies if the parent cell should be resized if a child cell is being resized so that it overlaps the parent bounds.
	static public final String IS_EXTEND_PARENT_ON_RESIZE = "isExtendParentOnResize"; // true
	// Specifies if the siblings below a cell should be shifted downwards if a cell is being resized.
	static public final String IS_SHIFT_DOWNWARDS = "isShiftDownwards"; // false
	// Specifies if the siblings to the right of a cell should be shifted rightwards if a cell is being resized.
	static public final String IS_SHIFT_RIGHTWARDS = "isShiftRightwards"; // false
	// Specifies if the cell's size should change to the cell's preferred size on the first collapse.
	static public final String IS_COLLAPSED_TO_PREFERRED_SIZE = "isCollapseToPreferredSize"; // true
	// Specifies if the viewport should automatically contain the selection cells after a zoom operation.
	static public final String IS_KEEP_SELECTION_VISIBLE_ON_ZOOM = "isKeepSelectionVisibleOnZoom"; // false
	// Specifies if the zoom operations should go into the center of the document.
	static public final String IS_CENTER_ZOOM = "isCenterZoom"; // false
	// Specifies if the scale and translate should be reset if the root changes in the model.
	static public final String IS_RESET_VIEW_ON_ROOT_CHANGE = "isResetViewOnRootChange"; // true
	// Specifies if edge control points should be reset after the resize of a connected cell.
	static public final String IS_RESET_EDGES_ON_RESIZE = "isResetEdgesOnResize"; // false
	// Specifies if edge control points should be reset after the move of a connected cell.
	static public final String IS_RESET_EDGES_ON_MOVE = "isResetEdgesOnMove"; // true
	// Loops are not yet supported.
	static public final String IS_ALLOW_LOOPS = "isAllowLoops"; // false
	// Specifies if multiple edges between the same pair of vertices are allowed.
	static public final String IS_MULTIGRAPH = "isMultigraph"; // true
	// Specifies if edges with disconnected terminals are allowed in the graph.
	static public final String IS_ALLOW_DANGLING_EDGES = "isAllowDanglingEdges"; // true
	// Specifies if labels should be visible.
	static public final String IS_LABELS_VISIBLE = "isLabelsVisible"; // true
	// Specifies is nesting of swimlanes is allowed.
	static public final String IS_SWIMLANE_NESTING = "isSwimlaneNesting"; // true
	// Specifies the image to be used to indicate a collapsed state.
	static public final String COLLAPSED_IMAGE = "collapsedImage"; // mxClient.basePath+'images/collapsed.gif'
	// Specifies the image to be used to indicate a expanded state.
	static public final String EXPANDED_IMAGE = "expandedImage"; // mxClient.basePath+'images/expanded.gif'
	// Specifies the basename (no extension) for the image to be used to display a warning overlay.
	static public final String WARNING_IMAGE_BASENAME = "warningImageBasename"; // mxClient.basePath+'images/warning'
	// Specifies the error message to be displayed in non-multigraphs when two vertices are already connected.
	static public final String ALREADY_CONNECTED = "alreadyConnected"; // 'Nodes area already connected'
	// Specifies the warning message to be displayed when a collapsed cell contains validation errors.
	static public final String CONTAINS_VALIDATION_ERRORS = "containsValidationErrors"; // 'Contains validation errors'
	
	private MxLayout layout;
	private MxStyleSheet styleSheet = new MxStyleSheet();
	private MxModel model = new MxModel();
	private MxPanningHandler panningHandler = new MxPanningHandler(this);
	private String[] selection = new String[0]; // array of ids
	private Map<String,Object> properties = new HashMap<String,Object>();	
	private Map<String,Menu> menus = new HashMap<String,Menu>();
	/**
	 * Constructs a new graph
	 *
	 */
	public MxGraph() {
		initializeStylesheet();
	}
	
	private void initializeStylesheet() {
		styleSheet.putCellStyle(MxConstants.SHAPE_ACTOR, MxStyleHelper.getActorStyle());
		styleSheet.putCellStyle(MxConstants.SHAPE_ARROW, MxStyleHelper.getArrowStyle());
		styleSheet.putCellStyle(MxConstants.SHAPE_CYLINDER, MxStyleHelper.getCylinderStyle());
		styleSheet.putCellStyle(MxConstants.SHAPE_ELLIPSE, MxStyleHelper.getEllipseStyle());
		styleSheet.putCellStyle(MxConstants.SHAPE_SWIMLANE, MxStyleHelper.getSwimlaneStyle());
	}
	
	/**
	 * Encodes an object as a JSON string
	 * @param object
	 * @return the JSON string
	 */
	public static String encode(Object object) {
		String encodedString = null;
		encodedString = JSONSerializer.toJSON(object).toString();
		return encodedString;
	}
	
	@Override
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final StringBuffer sb = new StringBuffer(64);
		if (attrs != null) {
			sb.append(attrs);
		}
		try {
			HTMLs.appendAttribute(sb, "z.init", URLEncoder.encode(MxGraph.encode(getInitialState()), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	/**
	 * Returns a Map containing the current state of the graph
	 * @return Map<String, Object>
	 */
	public Map getInitialState() {
		Map<String,Object> state = new HashMap<String,Object>();
		state.put("layout", layout);
		state.put("styleSheet", styleSheet);
		state.put("properties", properties); // those with values set, i.e. with non-default values
		state.put("model", model);
		state.put("panningHandler", panningHandler);
		state.put("selection", selection);
		state.put("menus", menus);
		return state;
	}
	/**
	 * Adds a vertex to the graph and model.
	 * @param vertex
	 * @return 
	 */
	public MxVertex addVertex(MxVertex vertex) {
		model.addCell(vertex);
		smartUpdate("z:addVertex", MxGraph.encode(vertex));
		Events.postEvent(new VertexAddedEvent(MxConstants.COMMAND_ADD_VERTEX, this, vertex.getId()));
		return vertex;
	}
	/**
	 * Constructs a new vertex and adds it to the graph and model.
	 * @param value The value to display
	 * @param x Upper left X coordinate
	 * @param y Upper left Y coordinate
	 * @param width desired initial width of the vertex
	 * @param height desired initial height of the vertex
	 * @return the new vertex
	 */
	public MxVertex addVertex(String value, int x, int y, int width, int height) {
		MxVertex vertex = new MxVertex(value, x, y, width, height);
		return addVertex(vertex);
	}
	
	/**
	 * Adds an edge to the graph and model
	 * @param edge
	 * @return
	 */
	public MxEdge addEdge(MxEdge edge) {
		model.addCell(edge);
		smartUpdate("z:addEdge", MxGraph.encode(edge));
		Events.postEvent(new EdgeAddedEvent(MxConstants.COMMAND_ADD_EDGE, this, edge.getId()));
		return edge;
	}
	
	/**
	 * Constructs a new edge and adds it to the model
	 * @param value the value to display on the edge
	 * @param from the source of the edge
	 * @param to the target of the edge
	 * @return
	 */
	public MxEdge addEdge(String value, MxVertex from, MxVertex to) {
		MxEdge edge = new MxEdge(value, from.getId(), to.getId());
		return addEdge(edge);
	}
	
	/**
	 * Deletes a particular cell from the graph and deletes it from the model.
	 * @param id
	 */
	public void deleteCell(String id) {
		String[] ids = {id};
		deleteCells(ids);
	}
	
	/**
	 * Removes a set of cells from the graph and deletes them from the model.
	 * @param ids the list of unique IDs for the cells to remove
	 */
	public void deleteCells(String[] ids) {
		List<String> removedIds = new ArrayList<String>();
		for (String id : ids) {
			if (model.removeCell(id)) {
				removedIds.add(id);
			}
		}
		smartUpdate("z:removeCells", MxGraph.encode(removedIds));
		Events.postEvent(new DeleteCellsEvent(MxConstants.COMMAND_DELETE, this, 
														removedIds.toArray(new String[removedIds.size()])));
	}
	
	/**
	 * Adds a menu.  The change is only propagated to the client when <code>requiresUpdate</code> is true.
	 * @param menu the menu to add
	 * @param requiresUpdate
	 */
	public void addMenu(Menu menu, boolean requiresUpdate) {
		menus.put(menu.getName(), menu);
		if (requiresUpdate) smartUpdate("z:addMenu", MxGraph.encode(menu));
	}
	
	/**
	 * Adds an overlay to a cell.
	 * @param cell
	 * @param overlay
	 */
	public void addOverlay(MxCell cell, MxOverlay overlay) {
		model.addOverlay(cell, overlay);
		JSONObject obj = JSONObject.fromObject(overlay);
		obj.put("cell", cell.getId());
		smartUpdate("z:addOverlay", obj.toString());
		Events.postEvent(new OverlayEvent(MxConstants.COMMAND_ADD_OVERLAY, this, 
														cell.getId(), overlay.getId()));
	}
	
	/**
	 * Removes a particular overlay from being rendered on a cell.
	 * @param cell
	 * @param overlay
	 */
	public void removeOverlay(MxCell cell, MxOverlay overlay) {
		model.removeOverlay(cell, overlay);
		JSONObject obj = new JSONObject();
		obj.put("cell", cell.getId());
		obj.put("overlay", overlay.getId());
		smartUpdate("z:removeOverlay", obj.toString());		
		Events.postEvent(new OverlayEvent(MxConstants.COMMAND_REMOVE_OVERLAY, this, 
				cell.getId(), overlay.getId()));
	}
	
	/**
	 * Removes all overlays from a particular cell.
	 * @param cell
	 */
	public void clearOverlays(MxCell cell) {
		model.clearOverlays(cell);
		JSONObject obj = new JSONObject();
		obj.put("cell", cell.getId());
		smartUpdate("z:clearOverlays", cell.getId());		
		Events.postEvent(new OverlayEvent(MxConstants.COMMAND_CLEAR_OVERLAYS, this, 
				cell.getId(), ""));
	}
	/**
	 * Sets a property on this graph instance.  If the update flag is enabled,
	 * the property change will be propagated to the client.
	 * @param name
	 * @param value
	 * @param update
	 */
	public void setProperty(String name, Object value, boolean update) {
		properties.put(name, value);
		if (update){ 
			String encVal;
			if (value instanceof String) {
				encVal = (String)value;
			} else {
				encVal = MxGraph.encode(value);
			}
			smartUpdate("z:setProperty", name + ":" + encVal);
		}
	}
	
	/**
	 * Retrieves a property value by name
	 * @param name
	 * @return the property value
	 */
	public Object getProperty(String name) {
		return properties.get(name);
	}
		
	/**
	 * Registers a style to be indexed by <code>key</code>.  If there is already an existing style registered 
	 * under <code>key</code>, it will be replaced with the new style.
	 * @param key the key to register the style under
	 * @param style a map containing style elements.  The available elements are documented in MxConstants.STYLE_*
	 */
	public void putStyle(String key, Map style) {
		styleSheet.putCellStyle(key, style);
		JSONObject obj = new JSONObject();
		obj.put("key", key);
		obj.put("style", style);
		smartUpdate("z:putStyle", obj.toString());		
		Events.postEvent(new StyleAddedEvent(MxConstants.COMMAND_PUT_STYLE, this, 
				key));
	}
	
	/**
	 * Adds a set of existing cells as children of a new group cell.  The parent of the group
	 * cell will be set to the previous parent of the children cells  The group cell
	 * should not have previously been added to the graph.
	 * 
	 * @param group
	 * @param cellIds
	 */
	public void groupCells(MxVertex group, String[] cellIds) {
		JSONObject obj = new JSONObject();
		obj.put("group", MxGraph.encode(group));
		obj.put("cells", MxGraph.encode(cellIds));   
		smartUpdate("z:groupCells", obj.toString());
		Events.postEvent(new GroupCellsEvent(MxConstants.COMMAND_GROUP_CELLS, this, group.getId(), cellIds));
		for (int inx = 0 ; inx < cellIds.length ; inx++) {
			MxCell cell = model.getCell(cellIds[inx]);
			cell.setVertex(true);
			group.setParent(cell.getParent());
			cell.setParent(group.getId());
			group.addChild(cell.getId());
		}
		model.addCell(group);
	}
	/**
	 * Removes all children cells from a parent and deletes the parent.
	 * @param group the parent cell
	 */
	public void ungroupCells(MxVertex group) {
		clearOverlays(group);
		smartUpdate("z:ungroupCells", group.getId());
		Events.postEvent(new UngroupCellsEvent(MxConstants.COMMAND_UNGROUP, this, group.getId()));
		for (String id : group.getChildren()) {
			MxCell cell = model.getCell(id);
			cell.setParent(group.getParent());
		}
		model.removeCell(group.getId());
	}
	
	/**
	 * Zooms the graph in by the value set in ZOOM_FACTOR.  By default, this is 1.2, or 120%.
	 *
	 */
	public void zoomIn() {
		smartUpdate("z:zoomIn", "");
		Events.postEvent(new ZoomEvent(MxConstants.COMMAND_ZOOM_IN, this));
	}
	/**
	 * Zooms the graph out by the value set in ZOOM_FACTOR.  By default, this is 1.2, or 120%.
	 *
	 */
	public void zoomOut() {
		smartUpdate("z:zoomOut", "");
		Events.postEvent(new ZoomEvent(MxConstants.COMMAND_ZOOM_IN, this));
	}
	/**
	 * Zooms the graph to it's default scale.
	 *
	 */
	public void zoomToActual() {
		smartUpdate("z:zoomActual", "");
		Events.postEvent(new ZoomEvent(MxConstants.COMMAND_ZOOM_IN, this));
	}
	
	/**
	 * Zooms the graph to fit in the display.
	 *
	 */
	public void zoomToFit() {
		smartUpdate("z:zoomFit", "");
		Events.postEvent(new ZoomEvent(MxConstants.COMMAND_ZOOM_IN, this));
	}
	
	// Event-free setters

	/**
	 * Sets the set of selected cells.  If <code>update</code> is true, 
	 * the selection is propagated to the client for display.
	 * @param data the IDs of the cells to select
	 * @param update 
	 */
	public void setSelection(String[] data, boolean update) {
		selection = data;
		if (update) smartUpdate("z:select", MxGraph.encode(selection));
        Events.postEvent(new SelectCellsEvent(MxConstants.COMMAND_SELECT, this, data));
	}
	
	/**
	 * Returns the set of currently selected cells
	 * @return String[] of cell IDs
	 */
	public String[] getSelection() {
		return selection;
	}

	/**
	 * Returns the graph layout
	 * @return the layout
	 */
	public MxLayout getLayout() {
		return layout;
	}

	/**
	 * Returns the model for the graph
	 * @return the model
	 */
	public MxModel getModel() {
		return model;
	}

	/**
	 * Initializes graph to a layout (not effective once the graph is activated).
	 * @param layout the layout to set
	 */
	public void setLayout(MxLayout layout) {
		this.layout = layout;
	}

	/**
	 * Returns the stylesheet
	 * @return the styleSheet
	 */
	public MxStyleSheet getStyleSheet() {
		return styleSheet;
	}

	/**
	 * Sets the stylesheet.  After initialization, the <code>putStyle()</code> method 
	 * should be used to register new styles.
	 * @param styleSheet the styleSheet to set
	 */
	public void setStyleSheet(MxStyleSheet styleSheet) {
		this.styleSheet = styleSheet;
	}
	
	/**
	 * Returns the panning handler
	 * @return the panningHandler
	 */
	public MxPanningHandler getPanningHandler() {
		return panningHandler;
	}
	
}
