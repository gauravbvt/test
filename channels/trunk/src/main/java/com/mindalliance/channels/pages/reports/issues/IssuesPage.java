package com.mindalliance.channels.pages.reports.issues;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.engine.analysis.IssueMetrics;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.pages.components.plan.IssuesSummaryTable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

    private boolean allExpanded;
    private AjaxLink<String> expandCollapseAllLink;


    public IssuesPage() {
        this( new PageParameters() );
    }

    public IssuesPage( PageParameters parameters ) {
        super( parameters );
    }

    public static PageParameters createParameters( CommunityService communityService ) {
        PageParameters result = new PageParameters();
        if ( communityService.isForDomain() ) {
            CollaborationModel collaborationModel = communityService.getPlan();
            result.set( MODEL_PARM, collaborationModel.getUri() );
            result.set( VERSION_PARM, collaborationModel.getVersion() );
        } else {
            PlanCommunity planCommunity = communityService.getPlanCommunity();
            result.set( COMMUNITY_PARM, planCommunity.getUri() );
        }
        return result;
    }

    @Override
    protected String getDefaultUserRoleId() {
        return getPlanCommunity().isModelCommunity() ? "developer" : "participant";
    }

    @Override
    protected String getHelpSectionId() {
        return getPlanCommunity().isModelCommunity() ? "model-issues-page" : "community-issues-page";
    }

    @Override
    protected String getHelpTopicId() {
        return getPlanCommunity().isModelCommunity() ?  "about-model-issues-page" : "about-community-issues-page";
    }

    protected void addContent() {
        addExpandCollapseAll();
        addTitle();
        IssueMetrics issueMetrics = new IssueMetrics( getCommunityService() );
        addIssuesSummary( issueMetrics );
        addIssueMetrics( issueMetrics );
    }

    private void addExpandCollapseAll() {
        expandCollapseAllLink = new AjaxLink<String>( "expandCollapseAll") {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                allExpanded = !allExpanded;
                addContent();
                target.add( getContainer() );
            }
        };
        Label collapseExpandLabel = new Label( "expandOrCollapse", allExpanded ? "Collapse all" : "Expand all" );
        expandCollapseAllLink.add( collapseExpandLabel );
        expandCollapseAllLink.setOutputMarkupId( true );
        getContainer().addOrReplace( expandCollapseAllLink );
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

    private void addTitle() {
        CommunityService communityService = getCommunityService();
        String context = communityService.isForDomain()
                ? "collaboration model " + communityService.getPlan().getVersionedName()
                : "collaboration community " + communityService.getPlanCommunity().getName();
        Label title = new Label("context", context );
        title.setOutputMarkupId( true );
        getContainer().addOrReplace( title );
    }

    private void addIssuesSummary( IssueMetrics issueMetrics ) {
        getContainer().addOrReplace( new IssuesSummaryTable( "issuesSummary", issueMetrics ) );
    }

    private void addIssueMetrics( IssueMetrics issueMetrics ) {
        getContainer().addOrReplace( new IssuesMetricsPanel( "robustnessMetrics", Issue.ROBUSTNESS, issueMetrics, allExpanded ) );
        getContainer().addOrReplace( new IssuesMetricsPanel( "completenessMetrics", Issue.COMPLETENESS, issueMetrics, allExpanded ) );
        getContainer().addOrReplace( new IssuesMetricsPanel( "validityMetrics", Issue.VALIDITY, issueMetrics, allExpanded ) );
    }

}
