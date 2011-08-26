package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.attachments.AttachmentManager;
import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.engine.command.Commander;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.engine.nlp.SemanticMatcher;
import com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage;
import com.mindalliance.channels.pages.reports.infoNeeds.InfoNeedsPage;
import com.mindalliance.channels.engine.query.PlanService;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
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
 * Abstract Channels Web Page Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary and
 * Confidential. User: jf Date: 3/3/11 Time: 12:27 PM
 */
public class AbstractChannelsWebPage extends WebPage implements Updatable {

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
    ImagingService imagingService;

    @SpringBean
    private AttachmentManager attachmentManager;

    private Plan plan;

    @SpringBean
    private PlanManager planManager;

    private transient QueryService queryService;

    @SpringBean
    private SemanticMatcher semanticMatcher;

    @SpringBean
    private User user;

    @SpringBean
    private UserService userService;

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
            link.setParameter( PLAN_PARM, URLEncoder.encode( plan.getUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
        link.setParameter( VERSION_PARM, plan.getVersion() );
    }

    @Override
    public void changed( Change change ) {
        // do nothing
    }

    //-----------------------------------
    public static PageParameters createParameters( Specable profile, String uri, int version ) {

        PageParameters result = new PageParameters();
        result.put( "plan", uri );
        result.put( "v", version );
        if ( profile != null ) {
            if ( profile.getActor() != null )
                result.put( "agent", profile.getActor().getId() );
            if ( profile.getRole() != null )
                result.put( "role", profile.getRole().getId() );
            if ( profile.getOrganization() != null )
                result.put( "org", profile.getOrganization().getId() );
            if ( profile.getJurisdiction() != null )
                result.put( "place", profile.getJurisdiction().getId() );
        }
        return result;
    }

    protected Channels getApp() {
        return (Channels) getApplication();
    }

    protected Commander getCommander() {
        // return Channels.instance().getCommander();
        return getApp().getCommander( getPlan() );
    }

    public static BookmarkablePageLink<? extends WebPage> getGuidelinesLink(
            String id, QueryService queryService, Plan plan, User user, boolean samePage ) {

        Actor actor = findActor( queryService, user.getUsername() );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        BookmarkablePageLink<? extends WebPage> guidelinesLink = newTargetedLink( id,
                                                                                  "",
                                                                                  GuidelinesPage.class,
                                                                                  GuidelinesPage.createParameters(
                                                                                          planner ? null : actor,
                                                                                          uri,
                                                                                          plan.getVersion() ),
                                                                                  null,
                                                                                  plan );
        if ( !samePage )
            guidelinesLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return guidelinesLink;
    }

    private static Actor findActor( QueryService queryService, String userName ) {
        Participation participation = queryService.findParticipation( userName );
        return participation != null && participation.getActor() != null ? participation.getActor() : null;
    }
    //---------------- getGuidelinesLink

    public static BookmarkablePageLink<? extends WebPage> getInfoNeedsLink(
            String id, QueryService queryService, Plan plan, User user, boolean samePage ) {
        Actor actor = findActor( queryService, user.getUsername() );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        BookmarkablePageLink<? extends WebPage> infoNeedsLink = newTargetedLink( id,
                                                                                 "",
                                                                                 InfoNeedsPage.class,
                                                                                 InfoNeedsPage.createParameters( planner ? null : actor,
                                                                                                                 uri,
                                                                                                                 plan.getVersion() ),
                                                                                 null,
                                                                                 plan );
        if ( !samePage )
            infoNeedsLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return infoNeedsLink;
    }

    @Override
    public QueryService getOwnQueryService() {
        return getQueryService();
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
                result.put( PLAN_PARM, URLEncoder.encode( plan.getUri(), "UTF-8" ) );
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "Failed to url-encode plan uri " + plan.getUri(), e );
            }
            result.put( VERSION_PARM, Integer.toString( plan.getVersion() ) );
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
            Actor actor = parameters.containsKey( "agent" ) ? service.find( Actor.class, parameters.getLong( "agent" ) )
                                                            : null;
            Role role =
                    parameters.containsKey( "role" ) ? service.find( Role.class, parameters.getLong( "role" ) ) : null;
            Organization organization =
                    parameters.containsKey( "org" ) ? service.find( Organization.class, parameters.getLong( "org" ) )
                                                    : null;
            Place jurisdiction =
                    parameters.containsKey( "place" ) ? service.find( Place.class, parameters.getLong( "place" ) )
                                                      : null;
            return new ResourceSpec( actor, role, organization, jurisdiction );
        } catch ( StringValueConversionException ignored ) {
            throw new NotFoundException();
        }
    }

    //-----------------------------------
    public static ResourceSpec getProfile( QueryService service, User user ) throws NotFoundException {

        Participation participation = service.findParticipation( user.getUsername() );
        if ( participation == null || participation.getActor() == null )
            throw new NotFoundException();

        return new ResourceSpec( participation.getActor(), null, null, null );
    }

    private PlanService getQueryService( Plan plan ) {
        return new PlanService( planManager, semanticMatcher, userService, plan, attachmentManager );
    }

    protected String getSupportCommunity() {
        Plan plan = User.current().getPlan();
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
            params.put( PLAN_PARM, URLEncoder.encode( plan.getUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
        params.put( VERSION_PARM, plan.getVersion() );
        return params;
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible a boolean
     */
    protected static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", true, new Model<String>( visible ? "" : "display:none" ) ) );
    }

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id, String target, Class<T> pageClass, PopupSettings popupSettings, Plan plan ) {

        BookmarkablePageLink<T> link = new BookmarkablePageLink<T>( id, pageClass );
        addPlanParameters( link, plan );
        link.add( new AttributeModifier( "target", true, new Model<String>( target ) ) );
        if ( popupSettings != null )
            link.setPopupSettings( popupSettings );

        return link;
    }

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id, String target, Class<T> pageClass, PageParameters parameters, PopupSettings popupSettings,
            Plan plan ) {

        BookmarkablePageLink<T> link = newTargetedLink( id, target, pageClass, popupSettings, plan );
        for ( String name : parameters.keySet() ) {
            link.setParameter( name, "" + parameters.get( name ) );
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

    public static String redirectUrl( String path, Plan p ) {
        return path + "?" + queryParameters( p );
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
        String encodedPlanUri = parameters.getString( PLAN_PARM, null );
        if ( encodedPlanUri == null ) {
            try {
                encodedPlanUri = URLEncoder.encode( user.getPlanUri(), "UTF-8" );
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
            planVersion = parameters.getInt( VERSION_PARM, 0 );
        } catch ( StringValueConversionException ignored ) {
            LOG.warn( "Bad value in url" );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
        }

        List<Plan> plans = getPlans();
        if ( plans.isEmpty() )
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );

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
        if ( !getPlans().contains( plan ) )
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );
        User.current().setPlan( plan );
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
        if ( plan == null ) {
            setPlanFromParameters( getPageParameters() );
        }
        return plan;
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    public final QueryService getQueryService() {
        if ( queryService == null ) {
            queryService = getQueryService( plan );
        }
        return queryService;
    }

    public SemanticMatcher getSemanticMatcher() {
        return semanticMatcher;
    }

    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService( UserService userService ) {
        this.userService = userService;
    }
}
