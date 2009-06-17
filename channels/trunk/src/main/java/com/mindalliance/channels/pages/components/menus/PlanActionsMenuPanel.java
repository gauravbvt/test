package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AddPart;
import com.mindalliance.channels.command.commands.AddScenario;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.command.commands.PastePart;
import com.mindalliance.channels.command.commands.RemoveScenario;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.ExportPage;
import com.mindalliance.channels.pages.PlanPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
    public List<Component> getMenuItems() {
        List<Component> menuItems = super.getMenuItems();
        // Export
        menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Export to XML" ),
                new BookmarkablePageLink(
                        "link",
                        ExportPage.class,
                        PlanPage.getParameters( (Scenario) getModel().getObject(), null ) ) ) );

        return menuItems;
    }

    /**
     * {@inheritDoc}
     */
    protected List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        final Scenario scenario = getScenario();
        commandWrappers.add( new CommandWrapper( new PastePart( getScenario() ) ) {
             public void onExecuted( AjaxRequestTarget target, Change change ) {
                 update( target, change );
             }
         } );
        commandWrappers.add( new CommandWrapper( new PasteAttachment( getScenario() ) ) {
             public void onExecuted( AjaxRequestTarget target, Change change ) {
                 update( target, change );
             }
         } );
        commandWrappers.add( new CommandWrapper( new AddPart( scenario ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new AddUserIssue( scenario ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new AddScenario() ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        commandWrappers.add( new CommandWrapper( new RemoveScenario( scenario ) ) {
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
