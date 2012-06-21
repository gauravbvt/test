package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.notifier.NotifierWebMarkupContainer;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.services.FeedbackService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract basic Channels page.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/20/12
 * Time: 2:53 PM
 */
public abstract class AbstractChannelsBasicPage extends AbstractChannelsWebPage {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractChannelsBasicPage.class );


    @SpringBean
    private FeedbackService feedbackService;
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
    /**
     * Notifier.
     */
    private NotifierWebMarkupContainer notifier;
    private String message;
    private WebMarkupContainer planPath;
    private static final int MAX_PLAN_DESCRIPTION_LENGTH = 50;
    private WebMarkupContainer contentsContainer;


    protected AbstractChannelsBasicPage() {
    }

    protected AbstractChannelsBasicPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    protected abstract void addContent(  );

    protected void updateContent( AjaxRequestTarget target ) {
        addContentsContainer();
        target.add( getContainer() );
    }

    protected void newMessage( AjaxRequestTarget target, Change change ) {
        // default is do nothing
    }

    private void init() {
        getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        addPageTitle();
        addForm();
        addSpinner();
        addNotifier();
        addLoggedIn();
        addHelp();
        addFeedback();
        addPlanPath();
        addContentsContainer();
     }

    private void addContentsContainer() {
        contentsContainer = new WebMarkupContainer( "contentsContainer" );
        contentsContainer.setOutputMarkupId( true );
        form.addOrReplace( contentsContainer );
        addContent();
    }

    protected WebMarkupContainer getContainer() {
        return contentsContainer;
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
        setResponsePage( this.getClass(), planParameters( getPlan() ) );
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
            form.add( helpLink );
        }
    }


    private void addNotifier() {
        notifier = new NotifierWebMarkupContainer( "notifier" );
        add( notifier );
    }

    private String getMessage() {
        return message == null ? "" : message;
    }


    private void doTimedUpdate( AjaxRequestTarget target ) {
        getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        updateContent( target );
    }


    private void addLoggedIn() {
        form.add( new Label( "user",
                getUser().getUsername() ) );
    }

    private void addFeedback() {
        form.add( new UserFeedbackPanel( "feedback", Feedback.PARTICIPATING ) );
    }

    private void addPlanPath() {
        planPath = new WebMarkupContainer( "planPath" );
        planPath.setOutputMarkupId( true );
        form.addOrReplace( planPath );
        addHomeInPath();
        addSelectedPlanInPath();
        addOtherPlansInPath();
        addSelectedPlanDescription();
    }

    private void addHomeInPath() {
        AjaxLink<String> homeLink = new AjaxLink<String>( "homeLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( UserPage.class, planParameters( getPlan() ) );
            }
        };
        planPath.add( homeLink );
    }

    private void addSelectedPlanInPath() {
        Label selectedPlanName = new Label(
                "selectedPlan",
                getPlan().toString() );
        planPath.add( selectedPlanName );
    }

    private void addOtherPlansInPath() {
        ListView<Plan> otherPlansListView = new ListView<Plan>(
                "otherPlans",
                getOtherPlans()
        ) {
            @Override
            protected void populateItem( final ListItem<Plan> item ) {
                AjaxLink<String> otherPlanLink = new AjaxLink<String>( "otherPlanLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        setPlan( item.getModelObject() );
                        redirectHere();
                    }
                };
                otherPlanLink.add( new Label( "otherPlanName", item.getModelObject().toString() ) );
                item.add( otherPlanLink );
            }
        };
        planPath.add( otherPlansListView );
    }

    private void addSelectedPlanDescription() {
        Label selectedPlanDescription = new Label(
                "planDescription",
                getAbbreviatedSelectedPlanDescription() );
        planPath.add( selectedPlanDescription );
        selectedPlanDescription.add( new AttributeModifier( "title", getPlan().getDescription() ) );
    }

    private String getAbbreviatedSelectedPlanDescription() {
        String oneLiner = getPlan().getDescription().replaceAll( "\\s+", " " );
        return StringUtils.abbreviate( oneLiner, MAX_PLAN_DESCRIPTION_LENGTH );
    }

    private List<Plan> getOtherPlans() {
        List<Plan> otherPlans = new ArrayList<Plan>( getPlans() );
        otherPlans.remove( getPlan() );
        return otherPlans;
    }


    public void setPlan( Plan plan ) {
        getCommander().userLeftPlan( getUser().getUsername() );
        super.setPlan( plan );
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
            if ( change.isForProperty( "participation" ) ) {
                updateContent( target );
                target.add( form );
            }
        }
        String message = change.getMessage();
        if ( message != null ) {
            notifier.create( target,
                    "Notification",
                    message );
        } else if ( change.isCommunicated() ) {
            newMessage( target, change );
            updateContent( target );
        }
        if ( change.getScript() != null ) {
            target.appendJavaScript( change.getScript() );
        }
    }

}
