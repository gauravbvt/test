/**
 * 
 */
package com.mindalliance.zk.mxgraph;

/**
 * @author dfeeney
 *
 */
public class MxOverlay {
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
	
}
