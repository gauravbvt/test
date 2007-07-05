// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

/**
 * A metadata container for Commands. These can be registered with 
 * a CommandFactory instance so that command instance are created
 * on demand.
 * 
 * @author brian
 * @see com.mindalliance.channels.command.CommandFactory
 * @see applicationContext-commands.xml
 */
package com.mindalliance.channels.command;

public class CommandMetadata {
	private Class commandClass;
	private String commandName;
	private String displayName;
	private boolean enabled = true;
	private String representation;
	
	/**
	 * Set the Class to create for this Command instance.
	 * 
	 * This is a required field.
	 * 
	 * @param commandClass that implements the Command interface
	 */
	public void setCommandClass(Class<? extends Command> commandClass) {
		
		if(commandClass==null) {
			throw new IllegalArgumentException("Cannot specify a null command Class");
		}
		
		this.commandClass = commandClass;
	}
	
	/**
	 * Returns the Class to be used to create this Command.
	 * 
	 * @return Class to be used for this Command
	 */
	public Class getCommandClass() {
		return commandClass;
	}
	
	
	/**
	 * Set the name of the Command to be used when the Command is
	 * created. The name should be unique within the namespace
	 * of a particular CommandFactory instance.
	 * 
	 * This is a required field.
	 * 
	 * Note: once created, Command names are immutable, but
	 * as metadata they are not.
	 * @param commandName String name to use
	 */
	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}
	
	/**
	 * Get the name to be associated with the Command instance
	 * when it is created.
	 * 
	 * @return String name to be used for the Command
	 */
	public String getCommandName() {
		return commandName;
	}
	
	/**
	 * Set the name to be used in UI displays for the Command
	 * instance. 
	 * 
	 * This is not a required field.
	 *  
	 * @param displayName String name to display in a UI
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * Get the String display name to show in a UI for the 
	 * Command instance after it is created.
	 * 
	 * @return String display name
	 * 
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Sets the enabled state for the Command when it is 
	 * created. The default is true.
	 * 
	 * This is not a required field.
	 * 
	 * @param enabled boolean enabled state
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Get the enabled state to use for the Command instance
	 * when it is created.
	 * 
	 * @return boolean enabled state
	 */
	public boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * Set the String representation for the Command instance. This
	 * is currently expected to be a filename for an image icon. 
 	 * 
	 * @param representation String representation
	 */
	public void setRepresentation(String representation) {
		this.representation = representation;
	}
	
	/**
	 * Get the String representation to be used when the
	 * Command is created. This is currently expected to be
	 * a filename for an image icon. 
	 * 
	 * @return String
	 */
	public String getRepresentation() {
		return representation;
	}
}
