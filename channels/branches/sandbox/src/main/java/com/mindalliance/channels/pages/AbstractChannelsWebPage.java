package com.mindalliance.channels.pages;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.nlp.SemanticMatcher;
import com.mindalliance.channels.pages.responders.ResponderPage;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
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
 * Abstract Channels Web Page
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/3/11
 * Time: 12:27 PM
 */
public class AbstractChannelsWebPage extends WebPage implements Updatable {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractChannelsWebPage.class );

    public static final String PLAN_PARM = "plan";
    public static final String VERSION_PARM = "v";
    /**
     * Delay between refresh check callbacks.
     */
    public static final int REFRESH_DELAY = 10;

    @SpringBean
    private User user;


    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    ImagingService imagingService;

    @SpringBean
    private UserService userService;

    @SpringBean
    private SemanticMatcher semanticMatcher;


    private Plan plan;

    private transient QueryService queryService;

    public AbstractChannelsWebPage() {
    }

    public AbstractChannelsWebPage( PageParameters parameters ) {
        super( parameters );
        setPlanFromParameters( parameters );
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
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

    public UserService getUserService() {
        return userService;
    }

    public void setUserService( UserService userService ) {
        this.userService = userService;
    }

    public SemanticMatcher getSemanticMatcher() {
        return semanticMatcher;
    }

    public void setSemanticMatcher( SemanticMatcher semanticMatcher ) {
        this.semanticMatcher = semanticMatcher;
    }

    public static void addPlanParameters( BookmarkablePageLink link, Plan plan ) {
        try {
            link.setParameter( PLAN_PARM, URLEncoder.encode( plan.getUri(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            LOG.error( "Failed to encode uri", e );
        }
        link.setParameter( VERSION_PARM, plan.getVersion() );
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

    public boolean isPlanner() {
        return getUser().isPlanner( plan.getUri() );
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
                LOG.error( "Failed to encode plan uri", e);
                encodedPlanUri = "";
            }
        }
        String planUri = "";
        try {
            planUri = URLDecoder.decode( encodedPlanUri, "UTF-8");
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to decode plan uri", e);
        }
        int planVersion = parameters.getInt( VERSION_PARM, 0 );

        List<Plan> plans = getPlans();
        if ( plans.isEmpty() )
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );

        for ( Iterator<Plan> it = plans.iterator(); it.hasNext() && plan == null; ) {
            Plan p = it.next();
            if ( planUri.equals( p.getUri() ) ) {
                if ( getUser().isPlanner( p.getUri() ) ) {
                    if ( planVersion == p.getVersion() || p.isDevelopment() )
                        plan = p;
                } else if ( p.isProduction() )
                    plan = p;
            }
        }

        if ( plan == null ) {
            LOG.warn( "PANIC: selecting first plan");
            plan = plans.get( 0 );
        }
        if ( !getPlans().contains( plan ) )
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );
        User.current().setPlan( plan );
        getQueryService();
    }

    public void setPlan( Plan plan ) {
        this.plan = plan;
        getUser().setPlan( plan );
        queryService = null;
    }

    @Override
    public QueryService getOwnQueryService() {
        return getQueryService();
    }

    public final QueryService getQueryService() {
        if ( queryService == null )
            queryService = getQueryService( plan );
        return queryService;
    }

    private PlanService getQueryService( Plan plan ) {
        return new PlanService(
                getPlanManager(), getSemanticMatcher(),
                getUserService(),
                plan );
    }

    /**
     * Get all plans that the current can read.
     *
     * @return a list of plans
     */
    public final List<Plan> getPlans() {
        List<Plan> result = new ArrayList<Plan>();
        for ( Plan p : getPlanManager().getReadablePlans( getUser() ) ) {
            String uri = p.getUri();
            if ( getUser().isPlanner( uri ) )
                result.add( p );
            else if ( getUser().isParticipant( uri ) ) {
                if ( p.isProduction() )
                    result.add( p );
            }
        }
        return result;
    }

    public Plan getPlan() {
        return plan;
    }

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id,
            String target,
            Class<T> pageClass,
            PopupSettings popupSettings,
            Plan plan ) {

        BookmarkablePageLink<T> link = new BookmarkablePageLink<T>( id, pageClass );
        addPlanParameters( link, plan );
        link.add( new AttributeModifier( "target", true, new Model<String>( target ) ) );
        if ( popupSettings != null )
            link.setPopupSettings( popupSettings );

        return link;
    }

    public static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id,
            String target,
            Class<T> pageClass,
            PageParameters parameters,
            PopupSettings popupSettings,
            Plan plan ) {
         BookmarkablePageLink<T> link = newTargetedLink(
                 id,
                 target,
                 pageClass,
                 popupSettings,
                 plan
         );
        for ( String name : parameters.keySet() ) {
            link.setParameter( name, "" + parameters.get( name ) );
        }
        return link;
    }

    public static String redirectUrl( String path, Plan p ) {
        return path + "?" + queryParameters( p );
    }

    public static String queryParameters( Plan p ) {
        String query = "";
        try {
            query = MessageFormat.format( "&plan={0}&v={1,number,0}",
                            URLEncoder.encode( p.getUri(), "UTF-8"),
                            p.getVersion() );
        } catch ( UnsupportedEncodingException e ) {
            LOG.error( "Failed to encode plan uri", e);
        }
        return query;
    }

    protected Channels getApp() {
        return (Channels) getApplication();
    }

    protected Commander getCommander() {
        // return Channels.instance().getCommander();
        return getApp().getCommander( getPlan() );
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", true, new Model<String>(
                visible ? "" : "display:none" ) ) );
    }

    protected String getSupportCommunity() {
        Plan plan = User.current().getPlan();
        if ( plan != null ) {
            return  plan.getPlannerSupportCommunity( planManager.getDefaultSupportCommunity() );
        } else {
            return planManager.getDefaultSupportCommunity();
        }
    }

    public static BookmarkablePageLink<? extends WebPage > getGuidelinesLink(
            String id,
            QueryService queryService,
            Plan plan,
            User user,
            boolean samePage ) {
        Actor actor = findActor( queryService, user.getUsername() );
        String uri = plan.getUri();
        boolean planner = user.isPlanner( uri );
        BookmarkablePageLink<? extends WebPage> guidelinesLink = AbstractChannelsWebPage.newTargetedLink(
                id,
                "",
                ResponderPage.class,
                ResponderPage.createParameters( planner ? null : actor, uri, plan.getVersion() ),
                null,
                plan );
        if ( !samePage )
            guidelinesLink.add( new AttributeModifier( "target", new Model<String>( "_blank" ) ) );
        return guidelinesLink;
    }

    private static Actor findActor( QueryService queryService, String userName ) {
        Participation participation = queryService.findParticipation( userName );
        return participation != null && participation.getActor() != null
                ? participation.getActor()
                : null;
    }


    @Override
    public void changed( Change change ) {
        // do nothing
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        // do nothing
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
        // do nothing
    }

}
