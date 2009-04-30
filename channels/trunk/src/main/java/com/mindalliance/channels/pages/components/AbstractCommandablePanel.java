package com.mindalliance.channels.pages.components;

import org.apache.wicket.model.IModel;
import org.apache.wicket.WicketRuntimeException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.LockManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.model.Identifiable;

import java.util.Set;

/**
 * Abstract panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 9, 2009
 * Time: 9:05:48 PM
 */
public class AbstractCommandablePanel extends AbstractUpdatablePanel {

    public AbstractCommandablePanel( String id ) {
        super( id );
    }

    public AbstractCommandablePanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
    }

    /**
     * Execute a command.
     *
     * @param command a command
     * @return the change caused
     */
    protected Change doCommand( Command command ) {
        Commander commander = getCommander();
        try {
            return commander.doCommand( command );
        } catch ( CommandException e ) {
            throw new WicketRuntimeException( e );
        }
    }

    protected Commander getCommander() {
        return Channels.instance().getCommander();
    }

    /**
     * Get the lock manager.
     *
     * @return a lock manager
     */
    protected LockManager getLockManager() {
        return Channels.instance().getLockManager();
    }

    /**
     * Whether an identifiable object is locked by current user.
     *
     * @param identifiable an identifiable object
     * @return a boolean
     */
    protected boolean isLockedByUser( Identifiable identifiable ) {
        return getLockManager().isLockedByUser( identifiable );
    }

    /**
     * Get name of lock owner.
     * @param identifiable the possibly locked identifiable
     * @return a string or null
     */
    protected String getLockOwner( Identifiable identifiable ) {
        return getLockManager().getLockOwner( identifiable.getId() );
    }


}
