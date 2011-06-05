package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.components.plan.IssuesSummaryTable;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

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
        result.put( "plan", uri );
        result.put( "v", version );
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
                "Send feedback" ) );
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
