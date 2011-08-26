package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/5/11
 * Time: 8:14 AM
 */
public class IssuesMetrics extends AbstractUpdatablePanel {

    private final String issueType;
    private Map<String, List<Issue>> issues;
    private Map<String, String> issueLabels = new HashMap<String, String>();
    private int allIssuesCount;
    private WebMarkupContainer issueMetricsContainer;

    public IssuesMetrics( String id, String issueType ) {
        super( id );
        this.issueType = issueType;
        init();
    }

    private void init() {
        allIssuesCount = getAnalyst().findAllUnwaivedIssues().size();
        issueMetricsContainer = new WebMarkupContainer( "issue-metrics" );
        issueMetricsContainer.setVisible( !getIssues().isEmpty() );
        add( issueMetricsContainer );
        addKinds();
        addNoIssues();
    }

    private void addNoIssues() {
        WebMarkupContainer noIssuesContainer = new WebMarkupContainer( "no-issues" );
        noIssuesContainer.setVisible( getIssues().isEmpty() );
        add( noIssuesContainer );
    }

    private void addKinds() {
        issueMetricsContainer.add( new ListView<String>(
                "kinds",
                getIssueKinds() ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
/*
                item.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>( item.getIndex() % 2 == 0 ? "even" : "odd" ) ) );
*/
                String kind = item.getModelObject();
                item.add( new Label( "kind", issueLabels.get( kind ) ) );
                item.add( new Label( "count", issueCount( kind ) ) );
                item.add( new Label( "minor", severityCount( kind, Level.Low ) ) );
                item.add( new Label( "major", severityCount( kind, Level.Medium ) ) );
                item.add( new Label( "severe", severityCount( kind, Level.High ) ) );
                item.add( new Label( "extreme", severityCount( kind, Level.Highest ) ) );
            }
        } );
    }

    private String severityCount( String kind, final Level severity ) {
        return Integer.toString( CollectionUtils.select(
                getIssues().get( kind ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).getSeverity().equals( severity );
                    }
                }
        ).size() );
    }

    private String issueCount( String kind ) {
        MessageFormat mf = new MessageFormat( "{0} ({1,number,percent})" );
        double count = issues.get( kind ).size();
        double percent = count / allIssuesCount;
        Object[] args = {count, Math.max( percent, percent > 0 ? 0.01 : 0.0 )};
        return mf.format( args );
    }

    private List<String> getIssueKinds() {
        List<String> issueKinds = new ArrayList<String>( getIssues().keySet() );
        Collections.sort( issueKinds, new Comparator<String>() {
            @Override
            public int compare( String k1, String k2 ) {
                int k1Count = getIssues().get( k1 ).size();
                int k2Count = getIssues().get( k2 ).size();
                return k1Count > k2Count
                        ? -1
                        : k1Count < k2Count
                        ? 1
                        : 0; // todo - sort on severity
            }
        } );
        return issueKinds;
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, List<Issue>> getIssues() {
        if ( issues == null ) {
            issues = new HashMap<String, List<Issue>>();
            List<Issue> allUnwaivedIssues = (List<Issue>) CollectionUtils.select(
                    getAnalyst().findAllUnwaivedIssues(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Issue) object ).getType().equals( issueType );
                        }
                    }
            );
            for ( Issue issue : allUnwaivedIssues ) {
                String kind = issue.getKind();
                List<Issue> issuesOfKind = issues.get( kind );
                if ( issuesOfKind == null ) {
                    issueLabels.put( kind, issue.getDetectorLabel() );
                    issuesOfKind = new ArrayList<Issue>();
                    issues.put( kind, issuesOfKind );
                }
                issuesOfKind.add( issue );
            }
        }
        return issues;
    }
}
