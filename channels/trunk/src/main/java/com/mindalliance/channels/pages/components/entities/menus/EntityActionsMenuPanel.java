package com.mindalliance.channels.pages.components.entities.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AddUserIssue;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.community.CommunityService;
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

    private List<Long> entityHistory = new ArrayList<Long>(  );

    public EntityActionsMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model, null );
    }

    @Override
    public String getHelpTopicId() {
        return "actions-entity";
    }

    /**
     * {@inheritDoc}
     */
    protected List<CommandWrapper> getCommandWrappers( CommunityService communityService ) {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        if ( isLockable( communityService ) ) {
            commandWrappers.add( new CommandWrapper( new PasteAttachment( getUser().getUsername(), getEntity() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddUserIssue( getUser().getUsername(), getEntity() ) ) {
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
