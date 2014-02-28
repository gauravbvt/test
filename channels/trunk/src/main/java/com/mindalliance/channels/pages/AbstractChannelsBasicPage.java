package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.notifier.NotifierWebMarkupContainer;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.help.HelpPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean showingQuickHelp;
    private AjaxLink<String> quickHelpLink;
    private HelpPanel helpPanel;
    /**
     * Help section.
     */
    private String sectionId;
    /**
     * Topic section.
     */
    private String topicId;

    protected AbstractChannelsBasicPage() {
        this( new PageParameters() );
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
        WebMarkupContainer body = new WebMarkupContainer( "body" );
        add( body );
        addGalleryModalWindow( "gallery", null, body );
        addForm( body );
        addHomeLink();
        addSpinner();
        addNotifier( body );
        addLoggedIn();
        // addHelp();
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


    private void addForm(  WebMarkupContainer body ) {
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
        addQuickHelp();
        addVizGalleryButton();
        body.add( form );
    }

    private void addQuickHelp() {
        addQuickHelpButton();
        addQuickHelpPanel();
    }

    private void addVizGalleryButton() {
        AjaxLink<String> vizGalleryLink = new AjaxLink<String>( "vizGalleryButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showGallery( "planner", target );
            }
        };
        addTipTitle( vizGalleryLink, "Opens the Channels gallery" );
        form.add(  vizGalleryLink );
    }

    private void addQuickHelpButton() {
        quickHelpLink = new AjaxLink<String>( "quickHelpButton" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                helpPanel.selectTopicInSection( getDefaultUserRoleId(), getHelpSectionId(), getHelpTopicId(), target );
                toggleQuickHelp( target );
            }
        };
        quickHelpLink.setOutputMarkupId( true );
        addTipTitle( quickHelpLink, "Opens online help" );
        form.add(  quickHelpLink );
    }

    protected String getHelpSectionId() {
        return null;  // DEFAULT
    }

    protected String getHelpTopicId() {
        return null;  // DEFAULT
    }


    private void toggleQuickHelp( AjaxRequestTarget target ) {
        showingQuickHelp = !showingQuickHelp;
        updateQuickHelpVisibility( target );
    }

    private void updateQuickHelpVisibility( AjaxRequestTarget target ) {
        makeVisible( quickHelpLink, !showingQuickHelp );
        makeVisible( helpPanel, showingQuickHelp );
        target.add( quickHelpLink );
        target.add( helpPanel );
    }

    private void showHelp( Change change, AjaxRequestTarget target ) {
        showingQuickHelp = true;
        updateQuickHelpVisibility( target );
        String userRoleId = change.hasQualifier("userRoleId") ? (String)change.getQualifier( "userRoleId" ) : null;
        sectionId = (String)change.getQualifier( "sectionId" );
        topicId = (String)change.getQualifier( "topicId" );
        helpPanel.selectTopicInSection( userRoleId, sectionId, topicId, target );
    }

    private void addQuickHelpPanel() {
        helpPanel = new HelpPanel( "quickHelp", getGuideName(), getDefaultUserRoleId(), getHelpContext() );
        makeVisible( helpPanel, false );
        form.add( helpPanel );
    }

    protected abstract String getDefaultUserRoleId();

    protected Map<String, Object> getHelpContext() {
        return new HashMap<String,Object>(); //DEFAULT
    }

    protected String getGuideName() {
        return "channels_guide";
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

    private void addNotifier( WebMarkupContainer body ) {
        notifier = new NotifierWebMarkupContainer( "notifier" );
        makeVisible( notifier, false );
        body.add( notifier );
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
        String oneLiner = getCollaborationModel().getDescription().replaceAll( "\\s+", " " );
        return StringUtils.abbreviate( oneLiner, MAX_PLAN_DESCRIPTION_LENGTH );
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
        } else if ( change.isCollapsed() || change.isRemoved() ) {
            collapse( change );
        } else if ( change.isExpanded() || change.isAdded() ) {
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
        } else if ( change.isHelpTopic() ) {
            helpPanel.selectTopicInSection( change.getUserRoleId(), change.getSectionId(), change.getTopicId(), target );
        } else if ( change.isCommunicated() ) {
            newMessage( target, change );
            updateContent( target );
        } else if ( change.isCollapsed() && change.getId() == Channels.GUIDE_ID ) {
            toggleQuickHelp( target );
        } else if ( change.isGuide() ) {
            showHelp( change, target );
        }
        if ( change.getScript() != null ) {
            target.appendJavaScript( change.getScript() );
        }
    }

    protected PlanCommunity getDomainPlanCommunity() {
        return planCommunityManager.getPlanCommunity( getPlanCommunity().getModelUri() );
    }

    protected CommunityService getDomainCommunityService() {
        return getCommunityService( getDomainPlanCommunity() );
    }

    protected CommunityService getCommunityService( PlanCommunity planCommunity ) {
        return communityServiceFactory.getService( planCommunity );
    }

}
