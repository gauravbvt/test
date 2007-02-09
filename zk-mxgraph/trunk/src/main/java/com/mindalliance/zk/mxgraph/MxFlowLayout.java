/*
 * Created on Feb 3, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

public class MxFlowLayout extends MxLayout {
	
	private boolean vertical = false;
	private int spacing = -1; // null
	private int x0 = -1; // null
	private int y0 = -1; // null

	/**
	 * @return the spacing
	 */
	public int getSpacing() {
		return spacing;
	}

	/**
	 * @param spacing the spacing to set
	 */
	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	public MxFlowLayout() {
		super(MxLayout.FLOW_LAYOUT);
	}
	
	public MxFlowLayout(boolean vertical) {
		this();
		this.vertical = vertical;
	}

	/**
	 * @return the vertical
	 */
	public boolean isVertical() {
		return vertical;
	}

	/**
	 * @param vertical the vertical to set
	 */
	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	/**
	 * @return the x0
	 */
	public int getX0() {
		return x0;
	}

	/**
	 * @param x0 the x0 to set
	 */
	public void setX0(int x0) {
		this.x0 = x0;
	}

	/**
	 * @return the y0
	 */
	public int getY0() {
		return y0;
	}

	/**
	 * @param y0 the y0 to set
	 */
	public void setY0(int y0) {
		this.y0 = y0;
	}
}
