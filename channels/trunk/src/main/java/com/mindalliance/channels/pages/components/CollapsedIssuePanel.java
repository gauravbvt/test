package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.pages.components.menus.IssueActionsMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 7:52:29 PM
 */
public class CollapsedIssuePanel extends AbstractCommandablePanel {
    /**
     * Issue in panel
     */
    private IModel<Issue> model;

    public CollapsedIssuePanel( String id, IModel<Issue> model ) {
        super( id );
        this.model = model;
        init();
    }

    private void init() {
        final Issue issue = getIssue();
        Label label;
        Label suggestion;
        if ( issue.isDetected() ) {
            label = new Label( "issue-label", new PropertyModel( issue, "label" ) );
            suggestion = new Label( "issue-suggestion", new PropertyModel( issue, "remediation" ) );
        } else {
            label = new Label( "issue-label", new AbstractReadOnlyModel() {

                public Object getObject() {
                    return issue.getLabel( IssuesPanel.MAX_LENGTH );
                }
            } );
            suggestion = new Label( "issue-suggestion", new AbstractReadOnlyModel() {

                public Object getObject() {
                    return StringUtils.abbreviate( issue.getRemediation(), IssuesPanel.MAX_LENGTH );
                }
            } );
        }
        add( label );
        add( suggestion );
        // Waiving
        WebMarkupContainer waivedContainer = new WebMarkupContainer( "waived-container" );
        makeVisible( waivedContainer, issue.canBeWaived() );
        add( waivedContainer );
        CheckBox waiveCheckBox = new CheckBox(
                "waived",
                new PropertyModel<Boolean>( this, "waived" ) );
        waiveCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change(
                        Change.Type.Updated,
                        issue.getAbout(),
                        "waivedIssueDetections" ) );
            }
        } );
        waiveCheckBox.setEnabled( isLockedByUser( issue.getAbout() ) );
        waivedContainer.add( waiveCheckBox );

        // Menu
        WebMarkupContainer menubar = new WebMarkupContainer( "menubar" );
        add( menubar );
        IssueActionsMenuPanel actionsMenu = new IssueActionsMenuPanel(
                "issueActionsMenu",
                new Model<Issue>( model.getObject() ),
                true );
        menubar.add( actionsMenu );
        makeVisible( menubar, !issue.isDetected() );
    }

    private Issue getIssue() {
        return model.getObject();
    }

    /**
     * Whether this kind of issue is waived for this model object.
     *
     * @return a boolean
     */
    public boolean isWaived() {
        return getIssue().isWaived();
    }

    /**
     * Waive or unwaive this kind of issue for this model object.
     *
     * @param waive a boolean
     */
    public void setWaived( boolean waive ) {
        Command command = UpdateObject.makeCommand(
                getIssue().getAbout(),
                "waivedIssueDetections",
                getIssue().getKind(),
                waive ? UpdateObject.Action.Add : UpdateObject.Action.Remove
        );
        doCommand( command );
    }
}
