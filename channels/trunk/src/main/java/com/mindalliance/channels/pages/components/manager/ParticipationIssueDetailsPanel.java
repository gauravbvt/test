package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

/**
 * Participation issue details panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/8/13
 * Time: 3:22 PM
 */
public class ParticipationIssueDetailsPanel extends AbstractUpdatablePanel {

    private IModel<Issue> participationIssueModel;

    public ParticipationIssueDetailsPanel( String id, IModel<Issue> participationIssueModel ) {
        super( id );
        this.participationIssueModel = participationIssueModel;
        init();
    }

    private void init() {
        addKind();
        addDescription();
        addRemediation();
    }

    private void addKind() {
        add(  new Label( "kind", getIssue().getDetectorLabel() ) );
    }

    private void addDescription() {
        add( new Label( "description", getIssue().getDescription() ) );
    }

    private void addRemediation() {
        ListView<String> remediationListView = new ListView<String>(
                "remediationOptions",
                getIssue().getRemediationOptions()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label( "option", item.getModel() ) );
            }
        };
        add( remediationListView );
    }

    private Issue getIssue() {
        return participationIssueModel.getObject();
    }
}
