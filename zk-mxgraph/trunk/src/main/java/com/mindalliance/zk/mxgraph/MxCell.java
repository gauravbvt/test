/*
 * Created on Jan 29, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

import java.util.ArrayList;
import java.util.List;

public class MxCell {
	
	private String id;
	private String value;
	private String style;
	private boolean visible;
	private String parent;
	private boolean vertex;
	private List<String> children = new ArrayList<String>();
	private MxOverlay overlay = null;
	
	public MxCell(String value) {
		id = MxModel.makeUid();
		this.value = value;
	}


	public void addChild(String childId) {
		children.add(childId);
	}

	public void removeChild(String childId) {
		children.remove(childId);
	}

	/**
	 * @return the children
	 */
	public List<String> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<String> children) {
		this.children = children;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the vertex
	 */
	public boolean isVertex() {
		return vertex;
	}

	/**
	 * @param vertex the vertex to set
	 */
	public void setVertex(boolean vertex) {
		this.vertex = vertex;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	public MxOverlay getOverlay() {
		return overlay;
	}


	public void setOverlay(MxOverlay overlay) {
		this.overlay = overlay;
	}


}
