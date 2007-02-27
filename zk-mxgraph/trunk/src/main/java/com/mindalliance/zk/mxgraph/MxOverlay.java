/**
 * 
 */
package com.mindalliance.zk.mxgraph;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.zk.mxgraph.event.OverlayClickListener;

/**
 * @author dfeeney
 *
 */
public class MxOverlay {
	private String id;
	private String image;
	private String tooltip;
	private MxGeometry bounds;
	private List<OverlayClickListener> clickListeners = new ArrayList<OverlayClickListener>();
	


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
	
	public void addClickListener(OverlayClickListener listener) {
		clickListeners.add(listener);
	}
	
	public List<OverlayClickListener> getClickListeners() {
		return clickListeners;
	}

	public void setClickListeners(List<OverlayClickListener> clickListeners) {
		this.clickListeners = clickListeners;
	}
	
}
