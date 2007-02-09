/*
 * Created on Feb 3, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

public class MxCompactTreeLayout extends MxLayout {
	
	private boolean horizontal = false;
	private int nodeDistance = 10;
	private int levelDistance = 20;

	public MxCompactTreeLayout() {
		super(MxLayout.COMPACT_TREE_LAYOUT);
	}
	
	public MxCompactTreeLayout(boolean horizontal) {
		this();
		this.horizontal = horizontal;
	}

	/**
	 * @return the horizontal
	 */
	public boolean isHorizontal() {
		return horizontal;
	}

	/**
	 * @param horizontal the horizontal to set
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	/**
	 * @return the levelDistance
	 */
	public int getLevelDistance() {
		return levelDistance;
	}

	/**
	 * @param levelDistance the levelDistance to set
	 */
	public void setLevelDistance(int levelDistance) {
		this.levelDistance = levelDistance;
	}

	/**
	 * @return the nodeDistance
	 */
	public int getNodeDistance() {
		return nodeDistance;
	}

	/**
	 * @param nodeDistance the nodeDistance to set
	 */
	public void setNodeDistance(int nodeDistance) {
		this.nodeDistance = nodeDistance;
	}

}
