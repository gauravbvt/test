package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.notifier.NotifierWebMarkupContainer;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.social.services.FeedbackService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

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

    @SpringBean
    private PlanCommunityManager planCommunityManager;

    @SpringBean
    private CommunityServiceFactory communityServiceFactory;

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
    private Component breadCrumbs;
    private static final int MAX_PLAN_DESCRIPTION_LENGTH = 50;
    private WebMarkupContainer contentsContainer;


    protected AbstractChannelsBasicPage() {
    }

    protected AbstractChannelsBasicPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    protected abstract void addContent();

    protected abstract String getContentsCssClass();

    protected abstract String getFeedbackType();


    // DEFAULT
    protected void updateContent( AjaxRequestTarget target ) {
        addContentsContainer();
        target.add( getContainer() );
    }

    protected void newMessage( AjaxRequestTarget target, Change change ) {
        // default is do nothing
    }

    private void init() {
        if ( getPlanCommunity() != null )
            getCommander(); // replays journal, save snapshot upon initialization.
        if ( getPlanCommunity() != null && canTimeOut() )
            getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        addPageTitle();
        addForm();
        addHomeLink();
        addSpinner();
        addNotifier();
        addLoggedIn();
        addHelp();
        addFeedback();
        addBreadCrumbs();
        addContentsContainer();
    }

    private void addHomeLink() {
        WebMarkupContainer homeLink = new WebMarkupContainer( "homeLink" );
        homeLink.add( new AttributeModifier(
                "href",
                makeHomeUrl() ) );
        form.add( homeLink );
    }

    private void addContentsContainer() {
        contentsContainer = new WebMarkupContainer( "contentsContainer" );
        contentsContainer.setOutputMarkupId( true );
        form.addOrReplace( contentsContainer );
        contentsContainer.add( new AttributeModifier( "class", getContentsCssClass() ) );
        addContent();
    }

    protected WebMarkupContainer getContainer() {
        return contentsContainer;
    }

    public Form getForm() {
        return form;
    }

    private void addPageTitle() {
        add( new Label( "sg-title",
                new Model<String>( "Channels - Collaboration Planning" ) ) );

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
                if ( canTimeOut() )
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
        setResponsePage( this.getClass(), getPageParameters() );
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
        makeVisible( notifier, false );
        add( notifier );
    }

    protected NotifierWebMarkupContainer getNotifier() {
        return notifier;
    }

    private String getMessage() {
        return message == null ? "" : message;
    }


    protected void doTimedUpdate( AjaxRequestTarget target ) {
        getCommander().keepAlive( getUser().getUsername(), REFRESH_DELAY );
        updateContent( target );
    }


    private void addLoggedIn() {
        form.add( new Label( "user",
                getUser().getUsername() ) );
    }

    private void addFeedback() {
        Component userFeedbackPanel;
        if ( isCommunityContext() || isPlanContext() ) {
            userFeedbackPanel = new UserFeedbackPanel( "feedback", getFeedbackType() );
        }  else {
            userFeedbackPanel = new Label( "feedback", "" );
            userFeedbackPanel.setVisible( false );
        }
        form.add( userFeedbackPanel );
    }


    protected void addBreadCrumbs() {
        breadCrumbs = hasBreadCrumbs()
                ? new BreadcrumbsPanel( "contextPath", this )
                : new Label( "contextPath", "" );
        breadCrumbs.setOutputMarkupId( true );
        form.addOrReplace( breadCrumbs );
    }

    protected boolean hasBreadCrumbs() {
        return true; // default
    }


    private String getAbbreviatedSelectedPlanDescription() {
        String oneLiner = getPlan().getDescription().replaceAll( "\\s+", " " );
        return StringUtils.abbreviate( oneLiner, MAX_PLAN_DESCRIPTION_LENGTH );
    }


/*
    public void setPlan( Plan newPlan ) {
        userLeftPlanCommunity();
        super.setPlan( newPlan );
    }
*/

    private Attachment getHelp() {
        return isPlanContext()
                ? (Attachment) CollectionUtils.find(
                getPlan().getAttachments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Attachment) object ).isHelp();
                    }
                } )
                : null;
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
        } else if ( change.isCollapsed() || change.isRemoved() )
            collapse( change );
        else if ( change.isExpanded() || change.isAdded() ) {
            expand( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() ) {
            addBreadCrumbs();
            target.add( breadCrumbs );
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

    protected PlanCommunity getDomainPlanCommunity() {
        return planCommunityManager.getPlanCommunity( getPlanCommunity().getPlanUri() );
    }

    protected CommunityService getDomainCommunityService() {
        return getCommunityService( getDomainPlanCommunity() );
    }

    protected CommunityService getCommunityService( PlanCommunity planCommunity ) {
        return communityServiceFactory.getService( planCommunity );
    }

}
