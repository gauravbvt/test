package com.mindalliance.channels.pages.components.entities.menus;

import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.commands.AddUserIssue;
import com.mindalliance.channels.engine.command.commands.PasteAttachment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity panel actions menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2009
 * Time: 12:57:11 PM
 */
public class EntityActionsMenuPanel extends ActionMenuPanel {

    public EntityActionsMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model, null );
    }

    /**
     * {@inheritDoc}
     */
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        if ( isLockable() ) {
            commandWrappers.add( new CommandWrapper( new PasteAttachment( getEntity() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddUserIssue( getEntity() ) ) {
                public void onExecuted(
                        AjaxRequestTarget target,
                        Change change ) {
                    update( target, change );
                }
            } );
        }
        return commandWrappers;
    }

    private ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }

}
