package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.AddSegment;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.DisconnectAndRemoveSegment;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.command.commands.PastePart;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.segment.SegmentEditPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

import java.util.List;
import java.util.Set;
import java.util.Arrays;

/**
 * Segment menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:58:39 AM
 */
public class PlanActionsMenuPanel extends ActionMenuPanel {

    public PlanActionsMenuPanel( String s, IModel<? extends Segment> model, Set<Long> expansions ) {
        super( s, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public List<Component> getMenuItems() {
        synchronized ( getCommander() ) {
            List<Component> menuItems = super.getMenuItems();

            // Import
            if ( getPlan().isDevelopment() )
                menuItems.add(
                    new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Move tasks to segment..." ),
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update( target,
                                        new Change( Change.Type.Expanded, getSegment(),
                                                    SegmentEditPanel.MOVER ) );
                            }
                        } ) );

            // Logout
            menuItems.add(
                new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Logout " + User.current().getUsername() ),
                    new ConfirmedAjaxFallbackLink( "link", "Log out?" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            getCommander().loggedOut( User.current().getUsername() );
                            getRequestCycle().setRequestTarget(
                                new RedirectRequestTarget( "/logout" ) );
                        }
                    } ) );

            return menuItems;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<CommandWrapper> getCommandWrappers() {
        final Segment segment = getSegment();

        return Arrays.asList(
            newWrapper( new PastePart( segment ) ),
            newWrapper( new PasteAttachment( segment ) ),
            newWrapper( new AddPart( segment ) ),
            newWrapper( new AddUserIssue( segment ) ),
            newWrapper( new AddSegment() ),
            new CommandWrapper( new DisconnectAndRemoveSegment( segment ), CONFIRM ) {
                    @Override
                    public void onExecuted( AjaxRequestTarget target, Change change ) {
                        update( target, change );
                    }
                } );
    }

    private CommandWrapper newWrapper( final Command command ) {
        return new CommandWrapper( command ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        };
    }

    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }
}
