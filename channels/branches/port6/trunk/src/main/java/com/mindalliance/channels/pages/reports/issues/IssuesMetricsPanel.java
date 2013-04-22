/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.IssueMetrics;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssuesMetricsPanel extends AbstractUpdatablePanel {

    private final String issueType;

    private Map<String, List<Issue>> issues;

    private Map<String, String> issueLabels = new HashMap<String, String>();

    private WebMarkupContainer issueMetricsContainer;

    public IssuesMetricsPanel( String id, String issueType, IssueMetrics issueMetrics ) {
        super( id );
        this.issueType = issueType;
        init( issueMetrics );
    }

    private void init( IssueMetrics issueMetrics ) {
        issueMetricsContainer = new WebMarkupContainer( "issue-metrics" );
        IssueMetrics.IssueTypeMetrics issueTypeMetrics = issueMetrics.getIssueTypeMetrics( issueType );
        issueMetricsContainer.setVisible( !issueTypeMetrics.isEmpty() );
        add( issueMetricsContainer );
        addKinds( issueTypeMetrics );
        addNoIssues( issueTypeMetrics );
    }

    private void addNoIssues( IssueMetrics.IssueTypeMetrics issueTypeMetrics ) {
        WebMarkupContainer noIssuesContainer = new WebMarkupContainer( "no-issues" );
        noIssuesContainer.setVisible( issueTypeMetrics.isEmpty() );
        add( noIssuesContainer );
    }

    private void addKinds( final IssueMetrics.IssueTypeMetrics issueTypeMetrics ) {
        issueMetricsContainer.add( new ListView<String>( "kinds", issueTypeMetrics.getIssueKinds() ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                String kind = item.getModelObject();
                IssueMetrics.Metrics metrics = issueTypeMetrics.getIssueTypeMetrics( kind );
                item.add( new Label(
                        "kind",
                        issueTypeMetrics.getIssueLabel( kind ) ) );
                item.add( new Label(
                        "count",
                        issueMetricsLabel( metrics ) ) );
                item.add( new Label(
                        "minor",
                        Integer.toString( issueTypeMetrics.getSeverityCount( kind, Level.Low ) ) ) );
                item.add( new Label(
                        "major",
                        Integer.toString( issueTypeMetrics.getSeverityCount( kind, Level.Medium ) ) ) );
                item.add( new Label(
                        "severe",
                        Integer.toString( issueTypeMetrics.getSeverityCount( kind, Level.High ) ) ) );
                item.add( new Label(
                        "extreme",
                        Integer.toString( issueTypeMetrics.getSeverityCount( kind, Level.Highest ) ) ) );
            }
        } );
    }

    private String issueMetricsLabel( IssueMetrics.Metrics metrics ) {
        MessageFormat mf = new MessageFormat( "{0} ({1,number,percent})" );
        Object[] args = {
                metrics.getCount(),
                Math.max( metrics.getPercent(), metrics.getPercent() > 0 ? 0.01 : 0.0 )};
        return mf.format( args );
    }

}
