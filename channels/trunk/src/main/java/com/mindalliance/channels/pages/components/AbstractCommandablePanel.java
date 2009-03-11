package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.WicketRuntimeException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Service;

import java.util.ArrayList;

/**
 * Abstract panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 9:05:48 PM
 */
public class AbstractCommandablePanel extends AbstractUpdatablePanel {

    public AbstractCommandablePanel( String s ) {
        super( s );
    }

    public AbstractCommandablePanel( String s, IModel<?> iModel ) {
        super( s, iModel );
    }

    protected Object doCommand( Command command ) {
        Commander commander = getCommander();
        try {
            return commander.doCommand( command );
        } catch ( CommandException e ) {
            throw new WicketRuntimeException( e );
        }
    }

    protected Commander getCommander() {
        return Project.getProject().getCommander();
    }

    protected LockManager getLockManager() {
        return Project.getProject().getLockManager();
    }

    protected Service getService() {
        return Project.getProject().getService(); 
    }

    protected boolean isLocked( final Identifiable identifiable ) {
        return !getLockManager().canGrabLocksOn( new ArrayList<Long>() {
            {
                add( identifiable.getId());
            }
        } );
    }


}
