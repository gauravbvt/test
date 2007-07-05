// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.command;

import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * An abstract base command that maintains basic state for
 * child Command classes.
 * 
 * @author brian
 *
 */
abstract public class AbstractCommand extends AbstractJavaBean implements Command {
	private String name;
	private String displayName;
	private boolean enabled = true;
	private String representation;
	
	/**
	 * Constructor for Commands that want to be enabled 
	 * by default.
	 * 
	 * @param name String name of Command
	 */
	public AbstractCommand(String name) {
		this(name, true);
	}
	
	/**
	 * Constructor for Commands that want to indicate
	 * enabled status as part of creation process.
	 * 
	 * @param name
	 * @param enabled
	 */
	public AbstractCommand(String name, boolean enabled) {
		this(name, null, enabled);
	}
	
	/**
	 * Constructor for Commands that want to indicate a name
	 * and displayName.
	 * 
	 * @param name
	 * @param displayName
	 */
	public AbstractCommand(String name, String displayName) {
		this(name, displayName, false);
	}

	/**
	 * Constructor for Commands that want to indicate
	 * name, displayName and enabled status as part of
	 * construction process.
	 *  
	 * @param name
	 * @param displayName
	 * @param enabled
	 */
	public AbstractCommand(String name, String displayName, boolean enabled) {
		this.name = name;
		this.displayName = displayName;
		this.enabled = enabled;		
	}
	
	/**
	 * Return the name of the Command instance. This is 
	 * immutable for the lifetime of the Command. And 
	 * should be unique within any particular CommandFactory
	 * namespace.
	 * 
	 * @return String name of the Command
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name intended for display in a user interface.
	 */
	
	public synchronized void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * Return a display name suitable for showing in a UI.
	 * Defaults to the command name if no display name has been
	 * specified.
	 * 
	 * @return String display name or Command name
	 */
	
	public synchronized String getDisplayName() {
		return displayName != null ? displayName : getName();
	}
	
	/**
	 * Return the enabled state of the Command.
	 * 
	 * @return boolean enabled state
	 */
	public synchronized boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * Sets the enabled state of the Command.
	 * 
	 * @param boolean enabled state of the Command
	 * 
	 */
	public synchronized void setEnabled(boolean enabled) {
		
		if(this.enabled == enabled) {
			return;
		}

		this.enabled = enabled;
	}
	
	/**
	 * Set the String reference to an image representation for the Command
	 * @param String representation
	 */
	public synchronized void setRepresentation(String representation) {
		
		if(this.representation == representation) {
			return;
		}
		
		this.representation = representation;
	}

	/**
	 * Get a String reference to an image representation for the Command.
	 * @return String filename
	 */
	public synchronized String getRepresentation() {
		return representation;
	}
}
