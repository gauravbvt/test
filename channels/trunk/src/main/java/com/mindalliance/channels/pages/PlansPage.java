package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.messages.UserMessageService;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.pages.reports.issues.IssuesPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * All plans page.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/11/13
 * Time: 11:22 AM
 */
public class PlansPage extends AbstractChannelsBasicPage {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlansPage.class );

    @SpringBean
    private RFIService rfiService;

    @SpringBean(name = "surveysDao")
    private SurveysDAO surveysDAO;

    @SpringBean
    private UserMessageService userMessageService;

    @SpringBean
    private FeedbackService feedbackService;

    private SocialPanel socialPanel;
    private WebMarkupContainer gotoIconsContainer;

    public PlansPage() {
        this( new PageParameters() );
    }

    public PlansPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getHelpSectionId() {
        return "plans-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-plans-page";
    }


    @Override
    protected boolean isDomainPage() {
        return true;
    }


    @Override
    protected void addContent() {
        addPlanName();
        addPlanClient();
        addAttachments();
        addGotoLinks( getCommunityService(), getUser() );
        addSocial();
    }

    @Override
    public boolean isPlanContext() {
        return true;
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        addGotoLinks( getCommunityService(), getUser() );
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
        getContainer().add( new Label( "planName", getPlan().getVersionedName() ) );
    }

    private void addPlanClient() {
        getContainer().add( new Label( "planClient", getPlan().getClient() ) );
    }

    private void addAttachments() {
        WebMarkupContainer attachmentsLabel = new WebMarkupContainer( "attachmentsLabel" );
        attachmentsLabel.setOutputMarkupId( true );
        getContainer().addOrReplace( attachmentsLabel );
        boolean visible = !isAttachmentsReadOnly() || !getPlan().getAttachments().isEmpty();
        makeVisible( attachmentsLabel, visible );
        AttachmentPanel attachmentPanel = new AttachmentPanel(
                "attachments",
                new Model<ModelObject>( getPlan() ),
                isAttachmentsReadOnly() );
        makeVisible( attachmentPanel, visible );
        getContainer().addOrReplace( attachmentPanel );
    }

    private boolean isAttachmentsReadOnly() {
        return !isPlanner();
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

    private void addGotoLinks( CommunityService communityService, ChannelsUser user ) {
        Plan plan = communityService.getPlan();
        String uri = plan.getUri();
        gotoIconsContainer = new WebMarkupContainer( "goto-icons" );
        gotoIconsContainer.setOutputMarkupId( true );
        getContainer().addOrReplace( gotoIconsContainer );

        // Surveys
        BookmarkablePageLink<? extends WebPage> gotoRFIsLink =
                getRFIsLink( "gotoRFIs", getPlan(), true );
        Label gotoRFIsLabel = new Label( "rfisLabel", getRFIsLabel( user, getCommunityService() ) );
        if ( isInCommunityContext() ) {
            addInCommunityContextParameter( gotoRFIsLink, getCommunityInContext() );
        }
        addTipTitle( gotoRFIsLabel,
                new Model<String>( getGotoRFIsDescription( user, communityService ) ) );
        gotoRFIsLink.add( gotoRFIsLabel );

        // Feedback
        BookmarkablePageLink<? extends WebPage> gotoFeedbackLink =
                getFeedbackLink( "gotoFeedback", getPlan(), true );
        Label gotoFeedbackLabel = new Label( "feedbackLabel", getFeedbackLabel( user, communityService ) );
        if ( isInCommunityContext() ) {
            addInCommunityContextParameter( gotoFeedbackLink, getCommunityInContext() );
        }
        addTipTitle(
                gotoFeedbackLabel,
                new Model<String>( getGotoFeedbackDescription( user, communityService ) )
        );
        gotoFeedbackLink.add( gotoFeedbackLabel );

        // Model editor link
        BookmarkablePageLink gotoModelLink = newTargetedLink( "gotoModel", "", PlanPage.class, null, plan );
        if ( isInCommunityContext() ) {
            addInCommunityContextParameter( gotoModelLink, getCommunityInContext() );
        }
        addTipTitle( gotoModelLink,
                new Model<String>( getGotoModelDescription( user, plan ) )
        );
        //Issues
       BookmarkablePageLink gotoIssuesLink = newTargetedLink(
                "gotoIssues",
                "",
                IssuesPage.class,
                IssuesPage.createParameters( uri, plan.getVersion() ),
                null,
                plan );
        if ( isInCommunityContext() ) {
            addInCommunityContextParameter( gotoIssuesLink, getCommunityInContext() );
        }
        addTipTitle(
                gotoIssuesLink,
                "View a summary of all issues automatically found by Channels or reported by planners" );

        // gotos
        gotoIconsContainer.add(
                // Goto model
                new WebMarkupContainer( "model" )
                        .add( gotoModelLink )
                        .setVisible( user.hasAccessTo( plan.getUri() ) || plan.isVisibleToUsers() )
                        .setOutputMarkupId( true ),

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
                        .setVisible( user.hasAccessTo( plan.getUri() ) || plan.isViewableByAll() )
                        .setOutputMarkupId( true )
        );

    }


    private String getRFIsLabel( ChannelsUser user, CommunityService communityService ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Planning surveys" );
        int lateCount = surveysDAO.countLate( communityService, user );
        if ( lateCount > 0 ) {
            sb.append( " (" )
                    .append( lateCount )
                    .append( " past deadline)" );
        }
        return sb.toString();
    }

    private String getFeedbackLabel( ChannelsUser user, CommunityService communityService ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Feedback and replies" );
        int count = userMessageService.countNewFeedbackReplies( communityService, user );
        if ( count > 0 ) {
            sb.append( " (" )
                    .append( count )
                    .append( " new " )
                    .append( count > 1 ? "replies" : "reply" )
                    .append( ")" );
        }
        return sb.toString();
    }

    private String getGotoRFIsDescription( ChannelsUser user, CommunityService communityService ) {
        int activeCount = rfiService.listUserActiveRFIs( communityService, user ).size();
        StringBuilder sb = new StringBuilder();
        sb
                .append( "I participate in " )
                .append( activeCount == 0 ? "no" : activeCount )
                .append( activeCount > 1 ? " surveys" : " survey" );
        if ( activeCount > 0 ) {
            int noAnswerCount = surveysDAO.countUnanswered( communityService, user );
            // int incompleteCount = surveysDAO.countIncomplete( plan, user, queryService, analyst );
            int partialCount = activeCount - noAnswerCount;
            int lateCount = surveysDAO.countLate( communityService, user );
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

    private String getGotoFeedbackDescription( ChannelsUser user, CommunityService communityService ) {
        int unresolvedCount = feedbackService.countUnresolvedFeedback( communityService, user );
        int newReplyCount = userMessageService.countNewFeedbackReplies( communityService, user );
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
        return user.isPlannerOrAdmin( plan.getUri() ) && getPlan().isDevelopment()
                ? "Build or modify the domain collaboration plan.\n" +
                " (Requires a modern, standards-compliant browser (Internet Explorer 8 or earlier is not supported)"
                : "View the collaboration plan.\n" +
                "  (Requires a modern, standards-compliant browser (Internet Explorer 8 or earlier is not supported)";
    }

    private void addSocial() {
        String[] tabsShown = {/*SocialPanel.CALENDAR, */SocialPanel.MESSAGES, SocialPanel.PARTICIPATION};
        socialPanel = new SocialPanel( "social", false, tabsShown, false );
        getContainer().add( socialPanel );
    }

/*
    private String getPlanImagePath() {
        Plan plan = getPlan();
        String path = imagingService.getSquareIconUrl( plan, plan );
        return path == null ? "images/plan.png" : path;
    }
*/

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
    protected String getDefaultUserRoleId() {
        return "planner";
    }

    @Override
    protected String getContentsCssClass() {
        return "home-contents";
    }

    @Override
    public String getPageName() {
        return "";
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
