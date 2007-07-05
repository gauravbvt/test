// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.command;

/**
 * An extension of the Command interface to indicate the ability to be undone.
 * 
 * @author brian
 *
 */
public interface UndoableCommand extends Command {
	/**
	 * Undo the previous Command execution.
	 */
	public void undo();
}
