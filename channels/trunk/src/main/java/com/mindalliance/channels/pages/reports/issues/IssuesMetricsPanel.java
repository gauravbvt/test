/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.engine.analysis.IssueMetrics;
import com.mindalliance.channels.pages.components.AbstractIssueTablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IssuesMetricsPanel extends AbstractUpdatablePanel {

    private static final int MAX_ROWS = 5;
    private final String issueType;

    private WebMarkupContainer issueMetricsContainer;
    private IssueMetrics.IssueTypeMetrics issueTypeMetrics;
    private Set<String> expandedKinds = new HashSet<String>();

    public IssuesMetricsPanel( String id, String issueType, IssueMetrics issueMetrics ) {
        super( id );
        this.issueType = issueType;
        init( issueMetrics );
    }

    private void init( IssueMetrics issueMetrics ) {
        issueMetricsContainer = new WebMarkupContainer( "issue-metrics" );
        issueTypeMetrics = issueMetrics.getIssueTypeMetrics( issueType );
        issueMetricsContainer.setVisible( !issueTypeMetrics.isEmpty() );
        add( issueMetricsContainer );
        addKinds(  );
        addNoIssues(  );
    }

    private void addNoIssues(  ) {
        WebMarkupContainer noIssuesContainer = new WebMarkupContainer( "no-issues" );
        noIssuesContainer.setVisible( issueTypeMetrics.isEmpty() );
        add( noIssuesContainer );
    }

    private void addKinds(  ) {
        issueMetricsContainer.add( new ListView<String>( "kinds", issueTypeMetrics.getIssueKinds() ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                final String kind = item.getModelObject();
                final WebMarkupContainer tableContainer = new WebMarkupContainer( "tableContainer" );
                tableContainer.setOutputMarkupId( true );
                item.add( tableContainer );
                addIssuesTable( tableContainer, kind );
                IssueMetrics.Metrics metrics = issueTypeMetrics.getIssueTypeMetrics( kind );
                AjaxLink<String> kindLink = new AjaxLink<String>( "kindLink") {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        toggleExpandedOrCollapsed( kind );
                        addIssuesTable( tableContainer, kind );
                        target.add( tableContainer );
                    }
                };
                addTipTitle( kindLink, "Click to show and hide individual issues" );
                kindLink.add( new Label(
                        "kind",
                        issueTypeMetrics.getIssueLabel( kind ) ) );
                item.add( kindLink );
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

            private void addIssuesTable( WebMarkupContainer tableContainer, String kind ) {
                boolean expanded = expandedKinds.contains( kind );
                if ( expanded ) {
                    IssuesOfKindTable issuesOfKindTable = new IssuesOfKindTable(
                            "table",
                            kind
                    );
                    tableContainer.addOrReplace( issuesOfKindTable );
                } else {
                    Label label = new Label( "table", "");
                    label.setOutputMarkupId( true );
                    tableContainer.addOrReplace( label );
                }
                makeVisible( tableContainer, expanded );
            }
        } );
    }

    private boolean toggleExpandedOrCollapsed( String kind ) {
        boolean expanded = expandedKinds.contains( kind );
        expanded = !expanded;
        if ( !expanded )
            expandedKinds.remove( kind );
        else
            expandedKinds.add( kind );
        return expanded;
    }

    private String issueMetricsLabel( IssueMetrics.Metrics metrics ) {
        MessageFormat mf = new MessageFormat( "{0} ({1,number,percent})" );
        Object[] args = {
                metrics.getCount(),
                Math.max( metrics.getPercent(), metrics.getPercent() > 0 ? 0.01 : 0.0 )};
        return mf.format( args );
    }

    private class IssuesOfKindTable extends AbstractTablePanel<Issue> {

        private List<Issue> issues;

        private IssuesOfKindTable( String id, String kind ) {
            super( id, null, MAX_ROWS, null );
            issues = issueTypeMetrics.getIssuesOfKind( kind );
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn( "About", "about.kindLabel", EMPTY ) );
            columns.add( makeColumn( "Named", "about.name", EMPTY ) );
            if ( getCommunityService().isForDomain() )
                columns.add( makeColumn( "In Segment", "about.segmentName", EMPTY ) );
            columns.add( makeColumn( "Severity", "severity.negativeLabel", null, EMPTY, null, "severity.ordinal" ) );
            columns.add( makeColumn( "Description", "description", EMPTY ) );
            columns.add( makeColumn( "Remediation", "remediation", EMPTY ) );
            columns.add( makeColumn( "Reported by", "reportedBy", EMPTY ) );
//            columns.add( makeColumn("Can be waived?", "waivability", EMPTY ));

            // provider and table
            add( new AjaxFallbackDefaultDataTable( "issues",
                    columns,
                    new SortableBeanProvider<Issue>( issues, "kind" ),
                    getPageSize() ) );
        }

    }
}
