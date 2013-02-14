/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.tiptip.TipTipBehavior;
import com.google.code.jqwicket.ui.tiptip.TipTipOptions;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.community.participation.UserParticipation;
import com.mindalliance.channels.core.community.participation.UserParticipationService;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.nlp.SemanticMatcher;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.reports.AbstractParticipantPage;
import com.mindalliance.channels.pages.reports.protocols.AllProtocolsPage;
import com.mindalliance.channels.pages.reports.protocols.ProtocolsPage;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Abstract Channels Web Page.
 */
public class AbstractChannelsWebPage extends WebPage implements Updatable, Modalable {

    // PLAN_PARM and COMMUNITY_PARM page parameters can not be both set.

    // Implied domain plan community (for the plan planners)
    public static final String PLAN_PARM = "plan";

    // Explicit plan community (for a community of adopters of a plan)
    public static final String COMMUNITY_PARM = "community";

    /**
     * Delay between refresh check callbacks.
     */
    public static final int REFRESH_DELAY = 10;

    public static final String VERSION_PARM = "v";

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractChannelsWebPage.class );

    /**
     * Id of components that are expanded.
     */
    private Set<Long> expansions = new HashSet<Long>();


    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    ImagingService imagingService;

    @SpringBean
    private AttachmentManager attachmentManager;

    private Plan plan;
    private PlanCommunity planCommunity;

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
    private UserParticipationService userParticipationService;

    @SpringBean
    private PlanCommunityManager planCommunityManager;

    @SpringBean
    private CommunityServiceFactory communityServiceFactory;

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
        setPlanCommunityFromParameters( parameters ); // can set plan if plan community specified.
        if ( plan == null ) {
            setPlanFromParameters( parameters ); // sets at least a default plan
            if ( planCommunity == null && plan != null ) {
                planCommunity = planCommunityManager.getDomainPlanCommunity( plan );
                user.setPlanCommunityUri( planCommunity.getUri() );
            }
        }
    }

    public Set<Long> getExpansions() {
        return expansions;
    }

    public void setExpansions( Set<Long> expansions ) {
        this.expansions = expansions;
    }

    public void addExpansion( long expansion ) {
        expansions.add( expansion );
    }

    public void removeExpansion( long expansion ) {
        expansions.remove( expansion );
    }

    protected void expand( Identifiable identifiable ) {
        if ( identifiable != null )
            expand( new Change( Change.Type.None, identifiable ) );
    }

    protected void expand( Change change ) {
        tryAcquiringLock( change );
        if ( isSingleExpansion( change ) ) {
            ModelObject subject = (ModelObject) change.getSubject( getCommunityService() );
            ModelObject previous = findExpanded( subject );
            if ( previous != null && !previous.equals( subject ) ) {
                collapse( new Change( Change.Type.None, previous ) );
            }
        }
        addExpansion( change.getId() );
    }

    private ModelObject findExpanded( ModelObject subject ) {
        Class clazz = subject instanceof ModelEntity
                ? ModelEntity.class
                : subject.getClass();
        for ( long id : getExpansions() ) {
            try {
                ModelObject mo = getQueryService().find( ModelObject.class, id );
                if ( clazz.isAssignableFrom( mo.getClass() ) )
                    return ( mo );
            } catch ( NotFoundException ignored ) {
                // ignore
            }
        }
        return null;
    }


    private boolean isSingleExpansion( Change change ) {
        return change.isForInstanceOf( ModelEntity.class );
    }


    protected void collapse( Change change ) {
        tryReleasingLock( change );
        removeExpansion( change.getId() );
    }

    protected void tryReleasingLock( Change change ) {
        getCommander().releaseAnyLockOn( getUser().getUsername(), change.getId() );
    }

    protected void tryAcquiringLock( Change change ) {
        if ( change.isByIdOnly() && getPlan().isDevelopment() ) {
            getCommander().requestLockOn( getUser().getUsername(), change.getId() );
        } else if ( change.isForInstanceOf( Identifiable.class ) ) {
            Identifiable identifiable = change.getSubject( getCommunityService() );
            if ( !ModelObject.isUnknownModelObject( identifiable )
                    && ( identifiable.isModifiableInProduction() || getPlan().isDevelopment() )
                    && getCommander().isLockable( change.getClassName() ) ) {
                getCommander().requestLockOn( getUser().getUsername(), change.getId() );
            }
        }
    }

    /**
     * Get read-only expansions.
     *
     * @return a read-only set of Longs
     */
    protected Set<Long> getReadOnlyExpansions() {
        return Collections.unmodifiableSet( getExpansions() );
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

    public static void addPlanCommunityParameter( BookmarkablePageLink link, PlanCommunity planCommunity ) {
        try {
            link.getPageParameters().set( COMMUNITY_PARM, URLEncoder.encode( planCommunity.getUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
    }

    protected boolean isCommunityContext() {
        return getPageParameters().getNamedKeys().contains( COMMUNITY_PARM );
    }

    protected boolean isPlanContext() {
        return getPageParameters().getNamedKeys().contains( PLAN_PARM );
    }


    @Override
    public void changed( Change change ) {
        // do nothing
    }

    //-----------------------------------
    public static PageParameters createParameters( Specable profile, PlanCommunity planCommunity, int version ) {

        PageParameters result = new PageParameters();
        result.set( AbstractChannelsWebPage.PLAN_PARM, planCommunity.getPlanUri() );
        result.set( AbstractChannelsWebPage.COMMUNITY_PARM, planCommunity.getUri() );
        result.set( AbstractChannelsWebPage.VERSION_PARM, version );
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

    protected boolean canTimeOut() {
        return false;
    }

    protected Commander getCommander() {
        return commanderFactory.getCommander( getCommunityService() );
    }

    public UserParticipationService getUserParticipationService() {
        return userParticipationService;
    }

    protected List<UserParticipation> getUserParticipations( PlanCommunity planCommunity, ChannelsUser user ) {
        return userParticipationService.getActiveUserParticipations(
                user,
                getCommunityService() );
    }

    public String getPlanCommunityUri() {
        return planCommunity == null ? getUser().getPlanCommunityUri() : planCommunity.getUri();
    }

    public PlanCommunity getPlanCommunity() {
        if ( planCommunity == null ) {
            planCommunity = planCommunityManager.getPlanCommunity( getPlanCommunityUri() );
        }
        return planCommunity;
    }

    private PlanService getPlanService() {
        return getCommunityService().getPlanService();
    }

    protected BookmarkablePageLink<? extends WebPage> getProtocolsLink(
            String id,
            PlanCommunity planCommunity,
            ChannelsUser user,
            boolean samePage ) {
        List<UserParticipation> userParticipations = getUserParticipations( planCommunity, user );
        boolean planner = user.isPlanner( planCommunity.getPlanUri() );
        BookmarkablePageLink<? extends WebPage> guidelinesLink;
        if ( planner || userParticipations.size() != 1 ) {
            guidelinesLink = newTargetedLink(
                    id,
                    "",
                    AllProtocolsPage.class,
                    AbstractParticipantPage.createParameters(
                            new ResourceSpec(),
                            planCommunity,
                            getPlan().getVersion() ), // version needed?
                    null,
                    planCommunity );
        } else {
            Actor actor = userParticipations.get( 0 ).getAgent( getCommunityService() ).getActor(); // todo - COMMUNITY - agents!
            guidelinesLink = newTargetedLink( id,
                    "",
                    ProtocolsPage.class,
                    AbstractParticipantPage.createParameters(
                            actor,
                            planCommunity,
                            plan.getVersion() ),
                    null,
                    planCommunity );
        }
        if ( !samePage )
            guidelinesLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return guidelinesLink;
    }

    public BookmarkablePageLink<? extends WebPage> getRFIsLink(
            String id,
            PlanCommunity planCommunity,
            boolean samePage ) {
        BookmarkablePageLink<? extends WebPage> rfisLink = newTargetedLink(
                id,
                "",
                RFIsPage.class,
                new PageParameters(),
                null,
                planCommunity );
        if ( !samePage )
            rfisLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return rfisLink;
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


    public BookmarkablePageLink<? extends WebPage> getFeedbackLink(
            String id,
            PlanCommunity planCommunity,
            boolean samePage ) {
        BookmarkablePageLink<? extends WebPage> feedbackLink = newTargetedLink(
                id,
                "",
                FeedbackPage.class,
                new PageParameters(),
                null,
                planCommunity );
        if ( !samePage )
            feedbackLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return feedbackLink;
    }

    public BookmarkablePageLink<? extends WebPage> getFeedbackLink(
            String id,
            Plan plan,
            boolean samePage ) {
        BookmarkablePageLink<? extends WebPage> feedbackLink = newTargetedLink(
                id,
                "",
                FeedbackPage.class,
                new PageParameters(),
                null,
                plan );
        if ( !samePage )
            feedbackLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return feedbackLink;
    }


    public BookmarkablePageLink<? extends WebPage> getRequirementsLink(
            String id,
            PlanCommunity planCommunity,
            boolean samePage ) {
        BookmarkablePageLink<? extends WebPage> requirementsLink = newTargetedLink(
                id,
                "",
                RequirementsPage.class,
                new PageParameters(),
                null,
                planCommunity );
        if ( !samePage )
            requirementsLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return requirementsLink;
    }


    /**
     * Build a new parameter container for the current selections.
     *
     * @return the parameters
     */
    public PageParameters getParameters() {
        PageParameters result = new PageParameters();
        try {
            if ( getPlan() != null ) {
                result.set( PLAN_PARM, URLEncoder.encode( getPlan().getUri(), "UTF-8" ) );
                result.set( VERSION_PARM, Integer.toString( getPlan().getVersion() ) );
            }
            PlanCommunity planCommunity = getPlanCommunity();
            if ( planCommunity != null ) {
                result.set( COMMUNITY_PARM, URLEncoder.encode( planCommunity.getUri(), "UTF-8" ) );
            }
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to url-encode", e );
        }
        return result;
    }

    /**
     * Get all plans that the current user can read.
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
        Collections.sort( result, new Comparator<Plan>() {
            @Override
            public int compare( Plan p1, Plan p2 ) {
                return p1.getName().compareTo( p2.getName() );
            }
        } );
        return result;
    }

    /**
     * Get all non-domain communities that the current user can see.
     *
     * @return a list of plan communities
     */
    public final List<PlanCommunity> getVisiblePlanCommunities() {
        List<PlanCommunity> result = new ArrayList<PlanCommunity>();
        for ( PlanCommunity p : planCommunityManager.getPlanCommunities() ) {
            if ( !p.isDomainCommunity() ) {
                CommunityService communityService = communityServiceFactory.getService( p );
                if ( !p.isClosed() || communityService.isCommunityPlanner( user ) )
                    result.add( p );
            }
        }
        Collections.sort( result, new Comparator<PlanCommunity>() {
            @Override
            public int compare( PlanCommunity p1, PlanCommunity p2 ) {
                return p1.getName().compareTo( p2.getName() );
            }
        } );
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
        return user.isPlanner( getPlan().getUri() );
    }

    protected PageParameters makePlanParameters() {
        PageParameters params = new PageParameters();
        try {
            params.set( PLAN_PARM, URLEncoder.encode( getPlan().getUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
        params.set( VERSION_PARM, getPlan().getVersion() );
        return params;
    }

    protected PageParameters makeCommunityParameters() {
        PageParameters params = new PageParameters();
        try {
            params.set( COMMUNITY_PARM, URLEncoder.encode( getPlanCommunityUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
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

    public <T extends WebPage> BookmarkablePageLink<T> newTargetedLink( String id, Class<T> pageClass ) {
        return new BookmarkablePageLink<T>( id, pageClass );
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

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id, String target, Class<T> pageClass, PopupSettings popupSettings, PlanCommunity planCommunity ) {

        BookmarkablePageLink<T> link = new BookmarkablePageLink<T>( id, pageClass );
        addPlanCommunityParameter( link, planCommunity );
        link.add( new AttributeModifier( "target", new Model<String>( target ) ) );
        if ( popupSettings != null )
            link.setPopupSettings( popupSettings );

        return link;
    }

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id, String target, Class<T> pageClass, PageParameters parameters, PopupSettings popupSettings,
            PlanCommunity planCommunity ) {

        BookmarkablePageLink<T> link = newTargetedLink( id, target, pageClass, popupSettings, planCommunity );
        for ( String name : parameters.getNamedKeys() ) {
            link.getPageParameters().set( name, "" + parameters.get( name ) );
        }
        return link;
    }

    public static String queryParameters() {
        StringBuilder query = new StringBuilder();
        query.append( "&" );
        try {
            if ( ChannelsUser.current().getPlanCommunityUri() != null ) {
                query.append( COMMUNITY_PARM )
                        .append( "=" )
                        .append( ChannelsUser.current().getPlanCommunityUri() );
            } else {
                Plan p = ChannelsUser.current().getPlan();
                query.append( PLAN_PARM )
                        .append( "=" )
                        .append( URLEncoder.encode( p.getUri(), "UTF-8" ) )
                        .append( "&" )
                        .append( VERSION_PARM )
                        .append( "=" )
                        .append( p.getVersion() );
            }
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to encode plan uri", e );
        }
        return query.toString();
    }

    public static PageParameters planParameters( Plan p ) {
        PageParameters parameters = new PageParameters();
        try {
            parameters.set( PLAN_PARM, URLEncoder.encode( p.getUri(), "UTF-8" ) );
            parameters.set( VERSION_PARM, p.getVersion() );
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to encode plan uri", e );
        }
        return parameters;
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

    public void setPlanCommunity( PlanCommunity planCommunity ) {  // sets user plan and community uri
        this.planCommunity = planCommunity;
        if ( planCommunity != null ) {
            CommunityService communityService = communityServiceFactory.getService( planCommunity );
            user.setCommunityService( communityService );
            setPlan( communityService.getPlan() );
        }
    }

    protected void setPlanCommunityFromParameters( PageParameters pageParameters ) {
        PlanCommunity planCommunity = getPlanCommunityFromParameters( pageParameters );
        setPlanCommunity( planCommunity );  // also sets plan if planCommunity not null
    }

    public PlanCommunity getPlanCommunityFromParameters( PageParameters pageParameters ) {
        PlanCommunity planCommunity = null;
        String encodedCommunityUri = pageParameters.get( COMMUNITY_PARM ).toString( null );
        String communityUri = null;
        if ( encodedCommunityUri != null ) { // community identified
            // assert pageParameters.get( PLAN_PARM ) == null;
            try {
                communityUri = URLDecoder.decode( encodedCommunityUri, "UTF-8" );
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "Failed to decode community uri", e );
            }
        }
        if ( communityUri != null ) {
            planCommunity = planCommunityManager.getPlanCommunity( communityUri );
        }
        return planCommunity;
    }

    /**
     * Set plan from uri parameters. Plan version is optional.
     *
     * @param parameters the parameters
     */
    protected void setPlanFromParameters( PageParameters parameters ) {
        Plan plan = getPlanFromParameters( planManager, user, parameters );
        setPlan( plan );
    }

    static public Plan getPlanFromParameters(
            PlanManager planManager,
            ChannelsUser user,
            PageParameters parameters ) {
        Plan plan = null;
        String encodedPlanUri = parameters.get( PLAN_PARM ).toString( null );
        if ( encodedPlanUri == null ) {
            // assert parameters.get( COMMUNITY_PARM ) == null;
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
        int planVersion = 0; // = unspecified plan version  - development for planners, if any, otherwise production
        try {
            if ( parameters.getNamedKeys().contains( VERSION_PARM ) )
                planVersion = parameters.get( VERSION_PARM ).toInt( 0 );
        } catch ( StringValueConversionException ignored ) {
            LOG.warn( "Bad plan version in url (not an integer)" );
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not found" );
        }

        List<Plan> plans = planManager.getPlansWithUri( planUri );
/*
        if ( plans.isEmpty() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
*/

        for ( Iterator<Plan> it = plans.iterator(); it.hasNext() && plan == null; ) {
            Plan p = it.next();
            if ( planVersion == 0 ) {  // unspecified version
                if ( p.isDevelopment() && user.isPlanner( planUri ) ) {
                    plan = p;
                } else if ( p.isProduction() && !user.isPlanner( planUri ) ) {
                    plan = p;
                }
            } else {
                if ( planVersion == p.getVersion() ) {
                    if ( p.isProduction() || ( p.isDevelopment() && user.isPlanner( planUri
                    ) ) )
                        plan = p;
                }
            }
        }
        // If version mismatch, grab the production plan, if any
        if ( planUri != null && !planUri.isEmpty() && plan == null ) {
            plan = planManager.findProductionPlan( planUri );
        }
        // if still no plan, panic and grab first one.
        if ( plan == null ) {
            if ( plans.isEmpty() ) {
                plans = planManager.getPlans();
            }
            LOG.warn( "PANIC: selecting first plan" );
            plan = plans.get( 0 );
        }
        return plan;
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
        if ( isCommunityContext() ) {
            return planManager.getPlan( planCommunity.getPlanUri(), planCommunity.getPlanVersion() );
        } else {
            if ( plan == null ) {
                setPlanFromParameters( getPageParameters() );
            }
            return plan;
        }
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
            queryService = getPlanService();
//            queryService = commanderFactory.getCommander( plan ).getQueryService();
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

    protected void userLeftPlanCommunity() {
        if ( getPlanCommunity() != null ) getCommander().userLeftCommunity( getUser().getUsername() );
    }

    protected CommunityService getCommunityService() {
        return communityServiceFactory.getService( getPlanCommunity() );
    }


    // Modalable

    public void addModalDialog( String id, String cookieName, MarkupContainer container ) {
        dialogWindow = new ModalWindow( id ) {
            @Override
            protected ResourceReference newCssResource() {
                return null;
            }
        };
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

    protected String makeHomeUrl() {
        return getApp().getServerUrl() + "/home";
    }

    protected Place getPlanLocale() {
        return getPlanService().getPlanLocale();
    }

    protected Component addTipTitle( Component component, String title ) {
        return addTipTitle( component, new Model<String>( title ) );
    }

    protected Component addTipTitle( Component component, IModel<String> titleModel ) {
        component.add( new AttributeModifier( "title", titleModel ) );
        component.add( new TipTipBehavior( new TipTipOptions().maxWidth( "400px" ) ) );
//        component.add( new TipTipBehavior( new TipTipOptions().keepAlive( true ) ) );
        return component;
    }


}
