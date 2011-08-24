package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.command.commands.CreateEntityIfNew;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Abstract reports index page on participants and agents. Copyright (C) 2008 Mind-Alliance Systems. All Rights
 * Reserved. Proprietary and Confidential. User: jf Date: 7/28/11 Time: 9:54 AM
 */
abstract public class AbstractAllParticipantsPage extends WebPage {

    protected static final String PLAN = "plan";

    protected static final String VERSION = "v";

    @SpringBean
    protected User user;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private Commander commander;

    @SpringBean
    private LockManager lockManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    private String uri;

    private int version;

    private List<Participation> users;

    private List<Actor> actors;

    private List<User> unassigned;

    public AbstractAllParticipantsPage( Class<? extends AbstractParticipantPage> clazz ) {

        List<Plan> plans = planManager.getPlannablePlans( user );
        if ( plans.isEmpty() )
            throw new AbortWithWebErrorCodeException( SC_FORBIDDEN );

        Plan plan = plans.get( 0 );
        PageParameters pageParameters = new PageParameters();
        pageParameters.put( PLAN, plan.getUri() );
        pageParameters.put( VERSION, plan.getVersion() );

        setRedirect( true );
        setResponsePage( clazz, pageParameters );
    }

    public AbstractAllParticipantsPage( PageParameters parameters ) {
        super( parameters );

        try {
            if ( parameters.containsKey( PLAN ) && parameters.containsKey( VERSION ) ) {
                Plan plan = planManager.getPlan( parameters.getString( PLAN ), parameters.getInt( VERSION ) );

                if ( plan == null )
                    throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );

                if ( !user.isPlanner( plan.getUri() ) )
                    throw new AbortWithWebErrorCodeException( SC_FORBIDDEN );

                init( createService( plan ), plan );
            }
        } catch ( StringValueConversionException ignored ) {
            throw new AbortWithWebErrorCodeException( SC_NOT_FOUND );
        }
    }

    protected User getUser() {
        return user;
    }

    protected String getUri() {
        return uri;
    }

    protected int getVersion() {
        return version;
    }

    protected List<Participation> getUsers() {
        return users;
    }

    protected List<Actor> getActors() {
        return actors;
    }

    protected List<User> getUnassigned() {
        return unassigned;
    }

    protected PlanManager getPlanManager() {
        return planManager;
    }

    protected Commander getCommander() {
        return commander;
    }

    protected LockManager getLockManager() {
        return lockManager;
    }

    protected PlanService createService( Plan plan ) {
        return new PlanService( planManager, null, planManager.getUserService(), plan, attachmentManager );
    }

    private void init( PlanService service, final Plan plan ) {
        uri = plan.getUri();
        version = plan.getVersion();

        users = validate( planManager.getUserService(), service.list( Participation.class ) );

        actors = findFreeActors( findAssignedActors( users ), service.getAssignments().getActualActors() );

        unassigned = findUnassignedUsers( service );
        initComponents( service, plan );
    }

    abstract protected void initComponents( PlanService service, final Plan plan );

    protected void addChannelsLogo() {
        WebMarkupContainer channels_logo = new WebMarkupContainer( "channelsHome" );
        channels_logo.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String homeUrl = AbstractChannelsWebPage.redirectUrl( "home", getPlan() );
                RedirectPage page = new RedirectPage( homeUrl );
                setResponsePage( page );
            }
        } );
        add( channels_logo );
    }

    protected Plan getPlan() {
        return user.getPlan();
    }

    protected List<User> findUnassignedUsers( PlanService service ) {
        String uri = service.getPlan().getUri();
        Collection<User> inputCollection = service.getUserService().getUsers( uri );
        List<User> answer = new ArrayList<User>( inputCollection.size() );
        for ( User u : inputCollection ) {
            Participation participation = service.findParticipation( u.getUsername() );
            if ( participation == null || participation.getActor() == null )
                answer.add( u );
        }

        Collections.sort( answer, new Comparator<User>() {
            @Override
            public int compare( User o1, User o2 ) {
                return o1.getFullName().compareTo( o2.getFullName() );
            }
        } );
        return answer;
    }

    protected List<Actor> findFreeActors( Set<Actor> assigned, Collection<Actor> actors ) {
        List<Actor> answer = new ArrayList<Actor>( actors.size() );
        for ( Actor a : actors )
            if ( !a.isUnknown() && !assigned.contains( a ) )
                answer.add( a );

        Collections.sort( answer, new Comparator<Actor>() {
            @Override
            public int compare( Actor o1, Actor o2 ) {
                return o1.getNormalizedName().compareToIgnoreCase( o2.getNormalizedName() );
            }
        } );
        return answer;
    }

    protected Set<Actor> findAssignedActors( List<Participation> users ) {
        Set<Actor> result = new HashSet<Actor>( users.size() );
        for ( Participation p : users ) {
            Actor actor = p.getActor();
            if ( actor.isSingular() )
                result.add( actor );
        }

        return result;
    }

    /**
     * Filter invalid participations.
     *
     * @param userService to check if users are still valid
     * @param participations the participations to check
     * @return filtered list
     */
    protected static List<Participation> validate(
            UserService userService, Collection<Participation> participations ) {

        List<Participation> answer = new ArrayList<Participation>( participations.size() );
        for ( Participation item : participations ) {
            String userName = item.getUsername();
            if ( item.getActor() != null && userName != null && userService.getUserNamed( userName ) != null )
                answer.add( item );
        }

        return answer;
    }

    /**
     * Commander needs some tending to prior to use.
     *
     * @param plan a plan
     * @return a commander
     */
    protected Commander getCommander( Plan plan ) {
        // Adjust so commander actually behave as expected
        user.setPlan( plan );
        PlanService service = createService( plan );
        commander.setPlanDao( service.getDao() );
        commander.setLockManager( lockManager );

        return commander;
    }

    protected static Participation findParticipation( Commander cmdr, String username ) {
        QueryService planService = cmdr.getQueryService();
        Participation participation = planService.findParticipation( username );
        if ( participation == null ) {
            Participation newPart = (Participation) cmdr
                    .doUnsafeCommand( new CreateEntityIfNew( Participation.class, username, ModelEntity.Kind.Actual ) )
                    .getSubject( planService );

            newPart.setActual();
            return newPart;
        } else
            return participation;
    }
}
