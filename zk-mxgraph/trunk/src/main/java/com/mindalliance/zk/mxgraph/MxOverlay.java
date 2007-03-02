/**
 * 
 */
package com.mindalliance.zk.mxgraph;

import java.util.ArrayList;
import java.util.List;


/**
 * @author dfeeney
 *
 */
public class MxOverlay {
	private String id;
	private String image;
	private String tooltip;
	private MxGeometry bounds;
	private List<MxCellListener> clickListeners = new ArrayList<MxCellListener>();
	


	public MxOverlay(String imageUrl, String tooltip) {
		this(imageUrl, tooltip, 0, 0, 16, 16);
		
	}

	public MxOverlay(String imageUrl, String tooltip, int x, int y, int width, int height) {
		this.bounds = new MxGeometry(x,y,width,height);
		this.image = imageUrl;
		this.tooltip = tooltip;
		id = MxModel.makeUid();
	}
	public String getImage() {
		return image;
	}

	public void setImage(String imageUrl) {
		this.image = imageUrl;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public MxGeometry getBounds() {
		return bounds;
	}

	public void setBounds(MxGeometry bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	public void addClickListener(MxCellListener listener) {
		clickListeners.add(listener);
	}
	
	public List<MxCellListener> getClickListeners() {
		return clickListeners;
	}

	public void setClickListeners(List<MxCellListener> clickListeners) {
		this.clickListeners = clickListeners;
	}
	
	public void click(MxGraph graph, MxCell cell) {
		for (MxCellListener listener: clickListeners) {
			listener.onEvent(graph, cell);
		}
	}
	
}
