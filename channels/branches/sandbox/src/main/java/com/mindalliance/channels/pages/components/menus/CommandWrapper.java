package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * A command plus UI update instructions.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 9:02:28 PM
 */
public abstract class CommandWrapper implements Serializable {

    /** The wrapped command. */
    private Command command;

    /** Whether the command requires confirmation. */
    private boolean confirm;

    protected CommandWrapper( Command command ) {
        this.command = command;
    }

    protected CommandWrapper( Command command, boolean confirm ) {
        this( command );
        this.confirm = confirm;
    }

    public boolean isConfirm() {
        return confirm;
    }

    /**
     * Invoked after the command has executed.
     *
     * @param target an ajax request target
     * @param change the change caused by the command's execution
     */
    public abstract void onExecuted( AjaxRequestTarget target, Change change );

    public Command getCommand() {
        return command;
    }
}
