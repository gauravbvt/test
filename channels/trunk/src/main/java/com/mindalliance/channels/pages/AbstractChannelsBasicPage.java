package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.notifier.NotifierWebMarkupContainer;
import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.IndicatorAwareForm;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.social.services.FeedbackService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private WebMarkupContainer pagePath;
    private static final int MAX_PLAN_DESCRIPTION_LENGTH = 50;
    private WebMarkupContainer contentsContainer;


    protected AbstractChannelsBasicPage() {
    }

    protected AbstractChannelsBasicPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    protected abstract void addContent(  );

    // DEFAULT
    protected List<PagePathItem> getIntermediatePagesPathItems() {
        return new ArrayList<PagePathItem>();
    }

    // DEFAULT
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
        addHomeLink();
        addSpinner();
        addNotifier();
        addLoggedIn();
        addHelp();
        addFeedback();
        addPagePath();
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
        contentsContainer.add( new AttributeModifier( "class", getContentsCssClass() ));
        addContent();
    }

    protected abstract String getContentsCssClass();

    protected abstract String getPageName();

    protected abstract String getFeedbackType();

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
        form.add( new UserFeedbackPanel( "feedback", getFeedbackType() ) );
    }


    protected void addPagePath() {
        pagePath = new WebMarkupContainer( "planPath" );
        pagePath.setOutputMarkupId( true );
        form.addOrReplace( pagePath );
        addHomeInPath();
        addSelectedPlanInPath();
        addOtherPlansInPath();
        addPathPageItems();
    }

    private void addHomeInPath() {
        AjaxLink<String> homeLink = new AjaxLink<String>( "homeLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( UserPage.class, planParameters( getPlan() ) );
            }
        };
        pagePath.add( homeLink );
    }


    private void addSelectedPlanInPath() {
        Label selectedPlanName = new Label(
                "selectedPlan",
                getPlan().toString() );
        pagePath.add( selectedPlanName );
        selectedPlanName.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                PageParameters params = makePlanParameters();
                setResponsePage( UserPage.class, params );
            }
        } );
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
                        PageParameters params = makePlanParameters();
                        setResponsePage( UserPage.class, params );
                    }
                };
                otherPlanLink.add( new Label( "otherPlanName", item.getModelObject().toString() ) );
                item.add( otherPlanLink );
            }
        };
        pagePath.add( otherPlansListView );
    }

    private void addPathPageItems() {
        ListView<PagePathItem> pagePathItems = new ListView<PagePathItem>(
                "pageItems",
                getPagePathItems()
                ) {
            @Override
            protected void populateItem( ListItem<PagePathItem> item ) {
                PagePathItem pagePathItem = item.getModelObject();
                item.add( pagePathItem.getLink( "pageItemLink" ) );
            }
        };
        pagePathItems.setVisible( this.getClass() != UserPage.class );
        pagePath.add( pagePathItems );
    }

    private List<PagePathItem> getPagePathItems() {
        List<PagePathItem> pagePathItems = new ArrayList<PagePathItem>();
        pagePathItems.addAll( getIntermediatePagesPathItems() );
        pagePathItems.add( new PagePathItem( getClass(), getPageParameters(), getPageName() ) );
        return pagePathItems;
    }

    private String getAbbreviatedSelectedPlanDescription() {
        String oneLiner = getPlan().getDescription().replaceAll( "\\s+", " " );
        return StringUtils.abbreviate( oneLiner, MAX_PLAN_DESCRIPTION_LENGTH );
    }

    private List<Plan> getOtherPlans() {
        List<Plan> otherPlans = new ArrayList<Plan>( getPlans() );
        otherPlans.remove( getPlan() );
        Collections.sort( otherPlans, new Comparator<Plan>() {
            @Override
            public int compare( Plan p1, Plan p2 ) {
                return p1.getName().compareTo( p2.getName() );
            }
        });
        return otherPlans;
    }


    public void setPlan( Plan newPlan ) {
        userLeftPlanCommunity();
        super.setPlan( newPlan );
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
        } else if ( change.isCollapsed() || change.isRemoved() )
            collapse( change );
        else if ( change.isExpanded() || change.isAdded() ) {
                expand( change );
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

    protected class PagePathItem implements Serializable {

        private Class<? extends AbstractChannelsWebPage> pageClass;
        private PageParameters pageParameters;
        private String name;

        public PagePathItem(
                Class<? extends AbstractChannelsWebPage> pageClass,
                PageParameters pageParameters,
                String name ) {
            this.pageClass = pageClass;
            this.pageParameters = pageParameters;
            this.name = name;
        }

        protected BookmarkablePageLink<String> getLink( String id ) {
            BookmarkablePageLink<String>  link = new BookmarkablePageLink<String>( id, pageClass, pageParameters );
            link.add(  new Label( "pageName", name ) );
            return link;
        }
    }

}
