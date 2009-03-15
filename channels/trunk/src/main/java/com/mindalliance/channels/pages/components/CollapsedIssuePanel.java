package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Issue;
import com.mindalliance.channels.pages.components.menus.IssueActionsMenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 7:52:29 PM
 */
public class CollapsedIssuePanel extends AbstractUpdatablePanel {
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
        final Issue issue = model.getObject();
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
        WebMarkupContainer menubar = new WebMarkupContainer( "menubar" );
        add( menubar );
        IssueActionsMenuPanel actionsMenu = new IssueActionsMenuPanel(
                "issueActionsMenu",
                new Model<Issue>( model.getObject() ),
                true );
        menubar.add( actionsMenu );
        makeVisible( menubar, !issue.isDetected() );
    }
}
