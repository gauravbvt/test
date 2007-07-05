// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.command;


/**
 * An interface representing an implementation of the Command Design Pattern.
 * 
 * This is used to encapsulate user actions into a discrete tasks to support
 * things like transactional work units, undo, macro playback, etc.
 * 
 * @author brian
 *
 */
public interface Command {
	
	/**
	 * Perform the activity associated with this Command instance. It is
	 * expected to have all necessary state already.
	 * 
	 */
	public void execute();
	
	/**
	 * Return the name of the Command.
	 * 
	 * @return String name of Command
	 */
	public String getName();
	
	/**
	 * Return a name intended for display in a user interface.
	 * 
	 * @return
	 */
	public String getDisplayName();
	
	/**
	 * Set the name intended for display in a user interface.
	 */
	public void setDisplayName(String displayName);
	
	/**
	 * Return whether the Command is enabled.
	 * 
	 * @return boolean indicating Command enabled state
	 */
	public boolean getEnabled();
	
	/**
	 * Sets the enabled state of the Command.
	 * 
	 */
	public void setEnabled(boolean enabled);
	
	/**
	 * Get a String reference to an image representation for the Command.
	 * @return String filename
	 */
	public String getRepresentation();
	
	/**
	 * Set the String reference to an image representation for the Command
	 * @param String representation
	 */
	public void setRepresentation(String representation);
}
