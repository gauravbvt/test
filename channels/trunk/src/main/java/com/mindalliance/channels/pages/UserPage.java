package com.mindalliance.channels.pages;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.MessagePanel;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.pages.reports.ProcedureMapPage;
import com.mindalliance.channels.pages.responders.ResponderPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.util.List;

/**
 * Channels' home page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/2/11
 * Time: 12:42 PM
 */
public class UserPage extends AbstractChannelsWebPage implements Updatable {


    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserPage.class );

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



    public UserPage() {
        this( new PageParameters() );
    }

    public UserPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {
        getCommander().keepAlive( User.current().getUsername(), REFRESH_DELAY );
        addPageTitle();
        addForm();
        addSpinner();
        addChangeMessagePanel();
        addWelcome();
        addLoggedIn();
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
                    target.addComponent( spinner );
                } catch ( Exception e ) {
                    LOG.error( "Failed to do timed update", e );
                    ErrorPage.emailException(
                            new Exception( "Timed update failed", e ),
                            mailSender,
                            getApp().getPlannerSupportCommunity()
                    );
                    redirectHere();
                }
            }
        } );
        form.setMultiPart( true );
        add( form );
    }

    private void redirectHere() {
        String url = redirectUrl( "home", getPlan() );
        RedirectPage page =  new RedirectPage( url );
        setResponsePage( page );
    }

    private void addSpinner() {
        spinner = new WebMarkupContainer( "spinner" );
        spinner.setOutputMarkupId( true );
        spinner.add( new AttributeModifier( "id", true, new Model<String>( "spinner" ) ) );
        form.addOrReplace( spinner );
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
        getCommander().keepAlive( User.current().getUsername(), REFRESH_DELAY );
        getCommander().processDeaths();
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


    private void addWelcome() {
        form.add( new Label( "userName", getUser().getFullName() ) );
    }

    private void addLoggedIn() {
        form.add( new Label( "user",
                getUser().getUsername() ) );
    }

    private void addFeedback() {
        form.add( new UserFeedbackPanel( "feedback" ) );
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
        getCommander().absent( getUser().getUsername() );
        super.setPlan( plan );
    }

    private void addPlanImage() {
        WebMarkupContainer image = new WebMarkupContainer( "planImage" );
        image.add( new AttributeModifier( "src", true, new Model<String>( getPlanImagePath() ) ) );
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

    private void addGotoLinks( Plan plan, User user ) {

        Actor actor = findActor( getQueryService(), user.getUsername() );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        BookmarkablePageLink<? extends WebPage > gotoReportLink =  newTargetedLink(
                "gotoReport",
                "",
                ResponderPage.class,
                ResponderPage.createParameters( actor, uri, plan.getVersion() ),
                null,
                plan );
        Label gotoReportLabel = new Label( "proceduresLabel", getGotoReportLabel( user, plan ) );
        gotoReportLink.add( gotoReportLabel );
        form.add(
            // Goto admin
            new WebMarkupContainer( "admin" )
                .add( newTargetedLink( "gotoAdmin", "", AdminPage.class, null, plan ) )
                .setVisible( user.isAdmin() ),

            // Goto model
            new WebMarkupContainer( "model" )
                .add( newTargetedLink( "gotoModel", "", PlanPage.class, null, plan ) )
                .setVisible( planner || plan.isTemplate() ),

            // Goto mapped procedures
            new WebMarkupContainer( "mapped" )
                .add( newTargetedLink( "gotoMapped", "", ProcedureMapPage.class, null, plan ) ).
                setVisible( planner || plan.isTemplate() ),

            // Goto personal procedures
            new WebMarkupContainer( "report" )
                .add( gotoReportLink )
                .add ( new Label( "proceduresDescription", getGotoReportDescription( user ,plan ) ) )
                .setVisible( planner || actor != null ) );
    }

    private String getGotoReportLabel( User user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "Procedures for all participants"
                : "My procedures";
    }

    private String getGotoReportDescription( User user, Plan plan ) {
        return user.isPlanner( plan.getUri() )
                ? "View and print the tasks and related communications (information sharing procedures) " +
                "that are assigned to all participants in the plan."
                : "View and print the tasks and related communications (information sharing procedures) " +
                "that are assigned to you as a participant in the plan.";
    }

    private static Actor findActor( QueryService queryService, String userName ) {
        Participation participation = queryService.findParticipation( userName );
        return participation != null && participation.getActor() != null
                        ? participation.getActor()
                        : null;
    }

    private void addSocial() {
        String[] tabsShown = {"Calendar", "Surveys", "Messages"};
        socialPanel = new SocialPanel( "social", false, tabsShown );
        form.add( socialPanel );
    }

    private String getPlanImagePath() {
        String path = imagingService.getSquareIconUrl( getPlan() );
        if ( path == null ) {
            path = "images/plan.png";
        }
        return path;
    }

    @Override
    public void changed( Change change ) {
        getCommander().clearTimeOut();
        if ( change.getMessage() != null ) {
            message = change.getMessage();
        }
        if ( change.isNone() ) {
        }
         else if ( change.isCommunicated() ) {
            // do something?
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        String message = change.getMessage();
        if ( message != null ) {
            addChangeMessagePanel();
            target.addComponent( messageContainer );
        }else if ( change.isCommunicated() ) {
                newMessage( target, change );
                refreshSocialPanel( target, change );
            }
        if ( change.getScript() != null ) {
            target.appendJavascript( change.getScript() );
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
