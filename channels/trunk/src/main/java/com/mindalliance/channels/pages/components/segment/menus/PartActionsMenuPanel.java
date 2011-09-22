package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AddUserIssue;
import com.mindalliance.channels.core.command.commands.CopyPart;
import com.mindalliance.channels.core.command.commands.Disintermediate;
import com.mindalliance.channels.core.command.commands.DuplicatePart;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.command.commands.PasteFlow;
import com.mindalliance.channels.core.command.commands.RemovePart;
import com.mindalliance.channels.core.command.commands.SatisfyAllNeeds;
import com.mindalliance.channels.core.command.commands.SetPartFromCopy;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Actions menu for a part..
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 1:30:09 PM
 */
public class PartActionsMenuPanel extends ActionMenuPanel {

    public PartActionsMenuPanel( String s, IModel<? extends Part> model, Set<Long> expansions ) {
        super( s, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        commandWrappers.add( new CommandWrapper( new CopyPart( User.current().getUsername(), getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        if ( isExpanded( getPart() ) && getCommander().isPartCopied( User.current().getUsername() ) ) {
            commandWrappers.add( new CommandWrapper( new SetPartFromCopy( User.current().getUsername(), getPart() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        }
        commandWrappers.add( new CommandWrapper( new RemovePart( User.current().getUsername(), getPart() ), CONFIRM ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                Part part = getPart();
                update( target, change );
                if ( part.getActor() != null )
                    getCommander().cleanup( Actor.class, part.getActor().getName() );
                if ( part.getRole() != null )
                    getCommander().cleanup( Role.class, part.getRole().getName() );
                if ( part.getOrganization() != null )
                    getCommander().cleanup( Organization.class, part.getOrganization().getName() );
                if ( part.getJurisdiction() != null )
                    getCommander().cleanup( Place.class, part.getJurisdiction().getName() );
                if ( part.getLocation() != null )
                    getCommander().cleanup( Place.class, part.getLocation().getName() );
            }
        } );
        if ( isExpanded( getPart() ) ) {
            commandWrappers.add( new CommandWrapper( new PasteAttachment( User.current().getUsername(), getPart() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new PasteFlow( User.current().getUsername(), getPart() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddUserIssue( User.current().getUsername(), getPart() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new DuplicatePart( User.current().getUsername(), getPart() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new SatisfyAllNeeds( User.current().getUsername(), getPart() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new Disintermediate( User.current().getUsername(), getPart() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        }
        return commandWrappers;
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

}
