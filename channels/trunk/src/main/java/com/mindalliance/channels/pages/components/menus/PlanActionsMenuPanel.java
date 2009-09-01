package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.AddScenario;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.command.commands.PastePart;
import com.mindalliance.channels.command.commands.RemoveScenario;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.ExportPage;
import com.mindalliance.channels.pages.PlanPage;
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
 * Scenario menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:58:39 AM
 */
public class PlanActionsMenuPanel extends ActionMenuPanel {

    public PlanActionsMenuPanel(
            String s,
            IModel<? extends Scenario> model,
            Set<Long> expansions ) {
        super( s, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public List<Component> getMenuItems() {
        List<Component> menuItems = super.getMenuItems();
        // Import
        menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Import scenario" ),
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        ( (PlanPage) getPage() ).importScenario( target );
                    }
                } ) );

        // Export
        menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Export scenario" ),
                new BookmarkablePageLink(
                        "link",
                        ExportPage.class,
                        PlanPage.getParameters( (Scenario) getModel().getObject(), null ) ) ) );
        // Logout
        AjaxFallbackLink logoutLink = new AjaxFallbackLink( "link" )  {
                    public void onClick( AjaxRequestTarget target ) {
                        getRequestCycle().setRequestTarget(new RedirectRequestTarget("/logout"));
                    }
                };
        confirm( logoutLink, "Logging out?");
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
        final Scenario scenario = getScenario();
        commandWrappers.add( new CommandWrapper( new PastePart( getScenario() ) ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new PasteAttachment( getScenario() ) ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new AddPart( scenario ) ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new AddUserIssue( scenario ) ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new AddScenario() ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new RemoveScenario( scenario ), CONFIRM ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        return commandWrappers;
    }


    private Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }

}
