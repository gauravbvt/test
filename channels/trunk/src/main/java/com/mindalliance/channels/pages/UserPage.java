/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.MessagePanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.pages.reports.issues.IssuesPage;
import com.mindalliance.channels.social.model.Feedback;
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
        spinner.add( new AttributeModifier( "id", true, new Model<String>( "spinner" ) ) );
        form.addOrReplace( spinner );
    }

    private void addHelp() {
        Attachment help = getHelp();

        if ( help != null ) {
            ExternalLink helpLink = new ExternalLink( "help-link", help.getUrl() );
            form.add( helpLink );

        } else {
            BookmarkablePageLink<HelpPage> helpLink = new BookmarkablePageLink<HelpPage>( "help-link", HelpPage.class );
            helpLink.add( new AttributeModifier( "target", true, new Model<String>( "help" ) ) );
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
                documentLink.add( new AttributeModifier( "target", true, new Model<String>( "_" ) ) );
                item.add( documentLink );
                item.add( new AttributeModifier(
                        "title",
                        true,
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

        Actor actor = findActor( getQueryService(), user.getUsername() );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        // guidelines link
        BookmarkablePageLink<? extends WebPage> gotoGuidelinesLink =
                getGuidelinesLink( "gotoGuidelines", getQueryService(), getPlan(), user, true );
        Label gotoGuidelinesLabel = new Label( "guidelinesLabel", getGuidelinesReportLabel( user, plan ) );
        gotoGuidelinesLink.add( gotoGuidelinesLabel )
                .add(new AttributeModifier(
                "title",
                new Model<String>( getGotoGuidelinesDescription( user, plan ) ) ));
        // info needs link
        BookmarkablePageLink<? extends WebPage> gotoInfoNeedsLink =
                getInfoNeedsLink( "gotoInfoNeeds", getQueryService(), getPlan(), user, true );
        Label gotoInfoNeedsLabel = new Label( "infoNeedsLabel", getInfoNeedsReportLabel( user, plan ) );
        gotoInfoNeedsLink.add( gotoInfoNeedsLabel )
                .add(new AttributeModifier(
                "title",
                new Model<String>( getGotoInfoNeedsDescription( user, plan ) ) ));
        // plan editor link
        BookmarkablePageLink gotoModelLink = newTargetedLink( "gotoModel", "", PlanPage.class, null, plan );
        gotoModelLink.add( new AttributeModifier(
                "title",
                new Model<String>( getGotoModelDescription( user, plan ) ) ) );
        // gotos
        form.add(
                // Goto admin
                new WebMarkupContainer( "admin" )
                        .add( newTargetedLink( "gotoAdmin", "", AdminPage.class, null, plan ) )
                        .setVisible( user.isAdmin() ),

                // Goto model
                new WebMarkupContainer( "model" )
                        .add( gotoModelLink )
                        .setVisible( planner || plan.isTemplate() ),

 /*               // Goto mapped procedures
                new WebMarkupContainer( "procedures" )
                        .add( newTargetedLink( "gotoProcedures", "", ProcedureMapPage.class, null, plan ) ).
                                setVisible( planner || plan.isTemplate() ),
*/
                // Goto guidelines
                new WebMarkupContainer( "guidelines" )
                        .add( gotoGuidelinesLink )
                        .setVisible( planner || actor != null ),

                // Goto info needs
                new WebMarkupContainer( "infoNeeds" )
                        .add( gotoInfoNeedsLink )
                        .setVisible( planner || actor != null ),

                // Goto issues report
                new WebMarkupContainer( "issues" )
                        .add( AbstractChannelsWebPage.newTargetedLink(
                                "gotoIssues",
                                "",
                                IssuesPage.class,
                                IssuesPage.createParameters( uri, plan.getVersion() ),
                                null,
                                plan ) )
                        .setVisible( planner || plan.isTemplate() ) );

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

    private String getGotoInfoNeedsDescription( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "View the information needs of any participant or agent in this plan."
                : "View my information needs and their status in this plan.";
    }

    private String getGotoModelDescription( ChannelsUser user, Plan plan ) {
        return user.isPlanner( plan.getUri() ) && getPlan().isDevelopment()
                ? "Build or modify the information sharing plan.\n" +
                " (Requires a standards-compliant browser such as Internet Explorer 8+ and Firefox 3+.)"
                : "View the information sharing plan.\n" +
                "  (Requires a standards-compliant browser such as Internet Explorer 8+ and Firefox 3+.)";
    }

    private static Actor findActor( QueryService queryService, String userName ) {
        Participation participation = queryService.findParticipation( userName );
        return participation != null && participation.getActor() != null
                ? participation.getActor()
                : null;
    }

    private void addSocial() {
        String[] tabsShown = {SocialPanel.CALENDAR, /*SocialPanel.SURVEYS, */SocialPanel.MESSAGES, SocialPanel.USER};
        socialPanel = new SocialPanel( "social", false, tabsShown );
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
                && change.isForInstanceOf( Plan.class )
                && change.isForProperty( "user" ) ) {
            addWelcome();
            target.add( welcomeLabel );
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
