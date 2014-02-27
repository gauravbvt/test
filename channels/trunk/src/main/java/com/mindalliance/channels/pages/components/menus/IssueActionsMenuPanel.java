/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.command.commands.RemoveIssue;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.UserIssue;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

public class IssueActionsMenuPanel extends MenuPanel {

    /**
     * Whether the issue is shown collapsed.
     */
    private boolean isCollapsed;

    /**
     * The survey service.
     */
/*
    @SpringBean
    private SurveyService surveyService;
*/

    public IssueActionsMenuPanel( String s, IModel<? extends Issue> model, boolean isCollapsed ) {
        super( s, "Menu", model, null );
        this.isCollapsed = isCollapsed;
        doInit();
    }

    @Override
    public String getUserRoleId() {
        return "developer";
    }

    @Override
    public String getHelpTopicId() {
        return "issue-menu";
    }

    @Override
    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    @Override
    public List<LinkMenuItem> getMenuItems() throws CommandException {
        Commander commander = getCommander();
        synchronized ( commander ) {
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();

            // Show/hide details
            menuItems.add( new LinkMenuItem( "menuItem",
                    new Model<String>( isCollapsed ? "Show details" : "Hide details" ),
                    new AjaxLink( "link" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            update( target,
                                    new Change( isCollapsed ?
                                            Change.Type.Expanded :
                                            Change.Type.Collapsed, getIssue() ) );
                        }
                    } ) );

            // Undo and redo
            menuItems.add( getUndoMenuItem( "menuItem" ) );
            menuItems.add( getRedoMenuItem( "menuItem" ) );

            // Commands
            if ( commander.isTimedOut( getUser().getUsername() ) )
                menuItems.add( timeOutLinkMenuItem( "menuItem" ) );

            else if ( isLockedByUser( getIssue() ) || getLockOwner( getIssue() ) == null )
                menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );

            else
                menuItems.add( editedByLinkMenuItem( "menuItem", getIssue(), getLockOwner( getIssue() ) ) );

            return menuItems;
        }
    }

/*
    private static Component newStyledLabel( String label, String style ) {
        return new Label( "menuItem", label ).add( new AttributeModifier( "class", new Model<String>( style ) ) );
    }
*/

    private Issue getIssue() {
        return (Issue) getModel().getObject();
    }

    private List<CommandWrapper> getCommandWrappers( ) {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        Issue issue = getIssue();
        if ( !issue.isDetected() ) {
            commandWrappers.add( new CommandWrapper( new RemoveIssue( getUser().getUsername(), (UserIssue)issue ), CONFIRM ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );

            if ( !isCollapsed )
                commandWrappers.add( new CommandWrapper( new PasteAttachment( getUser().getUsername(),
                                                                              (UserIssue) issue ) ) {
                    @Override
                    public void onExecuted( AjaxRequestTarget target, Change change ) {
                        update( target, change );
                    }
                } );
        }
        return commandWrappers;
    }
}
