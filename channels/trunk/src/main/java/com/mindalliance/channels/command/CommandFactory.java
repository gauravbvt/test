// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.command;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The CommandFactory class represents a command registration factory and
 * serves as a form of namespace management for command instances.
 * 
 * In Channels, each user will have his or her own CommandFactory instance
 * (it is tied to the HttpSession by the Spring Config). The Command instances
 * are responsible for maintaining their own state.
 * 
 * The CommandFactory can be configured with either actual Command instances or 
 * sets of CommandMetadata that describe how the command instances should be
 * created. This latter form will be created on demand.
 * 
 * @author brian
 * @see com.mindalliance.channels.command.Command
 * @see applicationContext-commands.xml
 * 
 */
public class CommandFactory {
	
	// Map of String command names to Command instances
	private Map<String,Command> commandMap = new HashMap<String,Command>();
	// Map of String command names to CommandMetadata instances (used to create command instances on the fly)
	private Map<String,CommandMetadata> commandMetadataMap = new HashMap<String,CommandMetadata>();
	// Map of String command name to names of selection commands they care about
	private Map<String,String> reverseSelectionDependenciesMap = new HashMap<String,String>();
	// Map of String selection command names to the names of Commands and the Methods to call upon selection events
	private Map<String,Map<String,String>> dependencyEntryMap = new HashMap<String, Map<String,String>>();
	
	/**
	 * A method to establish the names of commands to create on demand from the specfied
	 * metadata.
	 * 
	 * @param cmdMetadata Set of CommandData
	 */
	public synchronized void setCommandsAsMetadata(Set<CommandMetadata> cmdMetadata) {
		if(cmdMetadata==null) {
			throw new IllegalArgumentException("Null command metadata Map");
		}
		
		Iterator<CommandMetadata> cmdMetadataItor = cmdMetadata.iterator();
		
		while(cmdMetadataItor.hasNext()) {
			CommandMetadata next = cmdMetadataItor.next();
			
			if(next.getCommandName() == null || next.getCommandClass() == null) {
				throw new IllegalArgumentException("CommandMetadata must have at least name and Class to create.");
			}
			
			if(this.commandMetadataMap.containsKey(next.getCommandName())) {
				throw new IllegalStateException("Command: " + next.getCommandName() + " has already been added");
			}
			
			this.commandMetadataMap.put(next.getCommandName(), next);
			
			if(next instanceof SelectionCommandMetadata) {
				SelectionCommandMetadata scm = (SelectionCommandMetadata) next;
				if(scm.getInterestedCommands() != null) {
					Map<String,String> interestedCommands = scm.getInterestedCommands();
					Iterator<String> interestItor = interestedCommands.keySet().iterator();
					while(interestItor.hasNext()) {
						// TODO: Support multiple dependencies
						String cmdNameKey = interestItor.next();
						reverseSelectionDependenciesMap.put(cmdNameKey, scm.getCommandName());
						dependencyEntryMap.put(scm.getCommandName(), interestedCommands);
					}
				}
			}
		}
	}

	/**
	 * Establish a set of Command instances to add to the 
	 * CommandFactory instance.
	 * 
	 * @param set of Commands to install
	 */
	public synchronized void setCommands(Set<Command> set) {
		for( Command c: set ) {
			addCommand(c);
		}
	}
	
	/**
	 * Add a Command instance to the CommandFactory
	 * 
	 * @param c Command
	 */
	public synchronized void addCommand( Command c ) {
		if( c == null ) {
			throw new IllegalArgumentException("Null command");
		}
		
		if(commandMap.containsKey(c.getName())) {
			throw new IllegalStateException("There is already a command registered with the name: " + c.getName());
		}
		
		commandMap.put(c.getName(), c);
	}
	
	/**
	 * Retrieves a Command instance from the CommandFactory. This could be either
	 * a previously created and add'ed Command or one generated on the fly
	 * @param name
	 * @return
	 */
	public synchronized Command getCommand( String name ) {
		
		// See if we have an existing instance
		
		Command retValue = commandMap.get(name);
		
		// If not, see if we can create one from metadata
		
		if(retValue == null) {
			CommandMetadata cmdMetadata = commandMetadataMap.get(name);
			
			if(cmdMetadata != null) {
				try {
					// We expect our Commands to have a Constructor that takes a 
					// a String name.
					
					// TODO: Enforce this before now
					
					Constructor ctor = cmdMetadata.getCommandClass().getConstructor( new Class[] { String.class } );
					retValue = (Command) ctor.newInstance(new Object[] {name});
					commandMap.put(name, retValue);
					
					if(cmdMetadata.getDisplayName() != null) {
						retValue.setDisplayName(cmdMetadata.getDisplayName());
					}
					
					if(cmdMetadata.getRepresentation() != null) {
						retValue.setRepresentation(cmdMetadata.getRepresentation());
					}
					
					retValue.setEnabled(cmdMetadata.getEnabled());
					
					// See if we are interested in any SelectionCommand instances
					String interestingSelectionCommandName = reverseSelectionDependenciesMap.get(name);
					
					if(interestingSelectionCommandName != null) {
						Command dependentCommand = getCommand(interestingSelectionCommandName);
						
						if(dependentCommand instanceof SelectionCommand) {
							Map<String,String> interestMap = dependencyEntryMap.get(interestingSelectionCommandName);
							String methodName = interestMap.get(name);
							((SelectionCommand) dependentCommand ).addInterestedCommand(retValue,methodName);
						}
					}
					

				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		
		return retValue;
	}
	
	/**
	 * Remove the specified Command instance from the CommandFactory.
	 * 
	 * @param name of Command instance to remove
	 */
	public synchronized void removeCommand( String name ) {
		commandMap.remove(name);
	}
}
