/**
 * 
 */
package com.mindalliance.zk.mxgraph;

import java.util.ArrayList;
import java.util.List;


/**
 * An image overlay that can be associated with one or more cells.  Overlays are rendered as children
 * of the containing cell and maintain their own size and position in the containing cell.  Each overlay maintains
 * a set of MxCellListener instances that are executed when the overlay is clicked.
 * 
 * <p>
 * <code>		
 *      MxOverlay deleteOverlay = new MxOverlay("/images/delete.png", "Delete this node", 0,16,16,16);<br>
 *		deleteOverlay.addClickListener(new MxCellListener() {<br>
 *			public void onEvent(MxGraph graph, MxCell cell) {<br>
 *				graph.deleteCell(cell.getId());<br>
 *			}<br>
 *		});<br>
 * </code>
 * 
 * <p>The above code creates a new 16x16 overlay that, when clicked, will delete the node containing it from the graph.  The overlay 
 * will be rendered 16 pixels below the upper left hand corner of the containing cell.
 * 
 * <p>An overlay may be added directly to a cell that has not been added to the model via {@link com.mindalliance.zk.mxgraph.MxCell#addOverlay(MxOverlay)}.
 * <p><code>
 * node1.addOverlay(deleteOverlay);
 * </code>
 * <p>Overlays may be added to already inserted cells via {@link com.mindalliance.zk.mxgraph.MxGraph#addOverlay(MxCell, MxOverlay)}.
 * <p><code>
 * graph.addOverlay(node1, deleteOverlay);
 * </code>
 * <p>An overlay may be added to more than one cell, and similarly, a cell may contain more than one overlay.
 * 
 * <p>Overlays should be removed from cells via {@link com.mindalliance.zk.mxgraph.MxGraph#removeOverlay(MxCell, MxOverlay)} or
 * {@link com.mindalliance.zk.mxgraph.MxGraph#clearOverlays(MxCell)}.  An overlay may be reused in another cell
 * after it has been removed.
 *
 */
public class MxOverlay {
	private String id;
	private String image;
	private String tooltip;
	private MxGeometry bounds;
	private List<MxCellListener> clickListeners = new ArrayList<MxCellListener>();
	

	/**
	 * Creates a 16x16 overlay that will be placed in the upper right corner of the appropriate cell.
	 * @param imageUrl the url for the image to use
	 * @param tooltip
	 */
	public MxOverlay(String imageUrl, String tooltip) {
		this(imageUrl, tooltip, 0, 0, 16, 16);
		
	}

	/**
	 * Creates a (width x height) overlay that will be placed (x,y) units from the upper left of displaying cells
	 * @param imageUrl
	 * @param tooltip
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public MxOverlay(String imageUrl, String tooltip, int x, int y, int width, int height) {
		this.bounds = new MxGeometry(x,y,width,height);
		this.image = imageUrl;
		this.tooltip = tooltip;
		id = MxModel.makeUid();
	}
	/**
	 * Returns the image URL
	 * @return
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the URL to pull the overlay image from
	 * @param imageUrl
	 */
	public void setImage(String imageUrl) {
		this.image = imageUrl;
	}

	/**
	 * Returns the overlays tooltip.
	 * @return
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Sets the overlays tooltip.
	 * @param tooltip
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * Returns the bounds for this overlay.  The (x,y) coordinate pair is used
	 * to determine the offset from the upper left corner of cells containing this overlay.
	 * The width and height determine the size of the overlay when it is rendered.
	 * @return
	 */
	public MxGeometry getBounds() {
		return bounds;
	}

	/**
	 * Sets the bounds for this overlay.  The (x,y) coordinate pair is used
	 * to determine the offset from the upper left corner of cells containing this overlay.
	 * The width and height determine the size of the overlay when it is rendered.
	 * @param bounds
	 */
	public void setBounds(MxGeometry bounds) {
		this.bounds = bounds;
	}

	/**
	 * Returns the unique ID for this overlay.
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Adds a new listener to the end of the list of click listeners for this overlay.
	 * @param listener
	 */
	public void addClickListener(MxCellListener listener) {
		clickListeners.add(listener);
	}
	
	/**
	 * Returns the list of cell listeners for click events
	 * @return
	 */
	public List<MxCellListener> getClickListeners() {
		return clickListeners;
	}

	/**
	 * Sets the list of cell listeners for click events
	 * @param clickListeners
	 */
	public void setClickListeners(List<MxCellListener> clickListeners) {
		this.clickListeners = clickListeners;
	}
	
	/**
	 * Indicates that this overlay has been clicked.  Executes the click listeners in order, 
	 * passing in the cell that the overlay was clicked in.
	 * @param graph
	 * @param cell
	 */
	public void click(MxGraph graph, MxCell cell) {
		for (MxCellListener listener: clickListeners) {
			listener.onEvent(graph, cell);
		}
	}
	
}
