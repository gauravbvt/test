/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.ParticipationAnalyst;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.pages.reports.issues.IssuesPage;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.services.FeedbackService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.UserMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.util.List;

/**
 * Channels' home page.
 */
public class UserPage extends AbstractChannelsBasicPage {


    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserPage.class );

    /**
     * The mail sender.
     */
    @SpringBean
    private MailSender mailSender;

    @SpringBean
    private RFIService rfiService;

    @SpringBean(name = "surveysDao")
    private SurveysDAO surveysDAO;

    @SpringBean
    private UserMessageService userMessageService;

    @SpringBean
    private FeedbackService feedbackService;

    @SpringBean
    private UserParticipationService userParticipationService;

    @SpringBean
    private ParticipationAnalyst participationAnalyst;

    private SocialPanel socialPanel;
    private WebMarkupContainer gotoIconsContainer;

    public UserPage() {
        this( new PageParameters() );
    }

    public UserPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected void addContent() {
        addPlanName();
        addPlanClient();
        addReferences();
        addGotoLinks( getPlanCommunity(), getUser() );
        addSocial();
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        addGotoLinks( getPlanCommunity(), getUser() );
        target.add( gotoIconsContainer );
        updateSocialPanel( target );
    }

    /**
     * Update social panel.
     *
     * @param target an ajax request target
     */
    public void updateSocialPanel( AjaxRequestTarget target ) {
        socialPanel.refresh( target, new Change( Change.Type.Unknown ) );
    }


    private void addPlanName() {
        getContainer().add( new Label( "planName", getPlan().getName() ) );
    }

    private void addPlanClient() {
        getContainer().add( new Label( "planClient", getPlan().getClient() ) );
    }

    private void addReferences() {
        List<Attachment> references = getReferences();
        WebMarkupContainer referencesContainer = new WebMarkupContainer( "referencesContainer" );
        getContainer().add( referencesContainer );
        ListView<Attachment> attachmentList = new ListView<Attachment>(
                "references",
                references ) {

            @Override
            protected void populateItem( ListItem<Attachment> item ) {
                Attachment a = item.getModelObject();
                ExternalLink documentLink = new ExternalLink( "attachment",
                        a.getUrl(), getAttachmentManager().getLabel( getPlan(), a ) );
                documentLink.add( new AttributeModifier( "target", new Model<String>( "_" ) ) );
                item.add( documentLink );
                addTipTitle(
                        item,
                        new Model<String>(
                                a.getType().getLabel() + " - " + a.getUrl()
                        ) );
            }
        };
        referencesContainer.add( attachmentList );
        referencesContainer.setVisible( !references.isEmpty() );
    }

    @SuppressWarnings("unchecked")
    public List<Attachment> getReferences() {
        return (List<Attachment>) CollectionUtils.select(
                getPlan().getAttachments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Attachment) object ).isReference();
                    }
                }
        );
    }

    private void addGotoLinks( PlanCommunity planCommunity, ChannelsUser user ) {
        Plan plan = planCommunity.getPlan();
        List<UserParticipation> participations = getUserParticipations( planCommunity, user );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        boolean communityLeader = planCommunity.isCommunityLeader( user );
        gotoIconsContainer = new WebMarkupContainer( "goto-icons" );
        gotoIconsContainer.setOutputMarkupId( true );
        getContainer().addOrReplace( gotoIconsContainer );

        // Protocols link
        BookmarkablePageLink<? extends WebPage> gotoProtocolsLink =
                getProtocolsLink( "gotoProtocols", getPlanCommunity(), user, true );
        addTipTitle( gotoProtocolsLink, new Model<String>( getGotoProtocolsDescription( user, plan ) ) );
        // info needs link
/*
        BookmarkablePageLink<? extends WebPage> gotoInfoNeedsLink =
                getInfoNeedsLink( "gotoInfoNeeds", getQueryService(), getPlan(), user, true );
        gotoInfoNeedsLink
                .add( new AttributeModifier(
                        "title",
                        new Model<String>( getGotoInfoNeedsDescription( user, plan ) ) ) );
*/
        // Surveys
        BookmarkablePageLink<? extends WebPage> gotoRFIsLink =
                getRFIsLink( "gotoRFIs", getPlan(), true );
        Label gotoRFIsLabel = new Label( "rfisLabel", getRFIsLabel( user, planCommunity ) );
        addTipTitle( gotoRFIsLabel,
                new Model<String>( getGotoRFIsDescription( user, planCommunity ) ) );
        gotoRFIsLink.add( gotoRFIsLabel );

        // Feedback
        BookmarkablePageLink<? extends WebPage> gotoFeedbackLink =
                getFeedbackLink( "gotoFeedback", getPlan(), true );
        Label gotoFeedbackLabel = new Label( "feedbackLabel", getFeedbackLabel( user, planCommunity ) );
        addTipTitle(
                gotoFeedbackLabel,
                new Model<String>( getGotoFeedbackDescription( user, planCommunity ) )
        );
        gotoFeedbackLink.add( gotoFeedbackLabel );

        // Model editor link
        BookmarkablePageLink gotoModelLink = newTargetedLink( "gotoModel", "", PlanPage.class, null, plan );
        addTipTitle( gotoModelLink,
                new Model<String>( getGotoModelDescription( user, plan ) )
        );

        // Settings
        BookmarkablePageLink gotoAdminLink = newTargetedLink( "gotoAdmin", "", AdminPage.class, null, plan );
        addTipTitle(
                gotoAdminLink,
                "Configure Channels, add users, change access privileges, and create, configure, release, or delete plans" );

        BookmarkablePageLink participationManagerLink = newTargetedLink(
                "gotoParticipationManager",
                "",
                ParticipationManagerPage.class,
                null,
                plan );
        int toConfirmCount = userParticipationService
                .listUserParticipationsAwaitingConfirmationBy( getUser(), getPlanCommunity() ).size();
        int issueCount = participationAnalyst.detectAllIssues( getPlanCommunity() ).size();
        addTipTitle(
                participationManagerLink,
                "Manage plan participation ("
                        + toConfirmCount + " to confirm, "
                        + issueCount + ( issueCount > 1 ? " issues" : " issue" )
                        + ")" );

        BookmarkablePageLink gotoIssuesLink = newTargetedLink(
                "gotoIssues",
                "",
                IssuesPage.class,
                IssuesPage.createParameters( uri, plan.getVersion() ),
                null,
                plan );
        addTipTitle(
                gotoIssuesLink,
                "View a summary of all issues automatically found by Channels or reported by planners" );

        BookmarkablePageLink gotoRequirementsLink = getRequirementsLink(
                "gotoRequirements",
                getPlanCommunity(),
                true );
        addTipTitle(
                gotoRequirementsLink,
                "View the collaboration requirements" );


        // gotos
        gotoIconsContainer.add(
                // Goto admin
                new WebMarkupContainer( "admin" )
                        .add( gotoAdminLink )
                        .setVisible( user.isAdmin() )
                        .setOutputMarkupId( true ),

                // Goto participation manager
                new WebMarkupContainer( "participationManager" )
                        .add( participationManagerLink )
                        .setOutputMarkupId( true ),

                // Goto model
                new WebMarkupContainer( "model" )
                        .add( gotoModelLink )
                        .setVisible( planner || plan.isVisibleToUsers() )
                        .setOutputMarkupId( true ),

                // Goto protocols
                new WebMarkupContainer( "protocols" )
                        .add( gotoProtocolsLink )
                        .setVisible( planner || !participations.isEmpty() )
                        .setOutputMarkupId( true ),

                // Goto info needs
          /*      new WebMarkupContainer( "infoNeeds" )
                        .add( gotoInfoNeedsLink )
                        .setVisible( planner || !participations.isEmpty() )
                        .setOutputMarkupId( true ),*/

                // Goto surveys
                new WebMarkupContainer( "rfis" )
                        .add( gotoRFIsLink )
                        .setOutputMarkupId( true ),

                // Goto feedback
                new WebMarkupContainer( "allFeedback" )
                        .add( gotoFeedbackLink )
                        .setOutputMarkupId( true ),

                // Goto issues report
                new WebMarkupContainer( "issues" )
                        .add( gotoIssuesLink )
                        .setVisible( planner || plan.isViewableByAll() )
                        .setOutputMarkupId( true ),

                // Goto requirements
                new WebMarkupContainer( "requirements" )
                        .add( gotoRequirementsLink )
                        .setVisible( communityLeader )
                        .setOutputMarkupId( true )
        );

    }

    private String getGotoProtocolsDescription( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "Set how users participate in the plan and view their collaboration protocols."
                : "View all tasks and related communications assigned to me according to my participation in this plan.";
    }


    private String getRFIsLabel( ChannelsUser user, PlanCommunity planCommunity ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Planning surveys" );
        int lateCount = surveysDAO.countLate( planCommunity, user );
        if ( lateCount > 0 ) {
            sb.append( " (" )
                    .append( lateCount )
                    .append( " past deadline)" );
        }
        return sb.toString();
    }

    private String getFeedbackLabel( ChannelsUser user, PlanCommunity planCommunity ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Feedback and replies" );
        int count = userMessageService.countNewFeedbackReplies( planCommunity, user );
        if ( count > 0 ) {
            sb.append( " (" )
                    .append( count )
                    .append( " new " )
                    .append( count > 1 ? "replies" : "reply" )
                    .append( ")" );
        }
        return sb.toString();
    }


    private String getGotoInfoNeedsDescription( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "View the information needs of any participant or agent in this plan."
                : "View my information needs and their status in this plan.";
    }

    private String getGotoRFIsDescription( ChannelsUser user, PlanCommunity planCommunity ) {
        QueryService queryService = getQueryService();
        Analyst analyst = getAnalyst();
        int activeCount = rfiService.listUserActiveRFIs( planCommunity, user ).size();
        StringBuilder sb = new StringBuilder();
        sb
                .append( "I participate in " )
                .append( activeCount == 0 ? "no" : activeCount )
                .append( activeCount > 1 ? " surveys" : " survey" );
        if ( activeCount > 0 ) {
            int noAnswerCount = surveysDAO.countUnanswered( planCommunity, user );
            // int incompleteCount = surveysDAO.countIncomplete( plan, user, queryService, analyst );
            int partialCount = activeCount - noAnswerCount;
            int lateCount = surveysDAO.countLate( planCommunity, user );
            sb
                    .append( " of which " )
                    .append( noAnswerCount == 0 ? "none" : noAnswerCount )
                    .append( noAnswerCount == 1 ? " is" : " are" )
                    .append( " unanswered" );
            if ( partialCount != 0 ) {
                sb.append( " and " )
                        .append( partialCount )
                        .append( partialCount == 1 ? " is" : " are" )
                        .append( " partially answered. " );
            } else {
                sb.append( ". " );
            }
            sb.append( lateCount == 0 ? "None" : lateCount )
                    .append( lateCount <= 1 ? " is" : " are" )
                    .append( " overdue." );
        } else {
            sb.append( "." );
        }
        return sb.toString();
    }

    private String getGotoFeedbackDescription( ChannelsUser user, PlanCommunity planCommunity ) {
        int unresolvedCount = feedbackService.countUnresolvedFeedback( planCommunity, user );
        int newReplyCount = userMessageService.countNewFeedbackReplies( planCommunity, user );
        StringBuilder sb = new StringBuilder();
        if ( unresolvedCount == 0 ) {
            sb.append( "All feedback are resolved." );
        } else {
            sb.append( unresolvedCount )
                    .append( " feedback" )
                    .append( unresolvedCount > 1 ? " are " : " is " )
                    .append( "unresolved." );
        }
        if ( newReplyCount > 0 ) {
            sb.append( " You have" )
                    .append( newReplyCount )
                    .append( " new " )
                    .append( newReplyCount > 1 ? "replies." : "reply." );
        }
        return sb.toString();
    }

    private String getGotoModelDescription( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() ) && getPlan().isDevelopment()
                ? "Build or modify the collaboration plan.\n" +
                " (Requires a modern, standards-compliant browser (Internet Explorer 8 or earlier is not supported)"
                : "View the collaboration plan.\n" +
                "  (Requires a modern, standards-compliant browser (Internet Explorer 8 or earlier is not supported)";
    }

    private void addSocial() {
        String[] tabsShown = {SocialPanel.CALENDAR, /*SocialPanel.SURVEYS, */SocialPanel.MESSAGES, SocialPanel.USER, SocialPanel.PARTICIPATION};
        socialPanel = new SocialPanel( "social", false, tabsShown, false );
        getContainer().add( socialPanel );
    }

    private String getPlanImagePath() {
        Plan plan = getPlan();
        String path = imagingService.getSquareIconUrl( plan, plan );
        return path == null ? "images/plan.png" : path;
    }

    /**
     * Have social panel create a new message.
     *
     * @param target an ajax request target
     * @param change a change referencing what the communication is about
     */
    protected void newMessage( AjaxRequestTarget target, Change change ) {
        socialPanel.newMessage( target, change );
    }

    @Override
    protected String getContentsCssClass() {
        return "home-contents";
    }

    @Override
    protected String getPageName() {
        return "Home";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.CHANNELS;
    }

    @Override
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

