/*
 * Created on Feb 3, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

public class MxCircleLayout extends MxLayout {
	
	private int radius = 100;
	
	protected MxCircleLayout() {
		super(MxLayout.CIRCLE_LAYOUT);
	}
	
	public MxCircleLayout(int radius) {
		this();
		this.radius = radius;
	}

	/**
	 * @return the radius
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

}
