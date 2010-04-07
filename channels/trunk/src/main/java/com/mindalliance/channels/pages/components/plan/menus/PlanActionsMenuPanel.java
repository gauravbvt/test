package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.AddSegment;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.DisconnectAndRemoveSegment;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.command.commands.PastePart;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.pages.ExportPage;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Segment menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:58:39 AM
 */
public class PlanActionsMenuPanel extends ActionMenuPanel {

    public PlanActionsMenuPanel(
            String s,
            IModel<? extends Segment> model,
            Set<Long> expansions ) {
        super( s, model, expansions );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    /**
     * {@inheritDoc}
     */
    public List<Component> getMenuItems() throws CommandException {
        List<Component> menuItems = super.getMenuItems();
        // Import
        if ( getPlan().isDevelopment() ) {
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Import plan segment" ),
                    new AjaxFallbackLink( "link" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            ( (PlanPage) getPage() ).importSegment( target );
                        }
                    } ) );
        }
        // Export
        menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Export plan segment" ),
                new BookmarkablePageLink(
                        "link",
                        ExportPage.class,
                        PlanPage.getParameters( (Segment) getModel().getObject(), null ) ) ) );
        // Logout
        ConfirmedAjaxFallbackLink logoutLink = new ConfirmedAjaxFallbackLink( "link", "Log out?" ) {
            public void onClick( AjaxRequestTarget target ) {
                getRequestCycle().setRequestTarget( new RedirectRequestTarget( "/logout" ) );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Logout " + User.current().getUsername() ),
                logoutLink
        ) );
        return menuItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
            final Segment segment = getSegment();
            commandWrappers.add( new CommandWrapper( new PastePart( getSegment() ) ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new PasteAttachment( getSegment() ) ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddPart( segment ) ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddUserIssue( segment ) ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new AddSegment() ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
            commandWrappers.add( new CommandWrapper( new DisconnectAndRemoveSegment( segment ), CONFIRM ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        return commandWrappers;
    }


    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }

}
