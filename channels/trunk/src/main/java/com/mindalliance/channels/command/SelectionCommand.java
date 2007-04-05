// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.command;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract subclass of Command that handles object selection.
 * 
 * Object selection is a semantically meaningful activity that 
 * will feed values into other commands. They are also useful
 * to capture for recording/playback purposes.
 * 
 * @author brian
 *
 */
abstract public class SelectionCommand extends AbstractCommand {
	protected Object selection;
	private Class requiredType;
	private Map<Command,PropertyChangeListener> interestedCommandMap 
		= new HashMap<Command,PropertyChangeListener>();
	
	/**
	 * Create an enabled SelectionCommand instance with no
	 * displayName yet.
	 * 
	 * @param name
	 */
	public SelectionCommand(String name) {
		super(name);
	}
	
	/**
	 * Create an enabled SelectionCommand instance with the 
	 * specified name and displayName.
	 * 
	 * @param name
	 * @param displayName
	 */
	
	public SelectionCommand(String name, String displayName ) {
		super(name, displayName);
	}
	
	/**
	 * Create a SelectionCommand instance with the specified
	 * name, displayName and enabled state value.
	 * 
	 * @param name
	 * @param displayName
	 * @param enabled
	 */
	public SelectionCommand(String name, String displayName, boolean enabled) {
		super(name, displayName, enabled);
	}
	
	/**
	 * Adds the specified Command as interested in selection events on this
	 * SelectionCommand. When selections are performed, the specified method
	 * will be called with the new value.
	 * 
	 * @param c
	 * @param method
	 */
	public synchronized void addInterestedCommand(Command c, String method) {
		if(!interestedCommandMap.containsKey(c)) {
			PropertyChangeListener pcl = generatePropertyChangeListener(c,method); 
			interestedCommandMap.put(c, pcl );
			this.addPropertyChangeListener("selection", pcl);
		}
	}
	
	/**
	 * Removes the specified Command as an interested command for selection
	 * events on this SelectionCommand instance.
	 * 
	 * @param c
	 */
	public synchronized void removeInterestedCommand(Command c) {
		PropertyChangeListener pcl = interestedCommandMap.get(c);
		if( pcl != null ) {
			this.removePropertyChangeListener("selection", pcl);
			interestedCommandMap.remove(c);
		}
	}
	
	/**
	 * Retrieves the last selection object set on this SelectionCommand
	 * instance.
	 * 
	 * @return selected Object
	 */
	public synchronized Object getSelection() {
		return selection;
	}

	/**
	 * Set by some external event process in response to a selection event
	 * (i.e. user, programmatic, macro playback, etc)
	 * 
	 * @param selection
	 */
	public synchronized void setSelection(Object selection) {
		checkSelection(selection);
		this.selection = selection;
	}
	
	/**
	 * Subclasses can indicate that they require specific selection
	 * types.
	 * 
	 * @param c
	 */
	protected void setRequiredType(Class c) {
		this.requiredType = c;
	}

	/**
	 * Determines if the specified object is an instance
	 * of a previously defined requiredType.
	 * 
	 * @param obj
	 * @see SelectionCommand.setRequiredType();
	 */
	protected void checkSelection(Object obj) {
		if( requiredType != null && !requiredType.isInstance(obj) ) {
			throw new IllegalArgumentException(getName() + " requires a selection type of: " + requiredType.getCanonicalName());
		}
	}
	
	/**
	 * Generates a PropertyChangeListener to be installed on this SelectionCommand
	 * instance that will call the specified method on the specified
	 * Command instance when selections occur.
	 *  
	 * @param cmd
	 * @param methodName
	 * @return
	 */
	protected PropertyChangeListener generatePropertyChangeListener(final Command cmd, final String methodName) {
		PropertyChangeListener retValue = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent pce) {
				if(requiredType != null) {
					Class c = cmd.getClass();
					
					try {
						Method m = null;
						
						Method [] methods = c.getMethods();
						for( Method match : methods ) {
							if( match.getName().equals(methodName)) {
								m = match;
							}
						}

						m.invoke(cmd, new Object[] { pce.getNewValue() } );
					} catch( Exception e ) {
						e.printStackTrace();
					}
				} else {
					throw new UnsupportedOperationException("Non-typed listener hasn't been supported yet");
				}
			}
		};
		
		return retValue;
	}
}
