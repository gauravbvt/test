package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.UserIssue;
import com.mindalliance.channels.pages.components.menus.IssueActionsMenuPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

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
    /**
     * Description text area.
     */
    private TextArea<String> descriptionArea;
    /**
     * Issue type choice.
     */
    private DropDownChoice<String> typeChoice;
    /**
     * Severity level choice.
     */
    private DropDownChoice<Level> severityChoice;
    /**
     * Remediation text area.
     */
    private TextArea<String> remediationArea;
    /**
     * Issue action menu.
     */
    private IssueActionsMenuPanel issueActionMenu;

    public ExpandedIssuePanel( String id, IModel<Issue> model ) {
        super( id );
        this.model = model;
        init();
    }

    private void init() {
        Issue issue = model.getObject();
        setOutputMarkupId( true );
        addIssueActionsMenu();
        // Description
        descriptionArea = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssueActionsMenu();
                target.addComponent( issueActionMenu );
            }
        } );
        add( descriptionArea );
        // Type
        typeChoice = new DropDownChoice<String>(
                "type",
                new PropertyModel<String>( this, "type" ),
                Arrays.asList( Issue.TYPES )
        );
        typeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        add( typeChoice );
        // Severity
        severityChoice = new DropDownChoice<Level>(
                "severity",
                new PropertyModel<Level>( this, "severity" ),
                Arrays.asList( Level.values() ),
                new ChoiceRenderer<Level>(){
                    public Object getDisplayValue( Level level ) {
                        return level.getNegativeLabel();
                    }

                    public String getIdValue( Level object, int index ) {
                        return Integer.toString( index );
                    }
                }
                );
        severityChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        add( severityChoice );
        // Remediation
        remediationArea = new TextArea<String>( "remediation",
                new PropertyModel<String>( this, "remediation" ) );
        remediationArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssueActionsMenu();
                target.addComponent( issueActionMenu );
            }
        } );
        add( remediationArea );
        add( new AttachmentPanel( "attachments", new Model<UserIssue>( (UserIssue) issue ) ) );
        add( new Label( "reported-by",
                new PropertyModel<String>( issue, "reportedBy" ) ) );
        adjustFields();
    }

    private void adjustFields() {
        descriptionArea.setEnabled( !getIssue().isDetected()
                && isLockedByUserIfNeeded( getIssue() ) );
        typeChoice.setEnabled( !getIssue().isDetected()
                && isLockedByUserIfNeeded( getIssue() ) );
        severityChoice.setEnabled( !getIssue().isDetected()
                && isLockedByUserIfNeeded( getIssue() ) );
        remediationArea.setEnabled( !getIssue().isDetected()
                && isLockedByUserIfNeeded( getIssue() ) );
    }

    private void addIssueActionsMenu() {
        issueActionMenu = new IssueActionsMenuPanel(
                "issueActionsMenu",
                new Model<Issue>( model.getObject() ),
                false );
        addOrReplace( issueActionMenu );
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
        doCommand( new UpdatePlanObject( getIssue(), "description", desc ) );
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
        doCommand( new UpdatePlanObject( getIssue(), "remediation", rem ) );
    }

    /**
     * Get the issue's type.
     * @return an issue type
     */
    public String getType() {
        return getIssue().getType();
    }

    /**
     * Sets issue's type.
     *
     * @param type an issue severity level
     */
    public void setType( String type ) {
        doCommand( new UpdatePlanObject( getIssue(), "type", type ) );
    }

    /**
     * Get the issue's severity.
     * @return an issue level
     */
    public Level getSeverity() {
        return getIssue().getSeverity();
    }

    /**
     * Sets issue's severity.
     *
     * @param severity an issue severity level
     */
    public void setSeverity( Level severity ) {
        doCommand( new UpdatePlanObject( getIssue(), "severity", severity ) );
    }

}
