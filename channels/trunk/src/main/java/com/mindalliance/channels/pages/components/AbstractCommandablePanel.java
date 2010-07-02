package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.commands.CreateEntityIfNew;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.Releaseable;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;

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

    public AbstractCommandablePanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel, null );
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

    /**
     * Whether an identifiable object is locked by current user.
     *
     * @param identifiable an identifiable object
     * @return a boolean
     */
    protected boolean isLockedByUser( Identifiable identifiable ) {
        return getPlan().isDevelopment()
                && !isImmutable( identifiable )
                && getLockManager().isLockedByUser( identifiable );
    }

    /**
     * Model object is locked by user if necessary.
     *
     * @param identifiable a model object
     * @return a boolean
     */
    protected boolean isLockedByUserIfNeeded( Identifiable identifiable ) {
        return getPlan().isDevelopment() &&
                ( identifiable instanceof Segment
                        || identifiable instanceof Plan
                        || isImmutable( identifiable )
                        || isLockedByUser( identifiable ) );
    }

    private boolean isImmutable( Identifiable identifiable ) {
        return identifiable instanceof ModelObject && ( (ModelObject) identifiable ).isImmutable();
    }


    /**
     * Get name of lock owner.
     *
     * @param identifiable the possibly locked identifiable
     * @return a string or null
     */
    protected String getLockOwner( Identifiable identifiable ) {
        return getLockManager().getLockOwner( identifiable.getId() );
    }

    /**
     * Whether identifiable is locked by another user.
     *
     * @param identifiable an identifiable
     * @return a boolean
     */
    protected boolean isLockedByOtherUser( Identifiable identifiable ) {
        return !isLockedByUser( identifiable ) && getLockOwner( identifiable ) != null;
    }

    /**
     * Safely find or create a model entity via a command.
     *
     * @param clazz a model entity class
     * @param name  a name
     * @return a model entity
     */
    @SuppressWarnings( "unchecked" )
    protected <T extends ModelEntity> T doSafeFindOrCreate( Class<T> clazz, String name ) {
        return (T) doCommand( new CreateEntityIfNew(
                    clazz,
                    name,
                    ModelEntity.Kind.Actual ) ).getSubject( getQueryService() );
    }

    /**
     * Safely find or create a model entity type via a command.
     *
     * @param clazz a model entity class
     * @param name  a name
     * @return a model entity
     */
    @SuppressWarnings( "unchecked" )
    protected <T extends ModelEntity> T doSafeFindOrCreateType( Class<T> clazz, String name ) {
        return (T) doCommand( new CreateEntityIfNew(
                clazz,
                name,
                ModelEntity.Kind.Type ) ).getSubject( getQueryService() );
    }

    /**
     * Release locks acquired after initialization.
     */
    public void release() {
        Releaseable releaseable = findParent( Releaseable.class );
        if ( releaseable != null ) {
            releaseable.release();
        }
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    public void releaseAnyLockOn( Identifiable identifiable ) {
        Releaseable releaseable = findParent( Releaseable.class );
        if ( releaseable != null ) {
            releaseable.releaseAnyLockOn( identifiable );
        }
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    public void requestLockOn( Identifiable identifiable ) {
        Releaseable releaseable = findParent( Releaseable.class );
        if ( releaseable != null ) {
            releaseable.requestLockOn( identifiable );
        }
    }
}
