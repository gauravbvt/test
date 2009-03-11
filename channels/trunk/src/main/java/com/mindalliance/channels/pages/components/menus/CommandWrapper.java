package com.mindalliance.channels.pages.components.menus;

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

    public CommandWrapper( Command command ) {
        this.command = command;
    }

    private Command command;

    public abstract void onExecution( AjaxRequestTarget target, Object result);

    public Command getCommand() {
        return command;
    }
}
