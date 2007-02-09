/*
 * Created on Feb 3, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

public abstract class MxLayout {
	
	public static final String FLOW_LAYOUT = "FlowLayout";
	public static final String COMPACT_TREE_LAYOUT = "CompactTreeLayout";
	public static final String FAST_ORGANIC_LAYOUT = "FastOrganicLayout";
	public static final String CIRCLE_LAYOUT = "CircleLayout";

	private String type;
	
	protected MxLayout(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
