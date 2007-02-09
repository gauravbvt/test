/*
 * Created on Feb 3, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

public class MxFastOrganicLayout extends MxLayout {
	
	private int forceConstant = 50;
	
	/**
	 * @return the forceConstant
	 */
	public int getForceConstant() {
		return forceConstant;
	}

	/**
	 * @param forceConstant the forceConstant to set
	 */
	public void setForceConstant(int forceConstant) {
		this.forceConstant = forceConstant;
	}

	public MxFastOrganicLayout() {
		super(MxLayout.FAST_ORGANIC_LAYOUT);
	}

}
