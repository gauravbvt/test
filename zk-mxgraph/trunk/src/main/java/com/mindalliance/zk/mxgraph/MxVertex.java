/*
 * Created on Jan 30, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

import java.util.ArrayList;
import java.util.List;

public class MxVertex extends MxCell {

	public MxVertex(String value) {
		super(value);
		this.setVertex(true);
		this.geometry = new MxGeometry();
	}
	
	public MxVertex(String value, int x, int y, int width, int height) {
		this(value);
		this.geometry = new MxGeometry(x,y,width,height);
	}

	private MxGeometry geometry;
	private boolean collapsed;
	private boolean connectable;
	private List<String> edges = new ArrayList<String>();
	
	/**
	 * @return the collapsed
	 */
	public boolean isCollapsed() {
		return collapsed;
	}

	/**
	 * @param collapsed the collapsed to set
	 */
	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	/**
	 * @return the connectable
	 */
	public boolean isConnectable() {
		return connectable;
	}

	/**
	 * @param connectable the connectable to set
	 */
	public void setConnectable(boolean connectable) {
		this.connectable = connectable;
	}

	/**
	 * @return the edges
	 */
	public List<String> getEdges() {
		return edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(List<String> edges) {
		this.edges = edges;
	}

	/**
	 * @return the geometry
	 */
	public MxGeometry getGeometry() {
		return geometry;
	}

	/**
	 * @param geometry the geometry to set
	 */
	public void setGeometry(MxGeometry geometry) {
		this.geometry = geometry;
	}


}
