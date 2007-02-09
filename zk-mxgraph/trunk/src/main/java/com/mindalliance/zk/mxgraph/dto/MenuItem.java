/*
 * Created on Jan 31, 2007
 *
 */
package com.mindalliance.zk.mxgraph.dto;

public class MenuItem {
	
	private String name;
	private String icon;
	private String command;
	private String[] parameters = {};

	public static final MenuItem Separator = new MenuItem("_separator_", null, null, null);

	public MenuItem(String name, String icon, String command, String[] parameters) {
		this.name = name;
		this.icon = icon;
		this.command = command;
		if (parameters != null) this.parameters = parameters;
	}
	

	/**
	 * @return the com.mindalliance.zk.mxgraph.command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param com.mindalliance.zk.mxgraph.command the com.mindalliance.zk.mxgraph.command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parameters
	 */
	public String[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	

}
