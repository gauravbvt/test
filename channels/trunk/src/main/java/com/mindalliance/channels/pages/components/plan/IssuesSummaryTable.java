package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A table summary of all issues in the plan.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/3/11
 * Time: 1:43 PM
 */
public class IssuesSummaryTable extends AbstractUpdatablePanel {

    private List<Issue> allUnwaivedIssues = null;

    private List<Issue> allWaivedIssues = null;
    private List<Level> levels;

    public IssuesSummaryTable( String id ) {
        super( id );
        init();
    }

    private void init() {
        addRobustness();
        addCompleteness();
        addValidity();
    }

    private void addValidity() {
        add( new Label( "validityMetrics", issuesTypeMetrics( Issue.VALIDITY, false ) ) );
        add( new ListView<Level>(
                "validitySeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( item.getModelObject(), Issue.VALIDITY, false ) ) );
            }
        } );
        add( new Label( "validityWaivedMetrics", issuesTypeMetrics( Issue.VALIDITY, true ) ) );
        add( new ListView<Level>(
                "validityWaivedSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( item.getModelObject(), Issue.VALIDITY, true ) ) );
            }
        } );
    }

    private void addCompleteness() {
        add( new Label( "completenessMetrics", issuesTypeMetrics( Issue.COMPLETENESS, false ) ) );
        add( new ListView<Level>(
                "completenessSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( item.getModelObject(), Issue.COMPLETENESS, false ) ) );
            }
        } );
        add( new Label( "completenessWaivedMetrics", issuesTypeMetrics( Issue.COMPLETENESS, true ) ) );
        add( new ListView<Level>(
                "completenessWaivedSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( item.getModelObject(), Issue.COMPLETENESS, true ) ) );
            }
        } );
    }

    private void addRobustness() {
        add( new Label( "robustnessMetrics", issuesTypeMetrics( Issue.ROBUSTNESS, false ) ) );
        add( new ListView<Level>(
                "robustnessSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( item.getModelObject(), Issue.ROBUSTNESS, false ) ) );
            }
        } );
        add( new Label( "robustnessWaivedMetrics", issuesTypeMetrics( Issue.ROBUSTNESS, true ) ) );
        add( new ListView<Level>(
                "robustnessWaivedSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( item.getModelObject(), Issue.ROBUSTNESS, true ) ) );
            }
        } );
    }

    private List<Level> levels() {
        if ( levels == null ) {
            levels = Arrays.asList( Level.values() );
            Collections.reverse( levels );
        }
        return levels;
    }

    private String issuesTypeMetrics( String type, boolean waived ) {
        List<Issue> issues = getAllIssues( type, waived );
        Set<String> kinds = new HashSet<String>();
        for ( Issue issue : issues ) {
            kinds.add( issue.getKind() );
        }
        double n = issues.size();
        double total = issuesCount();
        MessageFormat mf = new MessageFormat( "{0} {1} ({2,number,percent}) , {3} kinds" );
        double percent = ( total == 0 ) ? 0.0 : ( n / total );
        Object[] args = {n, waived ? "waived" : "unresolved", Math.max( percent, percent > 0 ? 0.01 : 0.0 ), kinds.size()};
        return mf.format( args );
    }


    private String severityMetrics( final Level severity, String type, boolean waived ) {
        List<Issue> issues = getAllIssues( type, waived );
        double count = CollectionUtils.select(
                issues,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).getSeverity().equals( severity );
                    }
                }
        ).size();
        MessageFormat mf = new MessageFormat( "{0} {1} ({2,number,percent})" );
        double total = issuesCount();
        double percent = ( total == 0 ) ? 0.0 : ( count / total );
        Object[] args = {count, severity.getNegativeLabel().toLowerCase(), Math.max( percent, percent > 0 ? 0.01 : 0.0 )};
        return mf.format( args );
    }

    private List<Issue> getAllIssues( boolean waived ) {
        if ( !waived ) {
            if ( allUnwaivedIssues == null )
                allUnwaivedIssues = getAnalyst().findAllUnwaivedIssues();
            return allUnwaivedIssues;
        } else {
            if ( allWaivedIssues == null )
                allWaivedIssues = getAnalyst().findAllWaivedIssues();
            return allWaivedIssues;
        }
    }

    private int issuesCount() {
        return getAllIssues( true ).size() + getAllIssues( false ).size();
    }

    @SuppressWarnings( "unchecked" )
    private List<Issue> getAllIssues( final String type, boolean waived ) {
        return (List<Issue>) CollectionUtils.select(
                getAllIssues( waived ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).getType().equals( type );
                    }
                }
        );
    }
}
