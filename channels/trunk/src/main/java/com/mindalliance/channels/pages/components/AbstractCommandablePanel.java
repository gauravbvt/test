package com.mindalliance.channels.pages.components;

import org.apache.wicket.model.IModel;
import org.apache.wicket.WicketRuntimeException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Service;

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

    protected Change doCommand( Command command ) {
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

    protected boolean isLockedByUser( Identifiable identifiable ) {
        return getLockManager().isLockedByUser( identifiable );
    }

    protected boolean requestLockOn( Identifiable identifiable ) {
        return getLockManager().requestLockOn( identifiable );
    }

    protected boolean releaseAnyLockOn( Identifiable identifiable ) {
        return getLockManager().releaseAnyLockOn( identifiable );
    }


}
