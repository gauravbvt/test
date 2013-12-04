/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.IssueMetrics;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A table summary of all issues in the plan.
 */
public class IssuesSummaryTable extends AbstractUpdatablePanel {

    private List<Level> levels;

    public IssuesSummaryTable( String id, IssueMetrics issueMetrics ) {
        super( id );
        init( issueMetrics );
    }

    private void init( IssueMetrics issueMetrics ) {
        addRobustness( issueMetrics );
        addCompleteness( issueMetrics );
        addValidity( issueMetrics );
    }

    private void addValidity( final IssueMetrics issueMetrics ) {
        add( new Label(
                "validityMetrics",
                issuesSummary( issueMetrics.getIssueSummaryMetrics( Issue.VALIDITY, false ) ) ) );
        add( new ListView<Level>(
                "validitySeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( issueMetrics, item.getModelObject(), Issue.VALIDITY, false ) ) );
            }
        } );
        add( new Label(
                "validityWaivedMetrics",
                issuesSummary( issueMetrics.getIssueSummaryMetrics( Issue.VALIDITY, true ) ) ) );
        add( new ListView<Level>(
                "validityWaivedSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( issueMetrics, item.getModelObject(), Issue.VALIDITY, true ) ) );
            }
        } );
    }

    private void addCompleteness( final IssueMetrics issueMetrics ) {
        add( new Label(
                "completenessMetrics",
                issuesSummary( issueMetrics.getIssueSummaryMetrics( Issue.COMPLETENESS, false ) ) ) );
        add( new ListView<Level>(
                "completenessSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( issueMetrics, item.getModelObject(), Issue.COMPLETENESS, false ) ) );
            }
        } );
        add( new Label(
                "completenessWaivedMetrics",
                issuesSummary( issueMetrics.getIssueSummaryMetrics( Issue.COMPLETENESS, true ) ) ) );
        add( new ListView<Level>(
                "completenessWaivedSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( issueMetrics, item.getModelObject(), Issue.COMPLETENESS, true ) ) );
            }
        } );
    }

    private void addRobustness( final IssueMetrics issueMetrics ) {
        add( new Label( "robustnessMetrics", issuesSummary( issueMetrics.getIssueSummaryMetrics( Issue.ROBUSTNESS, false ) ) ) );
        add( new ListView<Level>(
                "robustnessSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( issueMetrics, item.getModelObject(), Issue.ROBUSTNESS, false ) ) );
            }
        } );
        add( new Label( "robustnessWaivedMetrics", issuesSummary( issueMetrics.getIssueSummaryMetrics( Issue.ROBUSTNESS, true ) ) ) );
        add( new ListView<Level>(
                "robustnessWaivedSeverity",
                levels()
        ) {
            @Override
            protected void populateItem( ListItem<Level> item ) {
                item.add( new Label(
                        "severityMetrics",
                        severityMetrics( issueMetrics, item.getModelObject(), Issue.ROBUSTNESS, true ) ) );
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

    private String issuesSummary( IssueMetrics.Metrics metrics ) {
        MessageFormat mf = new MessageFormat( "{0} {1} ({2,number,percent}) , {3}" );
        int kindsCount = metrics.getKindsCount();
        Object[] args = {
                metrics.getCount(),
                metrics.isWaived() ? "waived" : "unresolved",
                Math.max( metrics.getPercent(), metrics.getPercent() > 0 ? 0.01 : 0.0 ),
                kindsCount};
        return mf.format( args ) + ( kindsCount > 1 ? " kinds" : " kind" );
    }


    private String severityMetrics( IssueMetrics issueMetrics, final Level severity, String type, boolean waived ) {
        IssueMetrics.Metrics metrics = issueMetrics.getSeverityMetrics( severity, type, waived );
        MessageFormat mf = new MessageFormat( "{0} {1} ({2,number,percent})" );
        Object[] args = {
                metrics.getCount(),
                severity.getNegativeLabel().toLowerCase(),
                Math.max( metrics.getPercent(), metrics.getPercent() > 0 ? 0.01 : 0.0 )};
        return mf.format( args );
    }

}
