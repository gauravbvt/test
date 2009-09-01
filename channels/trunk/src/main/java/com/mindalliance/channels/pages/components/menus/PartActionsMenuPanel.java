package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.CopyPart;
import com.mindalliance.channels.command.commands.Disintermediate;
import com.mindalliance.channels.command.commands.DuplicatePart;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.command.commands.PasteFlow;
import com.mindalliance.channels.command.commands.RemovePart;
import com.mindalliance.channels.command.commands.SatisfyAllNeeds;
import com.mindalliance.channels.command.commands.SetPartFromCopy;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Actions menu for a part..
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 1:30:09 PM
 */
public class PartActionsMenuPanel extends ActionMenuPanel {

    public PartActionsMenuPanel( String s, IModel<? extends Part> model ) {
        super( s, model, null );
    }

    /**
     * {@inheritDoc}
     */
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        commandWrappers.add( new CommandWrapper( new CopyPart( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new SetPartFromCopy( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new RemovePart( getPart() ), CONFIRM ) {
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
        commandWrappers.add( new CommandWrapper( new PasteAttachment( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new PasteFlow( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new AddUserIssue( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new DuplicatePart( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new SatisfyAllNeeds( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new Disintermediate( getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        return commandWrappers;
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }

}
