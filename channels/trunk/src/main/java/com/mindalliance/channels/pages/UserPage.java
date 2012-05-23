/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.MessagePanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.pages.reports.issues.IssuesPage;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.services.FeedbackService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.UserMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.util.List;

/**
 * Channels' home page.
 */
public class UserPage extends AbstractChannelsWebPage {


    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserPage.class );

    /**
     * Minimium delay before a change message fades out.
     */
    public static final int MESSAGE_FADE_OUT_DELAY = 20;

    /**
     * The mail sender.
     */
    @SpringBean
    private MailSender mailSender;

    @SpringBean
    private RFIService rfiService;

    @SpringBean
    private SurveysDAO surveysDAO;

    @SpringBean
    private UserMessageService userMessageService;

    @SpringBean
    private FeedbackService feedbackService;

    /**
     * The big form -- used for attachments and segment imports only.
     */
    private IndicatorAwareForm form;
    /**
     * Ajax activity spinner.
     */
    private WebMarkupContainer spinner;
    private SocialPanel socialPanel;
    /**
     * Message container.
     */
    private WebMarkupContainer messageContainer;
    private String message;
    /**
     * Time at which a message appeared.
     */
    private long message_time = 0;
    private Label welcomeLabel;


    public UserPage() {
        this( new PageParameters() );
    }

    public UserPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {
        getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        addPageTitle();
        addForm();
        addSpinner();
        addChangeMessagePanel();
        addWelcome();
        addLoggedIn();
        addHelp();
        addFeedback();
        addPlanSelector();
        addPlanImage();
        addPlanName();
        addPlanClient();
        // addPlanMetrics();
        addReferences();
        addGotoLinks( getPlan(), getUser() );
        addSocial();
    }

    private void addPageTitle() {
        add( new Label( "sg-title",
                new Model<String>( "Channels - Information Sharing Planning" ) ) );

    }


    private void addForm() {
        form = new IndicatorAwareForm( "big-form" ) {
            @Override
            protected void onSubmit() {
                // Do nothing - everything is done via Ajax, even file uploads
                // System.out.println( "Form submitted" );
            }
        };
        form.add( new AbstractAjaxTimerBehavior( Duration.seconds( REFRESH_DELAY ) ) {
            @Override
            protected void onTimer( AjaxRequestTarget target ) {
                try {
                    doTimedUpdate( target );
                    addSpinner();
                    target.add( spinner );
                } catch ( Exception e ) {
                    LOG.error( "Failed to do timed update", e );
                    ErrorPage.emailException(
                            new Exception( "Timed update failed", e ),
                            mailSender,
                            getSupportCommunity(),
                            getUser()
                    );
                    redirectHere();
                }
            }
        } );
        form.setMultiPart( true );
        add( form );
    }

    private void redirectHere() {
        setResponsePage( UserPage.class, planParameters( getPlan() ) );
    }

    private void addSpinner() {
        spinner = new WebMarkupContainer( "spinner" );
        spinner.setOutputMarkupId( true );
        spinner.add( new AttributeModifier( "id", new Model<String>( "spinner" ) ) );
        form.addOrReplace( spinner );
    }

    private void addHelp() {
        Attachment help = getHelp();

        if ( help != null ) {
            ExternalLink helpLink = new ExternalLink( "help-link", help.getUrl() );
            form.add( helpLink );

        } else {
            BookmarkablePageLink<HelpPage> helpLink = new BookmarkablePageLink<HelpPage>( "help-link", HelpPage.class );
            helpLink.add( new AttributeModifier( "target", new Model<String>( "help" ) ) );
            helpLink.setPopupSettings( new PopupSettings(
                    PopupSettings.RESIZABLE |
                            PopupSettings.SCROLLBARS |
                            PopupSettings.MENU_BAR |
                            PopupSettings.TOOL_BAR ) );

            form.add( helpLink );
        }
    }


    private void addChangeMessagePanel() {
        messageContainer = new WebMarkupContainer( "message-container" );
        messageContainer.setOutputMarkupId( true );
        makeVisible( messageContainer, !getMessage().isEmpty() );
        form.addOrReplace( messageContainer );
        messageContainer.add( new MessagePanel( "message", new Model<String>( getMessage() ) ) );
        message_time = System.currentTimeMillis();
    }

    private String getMessage() {
        return message == null ? "" : message;
    }


    private void doTimedUpdate( AjaxRequestTarget target ) {
        getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        updateSocialPanel( target );
        fadeOutMessagePanel( target );
    }

    private void fadeOutMessagePanel( AjaxRequestTarget target ) {
        if ( !getMessage().isEmpty() ) {
            if ( ( System.currentTimeMillis() - message_time ) > ( MESSAGE_FADE_OUT_DELAY * 1000 ) ) {
                target.appendJavaScript( "$('div.change-message').fadeOut('slow');" );
                message = null;
            }
        } else {
            makeVisible( messageContainer, false );
        }
        target.add( messageContainer );
    }


    /**
     * Update social panel.
     *
     * @param target an ajax request target
     */
    public void updateSocialPanel( AjaxRequestTarget target ) {
        socialPanel.refresh( target, new Change( Change.Type.Unknown ) );
    }


    private void addWelcome() {
        welcomeLabel = new Label( "userName", getUser().getFullName() );
        welcomeLabel.setOutputMarkupId( true );
        form.addOrReplace( welcomeLabel );
    }

    private void addLoggedIn() {
        form.add( new Label( "user",
                getUser().getUsername() ) );
    }

    private void addFeedback() {
        form.add( new UserFeedbackPanel( "feedback", Feedback.PARTICIPATING ) );
    }

    private void addPlanSelector() {
        WebMarkupContainer planSelectorDiv = new WebMarkupContainer( "switch-plan" );
        form.add( planSelectorDiv );
        planSelectorDiv.add( new DropDownChoice<Plan>(
                "plan-sel",
                new PropertyModel<Plan>( this, "plan" ),
                new PropertyModel<List<? extends Plan>>( this, "plans" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        redirectHere();
                    }
                } ) );
        planSelectorDiv.setVisible( getPlans().size() > 1 );
    }

    public void setPlan( Plan plan ) {
        getCommander().userLeftPlan( getUser().getUsername() );
        super.setPlan( plan );
    }

    private void addPlanImage() {
        WebMarkupContainer image = new WebMarkupContainer( "planImage" );
        image.add( new AttributeModifier( "src", new Model<String>( getPlanImagePath() ) ) );
        form.add( image );
    }

    private void addPlanName() {
        form.add( new Label( "planName", getPlan().getName() ) );
    }

    private void addPlanClient() {
        form.add( new Label( "planClient", getPlan().getClient() ) );
    }

    private void addReferences() {
        List<Attachment> references = getReferences();
        WebMarkupContainer referencesContainer = new WebMarkupContainer( "referencesContainer" );
        form.add( referencesContainer );
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
                item.add( new AttributeModifier(
                        "title",
                        new Model<String>(
                                a.getType().getLabel() + " - " + a.getUrl()
                        ) ) );
            }
        };
        referencesContainer.add( attachmentList );
        referencesContainer.setVisible( !references.isEmpty() );
    }

    private Attachment getHelp() {
        return (Attachment) CollectionUtils.find(
                getPlan().getAttachments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Attachment) object ).isHelp();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
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

    private void addGotoLinks( Plan plan, ChannelsUser user ) {

        List<PlanParticipation> participations = getPlanParticipations( plan, user );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        // guidelines link
        BookmarkablePageLink<? extends WebPage> gotoGuidelinesLink =
                getGuidelinesLink( "gotoGuidelines", getQueryService(), getPlan(), user, true );
        Label gotoGuidelinesLabel = new Label( "guidelinesLabel", getGuidelinesReportLabel( user, plan ) );
        gotoGuidelinesLink.add( gotoGuidelinesLabel )
                .add( new AttributeModifier(
                        "title",
                        new Model<String>( getGotoGuidelinesDescription( user, plan ) ) ) );
        // info needs link
        BookmarkablePageLink<? extends WebPage> gotoInfoNeedsLink =
                getInfoNeedsLink( "gotoInfoNeeds", getQueryService(), getPlan(), user, true );
        Label gotoInfoNeedsLabel = new Label( "infoNeedsLabel", getInfoNeedsReportLabel( user, plan ) );
        gotoInfoNeedsLink.add( gotoInfoNeedsLabel )
                .add( new AttributeModifier(
                        "title",
                        new Model<String>( getGotoInfoNeedsDescription( user, plan ) ) ) );
        // Surveys
        BookmarkablePageLink<? extends WebPage> gotoRFIsLink =
                getRFIsLink( "gotoRFIs", getPlan(), true );
        Label gotoRFIsLabel = new Label( "rfisLabel", getRFIsLabel( user, plan ) );
        gotoRFIsLink.add( gotoRFIsLabel )
                .add( new AttributeModifier(
                        "title",
                        new Model<String>( getGotoRFIsDescription( user, plan ) ) ) );
        // Feedback
        BookmarkablePageLink<? extends WebPage> gotoFeedbackLink =
                getFeedbackLink( "gotoFeedback", getPlan(), true );
        Label gotoFeedbackLabel = new Label( "feedbackLabel", getFeedbackLabel( user, plan ) );
        gotoFeedbackLink.add( gotoFeedbackLabel )
                .add( new AttributeModifier(
                        "title",
                        new Model<String>( getGotoFeedbackDescription( user, plan ) ) ) );
        // plan editor link
        BookmarkablePageLink gotoModelLink = newTargetedLink( "gotoModel", "", PlanPage.class, null, plan );
        gotoModelLink.add( new AttributeModifier(
                "title",
                new Model<String>( getGotoModelDescription( user, plan ) ) ) );
        // gotos
        form.addOrReplace(
                // Goto admin
                new WebMarkupContainer( "admin" )
                        .add( newTargetedLink( "gotoAdmin", "", AdminPage.class, null, plan ) )
                        .setVisible( user.isAdmin() )
                        .setOutputMarkupId( true ),

                // Goto model
                new WebMarkupContainer( "model" )
                        .add( gotoModelLink )
                        .setVisible( planner || plan.isTemplate() )
                        .setOutputMarkupId( true ),

                /*               // Goto mapped procedures
                                new WebMarkupContainer( "procedures" )
                                        .add( newTargetedLink( "gotoProcedures", "", ProcedureMapPage.class, null, plan ) ).
                                                setVisible( planner || plan.isTemplate() ),
                */
                // Goto guidelines
                new WebMarkupContainer( "guidelines" )
                        .add( gotoGuidelinesLink )
                        .setVisible( planner || !participations.isEmpty() )
                        .setOutputMarkupId( true ),

                // Goto info needs
                new WebMarkupContainer( "infoNeeds" )
                        .add( gotoInfoNeedsLink )
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

                // Goto issues report
                new WebMarkupContainer( "issues" )
                        .add( AbstractChannelsWebPage.newTargetedLink(
                                "gotoIssues",
                                "",
                                IssuesPage.class,
                                IssuesPage.createParameters( uri, plan.getVersion() ),
                                null,
                                plan ) )
                        .setVisible( planner || plan.isTemplate() ) )
                .setOutputMarkupId( true );

    }

    private String getGuidelinesReportLabel( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "IS guidelines for all participants"
                : "My information sharing guidelines";
    }

    private String getGotoGuidelinesDescription( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "Set how users participate in the plan and view their information sharing guidelines."
                : "View all tasks and related communications assigned to me according to my participation in this plan.";
    }

    private String getInfoNeedsReportLabel( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "Information needs of all participants"
                : "My information needs";
    }

    private String getRFIsLabel( ChannelsUser user, Plan plan ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Planning surveys" );
        int lateCount = surveysDAO.countLate( plan, user, getQueryService(), getAnalyst() );
        if ( lateCount > 0 ) {
            sb.append( " (" )
                    .append( lateCount )
                    .append( " past deadline)" );
        }
        return sb.toString();
    }

    private String getFeedbackLabel( ChannelsUser user, Plan plan ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "Feedback and replies" );
        int count = userMessageService.countNewFeedbackReplies( plan, user );
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

    private String getGotoRFIsDescription( ChannelsUser user, Plan plan ) {
        QueryService queryService = getQueryService();
        Analyst analyst = getAnalyst();
        int activeCount = rfiService.listUserActiveRFIs( plan, user, queryService, analyst ).size();
        StringBuilder sb = new StringBuilder();
        sb
                .append( "I participate in " )
                .append( activeCount == 0 ? "no" : activeCount )
                .append( activeCount > 1 ? " surveys" : " survey" );
        if ( activeCount > 0 ) {
            int noAnswerCount = surveysDAO.countUnanswered( plan, user, queryService, analyst );
            int incompleteCount = surveysDAO.countIncomplete( plan, user, queryService, analyst );
            int partialCount = incompleteCount - noAnswerCount;
            int lateCount = surveysDAO.countLate( plan, user, queryService, getAnalyst() );
            sb
                    .append( " of which " )
                    .append( noAnswerCount == 0 ? "none" : noAnswerCount )
                    .append( noAnswerCount == 1 ? " is" : " are" )
                    .append( " unanswered and " )
                    .append( partialCount == 0 ? "none" : partialCount )
                    .append( partialCount == 1 ? " is" : " are" )
                    .append( " partially answered. " )
                    .append( lateCount == 0 ? "None" : lateCount )
                    .append( lateCount <= 1 ? " is" : " are" )
                    .append( " overdue." );
        } else {
            sb.append( "." );
        }
        return sb.toString();
    }

    private String getGotoFeedbackDescription( ChannelsUser user, Plan plan ) {
        int unresolvedCount = feedbackService.countUnresolvedFeedback( plan, user );
        int newReplyCount = userMessageService.countNewFeedbackReplies( plan, user );
        StringBuilder sb = new StringBuilder();
        if ( unresolvedCount == 0 ) {
            sb.append( "All feedback are resolved." );
        } else {
            sb.append( unresolvedCount )
                    .append( " feedback" )
            .append(  unresolvedCount > 1 ? " are " : " is " )
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
                ? "Build or modify the information sharing plan.\n" +
                " (Requires a standards-compliant browser such as Internet Explorer 8+ and Firefox 3+.)"
                : "View the information sharing plan.\n" +
                "  (Requires a standards-compliant browser such as Internet Explorer 8+ and Firefox 3+.)";
    }

    private void addSocial() {
        String[] tabsShown = {SocialPanel.CALENDAR, /*SocialPanel.SURVEYS, */SocialPanel.MESSAGES, SocialPanel.USER, SocialPanel.PARTICIPATION};
        socialPanel = new SocialPanel( "social", false, tabsShown, false );
        form.add( socialPanel );
    }

    private String getPlanImagePath() {
        Plan plan = getPlan();
        String path = imagingService.getSquareIconUrl( plan, plan );
        return path == null ? "images/plan.png" : path;
    }

    @Override
    public void changed( Change change ) {
        getCommander().clearTimeOut( getUser().getUsername() );
        if ( change.getMessage() != null ) {
            message = change.getMessage();
        }
        if ( change.isNone() ) {
        } else if ( change.isCommunicated() ) {
            // do something?
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated()
                && change.isForInstanceOf( Plan.class ) ) {
            if ( change.isForProperty( "user" ) ) {
                addWelcome();
                target.add( welcomeLabel );
            } else if ( change.isForProperty( "participation" ) ) {
                addGotoLinks( getPlan(), getUser() );
                target.add( form );
            }
        }
        String message = change.getMessage();
        if ( message != null ) {
            addChangeMessagePanel();
            target.add( messageContainer );
        } else if ( change.isCommunicated() ) {
            newMessage( target, change );
            refreshSocialPanel( target, change );
        }
        if ( change.getScript() != null ) {
            target.appendJavaScript( change.getScript() );
        }
    }

    /**
     * Have social panel create a new message.
     *
     * @param target an ajax request target
     * @param change a change referencing what the communication is about
     */
    private void newMessage( AjaxRequestTarget target, Change change ) {
        socialPanel.newMessage( target, change );
    }

    /**
     * Refresh social panel.
     *
     * @param target an ajax request target
     * @param change a change referencing what the communication is about
     */
    public void refreshSocialPanel( AjaxRequestTarget target, Change change ) {
        updateSocialPanel( target );
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
