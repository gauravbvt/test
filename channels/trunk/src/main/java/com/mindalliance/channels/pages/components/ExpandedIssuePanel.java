package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Issue;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.command.commands.UpdateProjectObject;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.pages.components.menus.IssueActionsMenuPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.Component;
import org.apache.wicket.AttributeModifier;

import java.util.Arrays;

/**
 * Editable (user) issue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 7:51:38 PM
 */
public class ExpandedIssuePanel extends AbstractCommandablePanel {
    /**
     * Issue shown in panel
     */
    private IModel<Issue> model;

    public ExpandedIssuePanel( String id, IModel<Issue> model ) {
        super( id );
        this.model = model;
        init();
    }

    private void init() {
        Issue issue = model.getObject();
        setOutputMarkupId( true );
        addIssueActionsMenu();
        TextArea<String> descriptionArea = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        descriptionArea.setEnabled( isLockedByUser( getIssue() ) );
        add( descriptionArea );
        DropDownChoice<Issue.Level> levelChoice = new DropDownChoice<Issue.Level>(
                "severity",
                new PropertyModel<Issue.Level>( this, "severity" ),
                Arrays.asList( Issue.Level.values() ) );
        levelChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        levelChoice.setEnabled( isLockedByUser( getIssue() ) );
        add( levelChoice );
        TextArea<String> remediationArea = new TextArea<String>( "remediation",
                new PropertyModel<String>( this, "remediation" ) );
        remediationArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        remediationArea.setEnabled( isLockedByUser( getIssue() ) );
        add( remediationArea );
        add( new AttachmentPanel( "attachments", new Model<UserIssue>( (UserIssue) issue ) ) );
        add( new Label( "reported-by",
                new PropertyModel<String>( issue, "reportedBy" ) ) );
    }

    private void addIssueActionsMenu() {
        Component actionsMenu;
        LockManager lockManager = getLockManager();
        if ( lockManager.isLockedByUser( getIssue() ) ) {
            actionsMenu = new IssueActionsMenuPanel(
                    "issueActionsMenu",
                    new Model<Issue>( model.getObject() ),
                    false );
        } else {
            String otherUser = lockManager.getLockOwner( getIssue().getId() );
            actionsMenu = new Label( "issueActionsMenu", new Model<String>( "Edited by " + otherUser ) );
            actionsMenu.add( new AttributeModifier( "class", true, new Model<String>( "locked" ) ) );
        }
        add( actionsMenu );
    }

    private Issue getIssue() {
        return model.getObject();
    }

    public String getDescription() {
        return getIssue().getDescription();
    }

    /**
     * Sets description.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        doCommand( new UpdateProjectObject( getIssue(), "description", desc ) );
    }

    public String getRemediation() {
        return getIssue().getRemediation();
    }

    /**
     * Sets remediation.
     *
     * @param rem a string
     */
    public void setRemediation( String rem ) {
        doCommand( new UpdateProjectObject( getIssue(), "remediation", rem ) );
    }

    public Issue.Level getSeverity() {
        return getIssue().getSeverity();
    }

    /**
     * Sets severity.
     *
     * @param severity an issue severity level
     */
    public void setSeverity( Issue.Level severity ) {
        doCommand( new UpdateProjectObject( getIssue(), "severity", severity ) );
    }


}
