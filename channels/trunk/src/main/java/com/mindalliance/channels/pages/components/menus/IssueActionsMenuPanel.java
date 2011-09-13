package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.command.commands.RemoveIssue;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.surveys.Survey;
import com.mindalliance.channels.surveys.SurveyException;
import com.mindalliance.channels.surveys.SurveyService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 12, 2009
 * Time: 1:52:04 PM
 */
public class IssueActionsMenuPanel extends MenuPanel {

    /**
     * Whether the issue is shown collapsed.
     */
    private boolean isCollapsed;

    /**
     * The survey service.
     */
    @SpringBean
    private SurveyService surveyService;

    public IssueActionsMenuPanel( String s, IModel<? extends Issue> model, boolean isCollapsed ) {
        super( s, "Menu", model, null );
        this.isCollapsed = isCollapsed;
        doInit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getMenuItems() throws CommandException {
        Commander commander = getCommander();
        synchronized ( commander ) {
            List<Component> menuItems = new ArrayList<Component>();

            // Undo and redo
            menuItems.add( getUndoMenuItem( "menuItem" ) );
            menuItems.add( getRedoMenuItem( "menuItem" ) );

            // Show/hide details
            menuItems.add(
                    new LinkMenuItem(
                            "menuItem",
                            new Model<String>( isCollapsed ? "Show details" : "Hide details" ),
                            new AjaxFallbackLink( "link" ) {
                                @Override
                                public void onClick( AjaxRequestTarget target ) {
                                    update(
                                            target,
                                            new Change(
                                                    isCollapsed ? Change.Type.Expanded : Change.Type.Collapsed,
                                                    getIssue() ) );
                                }
                            } ) );

            // Create/view survey
            if ( getPlan().isDevelopment() ) {
                String itemLabel = surveyService.isSurveyed( Survey.Type.Remediation, getIssue() )
                        ? "View survey"
                        : "Create survey";

                menuItems.add(
                        getIssue().getDescription().isEmpty() || getIssue().getRemediation().isEmpty() ?

                                newStyledLabel( itemLabel, "disabled" )

                                : new LinkMenuItem(
                                "menuItem",
                                new Model<String>( itemLabel ),
                                new AjaxFallbackLink( "link" ) {
                                    @Override
                                    public void onClick( AjaxRequestTarget target ) {
                                        try {
                                            update(
                                                    target,
                                                    new Change(
                                                            Change.Type.Expanded,
                                                            surveyService.getOrCreateSurvey(
                                                                    Survey.Type.Remediation,
                                                                    getIssue(),
                                                                    getPlan() ) ) );
                                        } catch ( SurveyException e ) {
                                            LoggerFactory.getLogger( getClass() ).warn(
                                                    "Error clicking on survey link", e );
                                            target.addComponent( IssueActionsMenuPanel.this );
                                            target.prependJavascript(
                                                    "alert('Oops -- " + e.getMessage() + "');" );
                                        }
                                    }
                                } ) );
            }

            // Commands
            if ( commander.isTimedOut() )
                menuItems.add( newStyledLabel( "Timed out", "disabled locked" ) );

            else if ( isLockedByUser( getIssue() ) || getLockOwner( getIssue() ) == null )
                menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );

            else
                menuItems.add(
                        editedByLabel( "menuItem", getIssue(), getLockOwner( getIssue() ) ) );

            return menuItems;
        }
    }

    private static Component newStyledLabel( String label, String style ) {
        return new Label( "menuItem", label )
                .add( new AttributeModifier( "class", true, new Model<String>( style ) ) );
    }

    private Issue getIssue() {
        return (Issue) getModel().getObject();
    }

    private List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        Issue issue = getIssue();
        if ( !issue.isDetected() ) {
            commandWrappers.add(
                    new CommandWrapper( new RemoveIssue( (UserIssue) issue ) ) {
                        @Override
                        public void onExecuted( AjaxRequestTarget target, Change change ) {
                            update( target, change );
                        }
                    } );

            if ( !isCollapsed )
                commandWrappers.add(
                        new CommandWrapper( new PasteAttachment( (UserIssue) issue ) ) {
                            @Override
                            public void onExecuted( AjaxRequestTarget target, Change change ) {
                                update( target, change );
                            }
                        } );
        }
        return commandWrappers;
    }
}
