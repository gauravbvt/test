package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.engine.analysis.IssueMetrics;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.pages.components.plan.IssuesSummaryTable;
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
public class IssuesPage extends AbstractChannelsBasicPage {

    public IssuesPage() {
        this( new PageParameters() );
    }

    public IssuesPage( PageParameters parameters ) {
        super( parameters );
    }

    public static PageParameters createParameters( CommunityService communityService ) {
        PageParameters result = new PageParameters();
        if ( communityService.isForDomain() ) {
            Plan plan = communityService.getPlan();
            result.set( TEMPLATE_PARM, plan.getUri() );
            result.set( VERSION_PARM, plan.getVersion() );
        } else {
            PlanCommunity planCommunity = communityService.getPlanCommunity();
            result.set( COLLAB_PLAN_PARM, planCommunity.getUri() );
        }
        return result;
    }

    @Override
    protected String getHelpSectionId() {
        return "issues-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-issues-page";
    }

    protected void addContent() {
        addTitle();
        IssueMetrics issueMetrics = new IssueMetrics( getCommunityService() );
        addIssuesSummary( issueMetrics );
        addIssueMetrics( issueMetrics );
    }

    @Override
    protected String getContentsCssClass() {
        return "issues-contents";
    }

    @Override
    public String getPageName() {
        return "Issues";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.ISSUES;
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "planner";
    }

    private void addTitle() {
        CommunityService communityService = getCommunityService();
        String context = communityService.isForDomain()
                ? "template " + communityService.getPlan().getVersionedName()
                : "plan " + communityService.getPlanCommunity().getName();
        Label title = new Label("context", context );
        getContainer().add( title );
    }

    private void addIssuesSummary( IssueMetrics issueMetrics ) {
        getContainer().add( new IssuesSummaryTable( "issuesSummary", issueMetrics ) );
    }

    private void addIssueMetrics( IssueMetrics issueMetrics ) {
        getContainer().add( new IssuesMetricsPanel( "robustnessMetrics", Issue.ROBUSTNESS, issueMetrics ) );
        getContainer().add( new IssuesMetricsPanel( "completenessMetrics", Issue.COMPLETENESS, issueMetrics ) );
        getContainer().add( new IssuesMetricsPanel( "validityMetrics", Issue.VALIDITY, issueMetrics ) );
    }

}
