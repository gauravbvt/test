package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.commons.lang.StringUtils;
import com.mindalliance.channels.Issue;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 7:52:29 PM
 */
public class CollapsedIssuePanel extends Panel implements DeletableIssue {

    private Issue issue;

    private boolean markedForDeletion = false;

    public CollapsedIssuePanel( String id, Issue issue ) {
        super( id );
        this.issue = issue;
        init();
    }

    private void init() {
        Label label;
        Label suggestion;
        if ( issue.isDetected() ) {
            label = new Label( "issue-label", new PropertyModel( issue, "description" ) );
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
        WebMarkupContainer menu = new WebMarkupContainer( "menu" );
        add( menu );
        ExternalLink expandLink = new ExternalLink( "expand", getRequest().getURL() + "&expand=" + issue.getId() );
        menu.add( expandLink );
        CheckBox deleteCheckBox = new CheckBox( "delete",
                new PropertyModel<Boolean>( this, "markedForDeletion" ) );
        menu.add( deleteCheckBox );
        menu.setVisible( !issue.isDetected() );
    }

    /**
     * @return the underlying issue
     */
    public Issue getIssue() {
        return issue;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion( boolean markedForDeletion ) {
        this.markedForDeletion = markedForDeletion;
    }

}
