/*
 * Created on Jan 31, 2007
 *
 */
package com.mindalliance.zk.mxgraph.dto;

import java.util.ArrayList;
import java.util.List;

public class Menu {
	
	public static final String ALL = "all";
	public static final String CELL = "cell";
	public static final String VERTEX = "vertex";
	public static final String EDGE = "edge";
	
	private String name;
	private List<MenuItem> items = new ArrayList<MenuItem>();
	
	/*
	 * Menu menu = Menu.named("default").item("itemName", "iconUrl", "com.mindalliance.zk.mxgraph.command").separator().item( ... ;
	 */
	public static Menu named(String name) {
		Menu menu = new Menu();
		menu.setName(name);
		return menu;
	}
	
	public Menu separator() {
		items.add(MenuItem.Separator);
		return this;
	}
	
	public Menu item(String itemName, String icon, String command, String[] parameters) {
		MenuItem menuItem = new MenuItem(itemName, icon, command, parameters);
		items.add(menuItem);
		return this;
	}
	
	public Menu item(String itemName, String icon, String command, String param) {
		String[] parameters = {param};
		return item(itemName, icon, command, parameters);
	}

	public Menu item(String itemName, String icon, String command) {
		return item(itemName, icon, command, new String[0]);
	}
	
	/**
	 * @return the items
	 */
	public List<MenuItem> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<MenuItem> items) {
		this.items = items;
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

}
