package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationAnalyst;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.messages.UserMessageService;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.ChannelsModalWindow;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.community.CommunityDetailsPanel;
import com.mindalliance.channels.pages.components.community.CommunityStatusPanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan community page.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/11/13
 * Time: 2:59 PM
 */
public class CommunityPage extends AbstractChannelsBasicPage {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( CommunityPage.class );


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

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private PlanCommunityManager planCommunityManager;

    private SocialPanel socialPanel;
    private WebMarkupContainer gotoIconsContainer;
    private ModalWindow detailsDialog;
    private WebMarkupContainer detailsContainer;

    public CommunityPage() {
        this( new PageParameters() );
    }

    public CommunityPage( PageParameters parameters ) {
        super( parameters );
        if ( getPlanCommunity().isClosed() && !getCommunityService().isCommunityPlanner( getUser() ) ) {
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_UNAUTHORIZED, "Not authorized" );
        }
    }

    @Override
    protected String getHelpSectionId() {
        return "community-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-community-page";
    }


    @Override
    protected void addContent() {
        addCommunityDetails();
        addCommunityDetailsDialog();
        addAttachments();
        addGotoLinks( getCommunityService(), getUser() );
        addSocial();
    }

    private void addCommunityDetailsDialog() {
        detailsDialog = new ChannelsModalWindow( "detailsDialog" );
        detailsDialog.setTitle( "Community details" );
        detailsDialog.setCookieName( "channels-community-details" );
        detailsDialog.setWindowClosedCallback( new ModalWindow.WindowClosedCallback() {
            public void onClose( AjaxRequestTarget target ) {
                addCommunityDetails();
                target.add( detailsContainer );
                detailsDialog = null;
            }
        } );
        CommunityDetailsPanel communityEditPanel = new CommunityDetailsPanel(
                detailsDialog.getContentId(),
                new PropertyModel<PlanCommunity>( this, "planCommunity" )
        );
        detailsDialog.setContent( communityEditPanel );
        getContainer().addOrReplace( detailsDialog );
    }

    private void addCommunityDetails() {
        detailsContainer = new WebMarkupContainer( "details" );
        detailsContainer.setOutputMarkupId( true );
        getContainer().addOrReplace( detailsContainer );
        addDetailLabels();
        addEditButton();
        addStatusPanel();
        addUpdateVersionButton();
    }

    private void addDetailLabels() {
        addCommunityName();
        addPlanVersion();
        addCommunityDescription();
        addCommunityLocale();
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


    private void addCommunityName() {
        Label label = new Label( "name", getPlanCommunity().getName() );
        label.setOutputMarkupId( true );
        detailsContainer.addOrReplace( label );
    }

    private void addCommunityDescription() {
        Label label = new Label( "description", getPlanCommunity().getDescription() );
        label.setOutputMarkupId( true );
        detailsContainer.addOrReplace( label );
    }

    private void addCommunityLocale() {
        Place locale = getPlanCommunity().getLocale( getCommunityService() );
        String localeName = locale == null ? "Anywhere" : "In " + locale.getName();
        Label label = new Label( "locale", localeName );
        label.setOutputMarkupId( true );
        detailsContainer.addOrReplace( label );
    }

    private void addPlanVersion() {
        Label label = new Label( "planVersion", "Based on " + getPlan().getSimpleVersionedName() );
        label.setOutputMarkupId( true );
        detailsContainer.addOrReplace( label );

    }

    @Override
    public Plan getPlan() {
        return getCommunityService().getPlan();
    }

    private void addEditButton() {
        AjaxLink<String> editButton = new AjaxLink<String>( "edit" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                addCommunityDetailsDialog();
                detailsDialog.show( target );
            }
        };
        editButton.setVisible( isCommunityPlanner() );
        addTipTitle( editButton, "Edit the name, description and locale of the community" );
        detailsContainer.add( editButton );
    }

    private void addUpdateVersionButton() {
        String planUri = getPlanCommunity().getPlanUri();
        int planVersion = getPlanCommunity().getPlanVersion();
        final int latestProdVersion = planManager.findProductionPlan( planUri ).getVersion();
        ConfirmedAjaxFallbackLink<String> updateVersionButton = new ConfirmedAjaxFallbackLink<String>(
                "updateVersion",
                "Are you sure you want to upgrade to the latest production version of the plan?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                try {
                    planCommunityManager.updateToPlanVersion( getPlanCommunity(), latestProdVersion );
                    setResponsePage( CommunityPage.class, getPageParameters() );
                } catch ( Exception e ) {
                    updateWith(
                            target,
                            Change.message( "Update failed." ),
                            new ArrayList<Updatable>() );
                }
            }
        };
        updateVersionButton.setOutputMarkupId( true );
        Label updateVersionLabel = new Label( "updateVersionLabel", "Update to version " + latestProdVersion );
        updateVersionButton.add( updateVersionLabel );
        makeVisible( updateVersionButton, planVersion != latestProdVersion && isCommunityPlanner() );
        detailsContainer.addOrReplace( updateVersionButton );
    }

    private boolean isCommunityPlanner() {
        return getCommunityService().isCommunityPlanner( getUser() );
    }

    private void addAttachments() {
        WebMarkupContainer attachmentsLabel = new WebMarkupContainer( "attachmentsLabel" );
        attachmentsLabel.setOutputMarkupId( true );
        getContainer().addOrReplace( attachmentsLabel );
        boolean visible = !isAttachmentsReadOnly() || !getPlanCommunity().getAttachments().isEmpty();
        makeVisible( attachmentsLabel, visible );
        AttachmentPanel attachmentPanel = new AttachmentPanel(
                "attachments",
                new Model<ModelObject>( getPlanCommunity() ),
                isAttachmentsReadOnly() );
        makeVisible( attachmentPanel, visible );
        getContainer().addOrReplace( attachmentPanel );
    }

    private boolean isAttachmentsReadOnly() {
        return !isCommunityPlanner();
    }

    private void addStatusPanel() {
        CommunityStatusPanel statusPanel = new CommunityStatusPanel( "status" );
        statusPanel.setVisible( getCommunityService().isCommunityPlanner( getUser() ) );
        detailsContainer.add( statusPanel );
    }

    private void addGotoLinks( CommunityService communityService, ChannelsUser user ) {
        Plan plan = communityService.getPlan();
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        List<UserParticipation> participations = getUserParticipations( planCommunity, user );
        String planUri = plan.getUri();
        boolean planner = user.isPlanner( planUri );
        boolean communityLeader = communityService.isCommunityPlanner( user );
        gotoIconsContainer = new WebMarkupContainer( "goto-icons" );
        gotoIconsContainer.setOutputMarkupId( true );
        getContainer().addOrReplace( gotoIconsContainer );

        // Protocols link
        BookmarkablePageLink<? extends WebPage> gotoProtocolsLink =
                getProtocolsLink( "gotoProtocols", getPlanCommunity(), user, true );
        addTipTitle( gotoProtocolsLink, new Model<String>( getGotoProtocolsDescription( user, plan ) ) );
        // Surveys
        BookmarkablePageLink<? extends WebPage> gotoRFIsLink =
                getRFIsLink( "gotoRFIs", getPlanCommunity(), true );
        Label gotoRFIsLabel = new Label( "rfisLabel", getRFIsLabel( user, getDomainCommunityService() ) );
        addTipTitle( gotoRFIsLabel,
                new Model<String>( getGotoRFIsDescription( user, getDomainCommunityService() ) ) );
        gotoRFIsLink.add( gotoRFIsLabel );

        // Feedback
        BookmarkablePageLink<? extends WebPage> gotoFeedbackLink =
                getFeedbackLink( "gotoFeedback", getPlanCommunity(), true );
        Label gotoFeedbackLabel = new Label( "feedbackLabel", getFeedbackLabel( user, getDomainCommunityService() ) );
        addTipTitle(
                gotoFeedbackLabel,
                new Model<String>( getGotoFeedbackDescription( user, getDomainCommunityService() ) )
        );
        gotoFeedbackLink.add( gotoFeedbackLabel );

        // Model editor link
        BookmarkablePageLink gotoModelLink = newTargetedLink( "gotoModel", "", PlansPage.class, null, plan );
        addInCommunityContextParameter( gotoModelLink, getPlanCommunity() );
        addTipTitle( gotoModelLink,
                new Model<String>( getGotoModelDescription( user, plan ) )
        );

        // Participation
        BookmarkablePageLink participationManagerLink = newTargetedLink(
                "gotoParticipationManager",
                "",
                ParticipationManagerPage.class,
                null,
                planCommunity );
        int toConfirmCount = userParticipationService
                .listUserParticipationsAwaitingConfirmationBy( getUser(), getCommunityService() ).size();
        int issueCount = participationAnalyst.detectAllIssues( getCommunityService() ).size();
        addTipTitle(
                participationManagerLink,
                "Manage plan participation ("
                        + toConfirmCount + " to confirm, "
                        + issueCount + ( issueCount > 1 ? " issues" : " issue" )
                        + ")" );

        BookmarkablePageLink gotoRequirementsLink = getRequirementsLink(
                "gotoRequirements",
                getPlanCommunity(),
                true );
        addTipTitle(
                gotoRequirementsLink,
                "View the collaboration requirements" );


        // gotos
        gotoIconsContainer.add(
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

                // Goto surveys
                new WebMarkupContainer( "rfis" )
                        .add( gotoRFIsLink )
                        .setOutputMarkupId( true ),

                // Goto feedback
                new WebMarkupContainer( "allFeedback" )
                        .add( gotoFeedbackLink )
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
                ? "Set how users participate in the plan and view their collaboration checklists."
                : "View all tasks and related communications assigned to me according to my participation in this plan.";
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
        return user.isPlanner( plan.getUri() ) && getPlan().isDevelopment()
                ? "Build or modify the " + plan.getName() + " plan.\n" +
                " (Requires a modern, standards-compliant browser (Internet Explorer 8 or earlier is not supported)"
                : "View the " + plan.getName() + " plan.\n" +
                "  (Requires a modern, standards-compliant browser (Internet Explorer 8 or earlier is not supported)";
    }

    private void addSocial() {
        String[] tabsShown = {/*SocialPanel.CALENDAR,*/ SocialPanel.MESSAGES, SocialPanel.PARTICIPATION};
        socialPanel = new SocialPanel( "social", false, tabsShown, false );
        getContainer().add( socialPanel );
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
    protected String getDefaultUserRoleId() {
        return "participant";
    }

    @Override
    protected String getContentsCssClass() {
        return "home-contents";
    }

    @Override
    public String getPageName() {
        return ""; // don;t show page name in breadcrumbs
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

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( PlanCommunity.class ) ) {
            if ( change.isCollapsed() || change.isUpdated() ) {
                if ( detailsDialog != null ) {
                    detailsDialog.close( target );
                    detailsDialog = null;
                }
                addDetailLabels();
                target.add( detailsContainer );
            }
            if ( change.isForProperty( "participation" ) ) {
                updateContent( target );
                target.add( getForm() );
            }
            if ( change.isUpdated() ) {
                super.updateWith( target, change, updated );
            }
        } else {
            super.updateWith( target, change, updated );
        }
    }
}
