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
	private int imageWidth;
	private int imageHeight;
	private List<OverlayClickListener> clickListeners = new ArrayList<OverlayClickListener>();
	


	public MxOverlay(String imageUrl, String tooltip) {
		this(imageUrl, tooltip, 16,16);
		
	}

	public MxOverlay(String imageUrl, String tooltip, int imageWidth, int imageHeight) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
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

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
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
