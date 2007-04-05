package com.mindalliance.channels.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An extension of the CommandMetadata class to provide
 * support for selection dependencies. This currently is
 * implemented as a map of "interested" commands. The map
 * represents the name of the command and the method to
 * call with the new value when a selection event occurs.
 * 
 * The SelectionCommandMetadata instance is associated with
 * a child of the SelectionCommand.
 * 
 * Example:
 * 
 * A ProjectSelectionCommand instance might have interest
 * from the ADD_PROJECT_TO_SYSTEM command and the REMOVE_PROJECT_FROM_SYSTEM
 * commands. Both might want to know when a Project has been selected
 * anywhere in the system.
 * 
 * In that case, as InvokeEditMethodCommand instances, they would
 * have map entries that looked like:
 * 
 * "ADD_PROJECT_TO_SYSTEM" : "setArgument"
 * "REMOVE_PROJECT_TO_SYSTEM" : "setArgument"
 * 
 * @author brian
 *
 */
public class SelectionCommandMetadata extends CommandMetadata {
	private Map<String,String> interestedCommands = new HashMap<String,String>();
	
	/**
	 * This method overrides the method in CommandMetadata to further enforce
	 * that it should only be applied to SelectionCommand derivatives.
	 * 
	 * @param Class<? extends Command> class to create as Command instance
	 */
	@Override
	public void setCommandClass(Class<? extends Command> commandClass) {
		if(commandClass.isAssignableFrom(SelectionCommand.class)) {
			super.setCommandClass(commandClass);
		} else {
			throw new IllegalArgumentException("SelectionCommandMetadata should be associated with a SelectionCommand");
		}
	}
	
	/**
	 * Adds the specified commands as interested in the selection events on
	 * the command created by this metadata. The Map should contain the
	 * Command instance names as well as the methods to invoke to pass the 
	 * selection targets on.
	 * 
	 * @param interestedCommands
	 */
	public void setInterestedCommands(Map<String,String> interestedCommands) {
		Iterator<String> cmdNameItor = interestedCommands.keySet().iterator();
		
		while(cmdNameItor.hasNext()) {
			String cmdName = cmdNameItor.next();
			this.interestedCommands.put(cmdName, interestedCommands.get(cmdName));
		}
	}
	
	/**
	 * Retrieve the map of interested Commands and the methods to call
	 * when the command created by this metadata is instantiated.
	 *  
	 * @return Map<String,String> map of interested command names and methods
	 */
	public Map<String,String> getInterestedCommands() {
		return interestedCommands;
	}
}
