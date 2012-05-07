/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.reports.AbstractParticipantPage;
import com.mindalliance.channels.pages.reports.guidelines.AllGuidelinesPage;
import com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage;
import com.mindalliance.channels.pages.reports.infoNeeds.AllInfoNeedsPage;
import com.mindalliance.channels.pages.reports.infoNeeds.InfoNeedsPage;
import com.mindalliance.channels.pages.surveys.RFIsPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract Channels Web Page.
 */
public class AbstractChannelsWebPage extends WebPage implements Updatable, Modalable {

    public static final String PLAN_PARM = "plan";

    /**
     * Delay between refresh check callbacks.
     */
    public static final int REFRESH_DELAY = 10;

    public static final String VERSION_PARM = "v";

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractChannelsWebPage.class );

    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    ImagingService imagingService;

    @SpringBean
    private AttachmentManager attachmentManager;

    private Plan plan;

    @SpringBean
    private PlanManager planManager;

    private transient QueryService queryService;

    @SpringBean
    private Analyst analyst;

    @SpringBean
    private SemanticMatcher semanticMatcher;

    @SpringBean
    private ChannelsUser user;

    @SpringBean
    private ChannelsUserDao userDao;

    @SpringBean
    private PlanParticipationService planParticipationService;

    /**
     * Subsituted update target.
     */
    private Updatable updateTarget;
    private ModalWindow dialogWindow;


    //-------------------------------
    public AbstractChannelsWebPage() {
    }

    public AbstractChannelsWebPage( PageParameters parameters ) {
        super( parameters );
        setPlanFromParameters( parameters );
    }

    //-------------------------------
    public static void addPlanParameters( BookmarkablePageLink link, Plan plan ) {
        try {
            link.getPageParameters().set( PLAN_PARM, URLEncoder.encode( plan.getUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
        link.getPageParameters().set( VERSION_PARM, plan.getVersion() );
    }

    @Override
    public void changed( Change change ) {
        // do nothing
    }

    //-----------------------------------
    public static PageParameters createParameters( Specable profile, String uri, int version ) {

        PageParameters result = new PageParameters();
        result.set( "plan", uri );
        result.set( "v", version );
        if ( profile != null ) {
            if ( profile.getActor() != null )
                result.set( "agent", profile.getActor().getId() );
            if ( profile.getRole() != null )
                result.set( "role", profile.getRole().getId() );
            if ( profile.getOrganization() != null )
                result.set( "org", profile.getOrganization().getId() );
            if ( profile.getJurisdiction() != null )
                result.set( "place", profile.getJurisdiction().getId() );
        }
        return result;
    }

    protected Channels getApp() {
        return (Channels) getApplication();
    }

    protected Commander getCommander() {
        return commanderFactory.getCommander( getPlan() );
    }

    public PlanParticipationService getPlanParticipationService() {
        return planParticipationService;
    }

    protected List<PlanParticipation> getPlanParticipations( Plan plan, ChannelsUser user ) {
        return planParticipationService.getParticipations(
                plan,
                user.getUserInfo(),
                getQueryService() );
    }

    protected BookmarkablePageLink<? extends WebPage> getGuidelinesLink(
            String id, QueryService queryService, Plan plan, ChannelsUser user, boolean samePage ) {
        List<PlanParticipation> planParticipations = getPlanParticipations( plan, user );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        BookmarkablePageLink<? extends WebPage> guidelinesLink;
        if ( planner || planParticipations.size() != 1 ) {
            guidelinesLink = newTargetedLink(
                    id,
                    "",
                    AllGuidelinesPage.class,
                    AbstractParticipantPage.createParameters(
                            new ResourceSpec(),
                            uri,
                            plan.getVersion() ),
                    null,
                    plan );
        } else {
            Actor actor = planParticipations.get( 0 ).getActor( queryService );
            guidelinesLink = newTargetedLink( id,
                    "",
                    GuidelinesPage.class,
                    AbstractParticipantPage.createParameters(
                            actor,
                            uri,
                            plan.getVersion() ),
                    null,
                    plan );
        }
        if ( !samePage )
            guidelinesLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return guidelinesLink;
    }

    public BookmarkablePageLink<? extends WebPage> getInfoNeedsLink(
            String id, QueryService queryService, Plan plan, ChannelsUser user, boolean samePage ) {
        List<PlanParticipation> planParticipations = getPlanParticipations( plan, user );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        BookmarkablePageLink<? extends WebPage> infoNeedsLink;
        if ( planner || planParticipations.size() != 1 ) {
            infoNeedsLink = newTargetedLink(
                    id,
                    "",
                    AllInfoNeedsPage.class,
                    AbstractParticipantPage.createParameters(
                            new ResourceSpec(),
                            uri,
                            plan.getVersion() ),
                    null,
                    plan );
        } else {
            Actor actor = planParticipations.get( 0 ).getActor( queryService );
            infoNeedsLink = newTargetedLink( id,
                    "",
                    InfoNeedsPage.class,
                    AbstractParticipantPage.createParameters(
                            actor,
                            uri,
                            plan.getVersion() ),
                    null,
                    plan );
        }
        if ( !samePage )
            infoNeedsLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return infoNeedsLink;
    }

    public BookmarkablePageLink<? extends WebPage> getRFIsLink(
            String id,
            Plan plan,
            boolean samePage ) {
        BookmarkablePageLink<? extends WebPage> rfisLink = newTargetedLink(
                id,
                "",
                RFIsPage.class,
                new PageParameters(),
                null,
                plan );
        if ( !samePage )
            rfisLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return rfisLink;
    }


    /**
     * Build a new parameter container for the current selections.
     *
     * @return the parameters
     */
    public PageParameters getParameters() {
        PageParameters result = new PageParameters();

        if ( plan != null ) {
            try {
                result.set( PLAN_PARM, URLEncoder.encode( plan.getUri(), "UTF-8" ) );
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "Failed to url-encode plan uri " + plan.getUri(), e );
            }
            result.set( VERSION_PARM, Integer.toString( plan.getVersion() ) );
        }

        return result;
    }

    /**
     * Get all plans that the current can read.
     *
     * @return a list of plans
     */
    public final List<Plan> getPlans() {
        List<Plan> result = new ArrayList<Plan>();
        for ( Plan p : planManager.getReadablePlans( user ) ) {
            String uri = p.getUri();
            if ( user.isPlanner( uri ) )
                result.add( p );
            else if ( user.isParticipant( uri ) ) {
                if ( p.isProduction() )
                    result.add( p );
            }
        }
        return result;
    }

    //-----------------------------------
    public static ResourceSpec getProfile( QueryService service, PageParameters parameters ) throws NotFoundException {
        // TODO check read permission
        try {
            Actor actor = parameters.getNamedKeys().contains( "agent" ) ? service.find( Actor.class, parameters.get( "agent" ).toLong() )
                    : null;
            Role role =
                    parameters.getNamedKeys().contains( "role" ) ? service.find( Role.class, parameters.get( "role" ).toLong() ) : null;
            Organization organization =
                    parameters.getNamedKeys().contains( "org" ) ? service.find( Organization.class, parameters.get( "org" ).toLong() )
                            : null;
            Place jurisdiction =
                    parameters.getNamedKeys().contains( "place" ) ? service.find( Place.class, parameters.get( "place" ).toLong() )
                            : null;
            return new ResourceSpec( actor, role, organization, jurisdiction );
        } catch ( StringValueConversionException ignored ) {
            throw new NotFoundException();
        }
    }

    protected String getSupportCommunity() {
        Plan plan = user.getPlan();
        if ( plan != null ) {
            return plan.getPlannerSupportCommunity( planManager.getDefaultSupportCommunity() );
        } else {
            return planManager.getDefaultSupportCommunity();
        }
    }

    public boolean isPlanner() {
        return user.isPlanner( plan.getUri() );
    }

    protected PageParameters makePlanParameters() {
        PageParameters params = new PageParameters();
        try {
            params.set( PLAN_PARM, URLEncoder.encode( plan.getUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
        params.set( VERSION_PARM, plan.getVersion() );
        return params;
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", new Model<String>( visible ? "" : "display:none" ) ) );
    }

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id, String target, Class<T> pageClass, PopupSettings popupSettings, Plan plan ) {

        BookmarkablePageLink<T> link = new BookmarkablePageLink<T>( id, pageClass );
        addPlanParameters( link, plan );
        link.add( new AttributeModifier( "target", new Model<String>( target ) ) );
        if ( popupSettings != null )
            link.setPopupSettings( popupSettings );

        return link;
    }

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id, String target, Class<T> pageClass, PageParameters parameters, PopupSettings popupSettings,
            Plan plan ) {

        BookmarkablePageLink<T> link = newTargetedLink( id, target, pageClass, popupSettings, plan );
        for ( String name : parameters.getNamedKeys() ) {
            link.getPageParameters().set( name, "" + parameters.get( name ) );
        }
        return link;
    }

    public static String queryParameters( Plan p ) {
        String query = "";
        try {
            query = MessageFormat
                    .format( "&plan={0}&v={1,number,0}", URLEncoder.encode( p.getUri(), "UTF-8" ), p.getVersion() );
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to encode plan uri", e );
        }
        return query;
    }

    public static PageParameters planParameters( Plan p ) {
        PageParameters parameters = new PageParameters();
        try {
            parameters.set( "plan", URLEncoder.encode( p.getUri(), "UTF-8" ) );
            parameters.set( "v", p.getVersion() );
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to encode plan uri", e );
        }
        return parameters;
    }

    public static String redirectUrl( String path, Plan p ) {
        return path
                + "?"
                + queryParameters( p );
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        // do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        // do nothing
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
        user.setPlan( plan );
        queryService = null;
    }

    /**
     * Set plan from uri parameters.
     *
     * @param parameters the parameters
     */
    protected void setPlanFromParameters( PageParameters parameters ) {
        String encodedPlanUri = parameters.get( PLAN_PARM ).toString( null );
        if ( encodedPlanUri == null ) {
            String userPlanUri = user.getPlanUri() == null ? "" : user.getPlanUri();
            try {
                encodedPlanUri = URLEncoder.encode( userPlanUri, "UTF-8" );
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "Failed to encode plan uri", e );
                encodedPlanUri = "";
            }
        }
        String planUri = "";
        try {
            planUri = URLDecoder.decode( encodedPlanUri, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to decode plan uri", e );
        }
        int planVersion;
        try {
            planVersion = parameters.get( VERSION_PARM ).toInt( 0 );
        } catch ( StringValueConversionException ignored ) {
            LOG.warn( "Bad value in url" );
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not found" );
        }

        List<Plan> plans = getPlans();
        if ( plans.isEmpty() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );

        for ( Iterator<Plan> it = plans.iterator(); it.hasNext() && plan == null; ) {
            Plan p = it.next();
            if ( planUri.equals( p.getUri() ) ) {
                if ( user.isPlanner( p.getUri() ) ) {
                    if ( planVersion == p.getVersion() || p.isDevelopment() )
                        plan = p;
                } else if ( p.isProduction() )
                    plan = p;
            }
        }

        if ( plan == null ) {
            LOG.warn( "PANIC: selecting first plan" );
            plan = plans.get( 0 );
        }
        user.setPlan( plan );
        getQueryService();
    }

    protected void setResponsePageWithPlan() {
        setResponsePage( AdminPage.class, makePlanParameters() );
    }

    @Override
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // do nothing
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        // do nothing
    }

    @Override
    public void setUpdateTarget( Updatable updatable ) {
        updateTarget = updatable;
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public void setAttachmentManager( AttachmentManager attachmentManager ) {
        this.attachmentManager = attachmentManager;
    }

    public ImagingService getImagingService() {
        return imagingService;
    }

    public void setImagingService( ImagingService imagingService ) {
        this.imagingService = imagingService;
    }

    public Plan getPlan() {
        if ( plan == null )
            setPlanFromParameters( getPageParameters() );
        return plan;
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    @Override
    public final QueryService getQueryService() {
        if ( queryService == null )
            queryService = commanderFactory.getCommander( plan ).getQueryService();
        return queryService;
    }

    public SemanticMatcher getSemanticMatcher() {
        return semanticMatcher;
    }

    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    public Analyst getAnalyst() {
        return analyst;
    }

    public void setAnalyst( Analyst analyst ) {
        this.analyst = analyst;
    }

    public ChannelsUser getUser() {
        return user;
    }

    public void setUser( ChannelsUser user ) {
        this.user = user;
    }

    public ChannelsUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao( ChannelsUserDao userDao ) {
        this.userDao = userDao;
    }

    // Modalable

    public void addModalDialog( String id, String cookieName, MarkupContainer container ) {
        dialogWindow = new ModalWindow( id );
        dialogWindow.setOutputMarkupId( true );
        dialogWindow.setResizable( true );
        dialogWindow.setContent(
                new Label(
                        dialogWindow.getContentId(),
                        "" ) );
        dialogWindow.setTitle( "" );
        dialogWindow.setCookieName( cookieName );
        dialogWindow.setCloseButtonCallback(
                new ModalWindow.CloseButtonCallback() {
                    public boolean onCloseButtonClicked( AjaxRequestTarget target ) {
                        return true;
                    }
                } );
        dialogWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback() {
            public void onClose( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        dialogWindow.setHeightUnit( "px" );
        dialogWindow.setInitialHeight( 0 );
        dialogWindow.setInitialWidth( 0 );
        container.addOrReplace( dialogWindow );
    }

    @Override
    public void showDialog(
            String title,
            int height,
            int width,
            Updatable contents,
            Updatable updateTarget,
            AjaxRequestTarget target ) {
        dialogWindow.setTitle( title );
        dialogWindow.setInitialHeight( height );
        dialogWindow.setInitialWidth( width );
        dialogWindow.setContent( (Component) contents );
        contents.setUpdateTarget( updateTarget );
        dialogWindow.show( target );
    }

    @Override
    public void hideDialog( AjaxRequestTarget target ) {
        dialogWindow.close( target );
    }

    @Override
    public String getModalContentId() {
        return dialogWindow.getContentId();
    }


}
