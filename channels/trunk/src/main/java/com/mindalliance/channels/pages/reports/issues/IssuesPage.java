package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.core.community.feedback.Feedback;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.components.plan.IssuesSummaryTable;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Issues report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/5/11
 * Time: 7:50 AM
 */
public class IssuesPage extends AbstractChannelsWebPage {

    public IssuesPage() {
        this( new PageParameters() );
    }

    public IssuesPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    public static PageParameters createParameters( String uri, int version ) {
        PageParameters result = new PageParameters();
        result.set( "plan", uri );
        result.set( "v", version );
        return result;
    }


    private void init() {
        addHeading();
        addIssuesSummary();
        addIssueMetrics();
    }

    private void addHeading() {
        add( new Label( "planName", getPlan().getName() ) );
        add( new Label( "planVersion", "v" + getPlan().getVersion() ) );
        add( new UserFeedbackPanel(
                "feedback",
                getPlan(),
                "Send feedback",
                Feedback.ISSUES ) );
        add( new Label( "planDescription", getPlan().getDescription() ) );
    }

    private void addIssuesSummary() {
        add(  new IssuesSummaryTable( "issuesSummary" ) );
    }

    private void addIssueMetrics() {
        add(  new IssuesMetrics( "robustnessMetrics", Issue.ROBUSTNESS ) );
        add(  new IssuesMetrics( "completenessMetrics", Issue.COMPLETENESS ) );
        add(  new IssuesMetrics( "validityMetrics", Issue.VALIDITY ) );
    }

}
