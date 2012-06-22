package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.pages.components.plan.IssuesSummaryTable;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Issues report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/5/11
 * Time: 7:50 AM
 */
public class IssuesPage extends AbstractChannelsBasicPage {

    public IssuesPage() {
        this( new PageParameters() );
    }

    public IssuesPage( PageParameters parameters ) {
        super( parameters );
    }

    public static PageParameters createParameters( String uri, int version ) {
        PageParameters result = new PageParameters();
        result.set( "plan", uri );
        result.set( "v", version );
        return result;
    }


    protected void addContent() {
        addIssuesSummary();
        addIssueMetrics();
    }

    @Override
    protected String getContentsCssClass() {
        return "issues-contents";
    }

    @Override
    protected String getPageName() {
        return "Issues";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.ISSUES;
    }

    private void addIssuesSummary() {
        getContainer().add( new IssuesSummaryTable( "issuesSummary" ) );
    }

    private void addIssueMetrics() {
        getContainer().add( new IssuesMetrics( "robustnessMetrics", Issue.ROBUSTNESS ) );
        getContainer().add( new IssuesMetrics( "completenessMetrics", Issue.COMPLETENESS ) );
        getContainer().add( new IssuesMetrics( "validityMetrics", Issue.VALIDITY ) );
    }

}
