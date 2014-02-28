/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.commands.CreateEntityIfNew;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.Releaseable;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Abstract panel.
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
        return commander.doCommand( command );
    }

    /**
     * Execute a command even in production.
     *
     * @param command a command
     * @return the change caused
     */
    protected Change doUnsafeCommand( Command command ) {
        Commander commander = getCommander();
        return commander.doUnsafeCommand( command );
    }

    /**
     * Whether an identifiable object is locked by current user.
     *
     * @param identifiable an identifiable object
     * @return a boolean
     */
    protected boolean isLockedByUser( Identifiable identifiable ) {
        return !isImmutable( identifiable ) && contextAllowsEditing( identifiable ) &&
                ( noLockRequired( identifiable )
                        || getLockManager().isLockedByUser( getUser().getUsername(), identifiable.getId() ) );
    }

    /**
     * Whether an identifiable object is locked by current user.
     *
     * @param id an identifiable object's id
     * @return a boolean
     */
    protected boolean isLockedByUser( long id ) {
        return !isImmutable( id ) && contextAllowsEditing( id ) &&
                ( noLockRequired( id )
                        || getLockManager().isLockedByUser( getUser().getUsername(), id ) );
    }

    private boolean noLockRequired( long id ) {
        return !getPlanCommunity().isModelCommunity() || getCollaborationModel().getId() == id;
    }


    private boolean noLockRequired( Identifiable identifiable ) {
        return !getPlanCommunity().isModelCommunity() || identifiable instanceof CollaborationModel;
    }

    /**
     * Identifiable object is locked by user if necessary.
     *
     * @param identifiable a model object
     * @return a boolean
     */
    protected boolean isLockedByUserIfNeeded( Identifiable identifiable ) {
        return contextAllowsEditing( identifiable )
                && ( isImmutable( identifiable ) || isLockedByUser( identifiable ) );
    }

    protected boolean contextAllowsEditing( long id ) {
        if ( id < 0 ) return true; // editing context id, not model object id
        try {
            ModelObject modelObject = getCommunityService().find( ModelObject.class, id );
            return contextAllowsEditing( modelObject );
        } catch ( NotFoundException e ) {
            return false;
        }
    }

    protected boolean contextAllowsEditing( Identifiable identifiable ) {
        return !getPlanCommunity().isModelCommunity()
                || identifiable.isModifiableInProduction()
                || getCollaborationModel().isDevelopment();
    }

    /**
     * Identifiable object is locked by user if necessary.
     *
     * @param id an identifiable object's id
     * @return a boolean
     */
    protected boolean isLockedByUserIfNeeded( long id ) {
        return contextAllowsEditing( id ) && isLockedByUser( id );
    }

    private boolean isImmutable( Identifiable identifiable ) {
        return identifiable instanceof ModelObject && ( (ModelObject) identifiable ).isImmutable();
    }

    private boolean isImmutable( long id ) {
        try {
            ModelObject mo = getQueryService().find( ModelObject.class, id );
            return mo.isImmutable();
        } catch ( NotFoundException e ) {
            return false;
        }
    }


    /**
     * Get name of lock owner.
     *
     * @param identifiable the possibly locked identifiable
     * @return a string or null
     */
    protected String getLockOwner( Identifiable identifiable ) {
        return getLockManager().getLockUser( identifiable.getId() );
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
    @SuppressWarnings("unchecked")
    protected <T extends ModelEntity> T doSafeFindOrCreateActual( Class<T> clazz, String name ) {
        String safeName = ChannelsUtils.cleanUpName( name );
        return (T) doCommand( new CreateEntityIfNew( getUser().getUsername(),
                clazz,
                safeName,
                ModelEntity.Kind.Actual ) ).getSubject( getCommunityService() );
    }

    /**
     * Safely find or create a model entity type via a command.
     *
     * @param clazz a model entity class
     * @param name  a name
     * @return a model entity
     */
    @SuppressWarnings("unchecked")
    protected <T extends ModelEntity> T doSafeFindOrCreateType( Class<T> clazz, String name ) {
        String safeName = ChannelsUtils.cleanUpName( name );
        return (T) doCommand( new CreateEntityIfNew( getUser().getUsername(),
                clazz,
                safeName,
                ModelEntity.Kind.Type ) ).getSubject( getCommunityService() );
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
