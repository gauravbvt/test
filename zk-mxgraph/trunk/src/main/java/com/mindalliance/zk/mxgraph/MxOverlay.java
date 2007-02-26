/**
 * 
 */
package com.mindalliance.zk.mxgraph;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;

import com.mindalliance.zk.mxgraph.event.OverlayClickEvent;

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
	
	
	public abstract class OverlayClickListener implements org.zkoss.zk.ui.event.EventListener {
		public void onEvent(Event event) throws UiException {
			OverlayClickEvent ocEvent = (OverlayClickEvent)event;
			MxGraph graph = (MxGraph)event.getTarget();
			MxCell cell = graph.getModel().getCell(ocEvent.getCellId());
			onClick(graph, cell);
		}
		public abstract void onClick(MxGraph graph, MxCell cell);
		public boolean isAsap() {
			return true;
		}
	}
	
}
