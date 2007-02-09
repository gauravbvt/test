/*
 * Created on Feb 3, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

import java.util.HashMap;
import java.util.Map;

public class MxStyleSheet {
	
	private Map<String,String> defaultVertexStyle = new HashMap<String,String>();
	private Map<String,String> defaultEdgeStyle = new HashMap<String,String>();
	private Map<String,Map> cellStyles = new HashMap<String,Map>();
	
	public void putCellStyle(String name, Map style) {
		cellStyles.put(name, style);
	}
	
	public Map getCellStyle(String name) {
		return cellStyles.get(name);
	}
	/**
	 * @return the cellStyles
	 */
	public Map<String, Map> getCellStyles() {
		return cellStyles;
	}
	/**
	 * @param cellStyles the cellStyles to set
	 */
	public void setCellStyles(Map<String, Map> cellStyles) {
		this.cellStyles = cellStyles;
	}
	/**
	 * @return the defaultEdgeStyle
	 */
	public Map<String, String> getDefaultEdgeStyle() {
		return defaultEdgeStyle;
	}
	/**
	 * @param defaultEdgeStyle the defaultEdgeStyle to set
	 */
	public void setDefaultEdgeStyle(Map<String, String> defaultEdgeStyle) {
		this.defaultEdgeStyle = defaultEdgeStyle;
	}
	/**
	 * @return the defaultVertexStyle
	 */
	public Map<String, String> getDefaultVertexStyle() {
		return defaultVertexStyle;
	}
	/**
	 * @param defaultVertexStyle the defaultVertexStyle to set
	 */
	public void setDefaultVertexStyle(Map<String, String> defaultVertexStyle) {
		this.defaultVertexStyle = defaultVertexStyle;
	}
}
