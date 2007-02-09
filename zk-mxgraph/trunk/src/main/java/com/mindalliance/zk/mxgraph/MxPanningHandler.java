/*
 * Created on Feb 8, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

import java.util.HashMap;
import java.util.Map;

public class MxPanningHandler {
	
	static public final String IS_USE_SHIFT_KEY = "isUseShiftKey";	// true
	static public final String IS_USE_POPUP_TRIGGER = "isUsePopupTrigger";	// true
	static public final String IS_SELECT_ON_POPUP = "isSelectOnPopup";	// true
	static public final String IS_USE_LEFT_BUTTON = "isUseLeftButton";	// false
	static public final String IS_USE_LEFT_BUTTON_FOR_POPUP = "isUseLeftButtonForPopup"; // false
	static public final String IS_PAN_ENABLED = "isPanEnabled";	// true

	private MxGraph graph;
	private Map<String,Object> properties = new HashMap<String,Object>();
	
	public MxPanningHandler(MxGraph graph) {
		this.graph = graph;
	}

	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public void setProperty(String name, Object value, boolean update) {
		properties.put(name, value);
		if (update) graph.smartUpdate("z:setPanning", name + ":" + MxGraph.encode(value));
	}


}
