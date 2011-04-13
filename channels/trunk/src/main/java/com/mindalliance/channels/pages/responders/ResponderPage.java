// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.responders;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RedirectToUrlException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * The responder report.  This page is different for every user.
 */

public class ResponderPage extends WebPage {

    private static final Logger LOG = LoggerFactory.getLogger( ResponderPage.class );

    /** The current logged-in user. */
    @SpringBean
    private User user;

    /** The keeper of all plans. */
    @SpringBean
    private PlanManager planManager;

    /** Required for getting information on other users. */
    @SpringBean
    private UserService userService;

    /**
     * Called for access without parameters.
     * Find the actor and plan corresponding to the current user and redirect to that page.
     * Otherwise, redirect to access denied.
     */
    public ResponderPage() {

        try {
            PlanService service = createPlanService( user );
            Plan plan = service.getPlan();

            setRedirect( true );
            setResponsePage(
                ResponderPage.class,
                createParameters( user.isPlanner( plan.getUri() ) ? new ResourceSpec()
                                                                  : getProfile( service, user ),
                                  plan.getUri(), plan.getVersion() ) );

        } catch ( NotFoundException e ) {
            // User has no responder page
            LOG.info( user.getFullName() + " not a responder", e );
            throw new RedirectToUrlException( "/static/nonResponder.html" );
        }
    }

    public ResponderPage( PageParameters parameters ) {
        super( parameters );

        try {
            String uri = parameters.getString( "plan" );

            if ( user.isPlanner( uri ) && parameters.size() == 2 ) {
                setRedirect( false );
                setResponsePage( AllResponders.class, parameters );
            } else {
                PlanService service = createPlanService( uri, parameters.getInt( "v" ) );
                init( service, getProfile( service, parameters ) );
            }

        } catch ( StringValueConversionException e ) {
            LOG.info( "Bad parameter: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

        } catch ( NotFoundException e ) {
            LOG.info( "Not found: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    private void init( PlanService service, ResourceSpec profile ) {
        add(
            new Label( "userName", user.getUsername() ),
            new Label( "personName", profile.displayString( 256 ) ),
            new Label( "planDescription", service.getPlan().getDescription() )
        );

    }

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

    private static ResourceSpec getProfile( PlanService service, PageParameters parameters )
        throws NotFoundException {

        // TODO check read permission

        try {
            Actor actor = parameters.containsKey( "agent" ) ?
                        service.find( Actor.class, parameters.getLong( "agent" ) ) : null;
            Role role = parameters.containsKey( "role" ) ?
                        service.find( Role.class, parameters.getLong( "role" ) ) : null;
            Organization organization = parameters.containsKey( "org" ) ?
                        service.find( Organization.class, parameters.getLong( "org" ) ) : null;
            Place jurisdiction = parameters.containsKey( "place" ) ?
                        service.find( Place.class, parameters.getLong( "place" ) ) : null;

            return new ResourceSpec( actor, role, organization, jurisdiction );

        } catch ( StringValueConversionException ignored ) {
            throw new NotFoundException();
        }
    }

    private static ResourceSpec getProfile( PlanService service, User user )
        throws NotFoundException {

        Participation participation = service.findParticipation( user.getUsername() );
        if ( participation == null || participation.getActor() == null )
            throw new NotFoundException();

        return new ResourceSpec( participation.getActor(), null, null, null );
    }

    /**
     * Find a plan service for a user.
     * @param user the logged in user
     * @return a service for the first readable plan in which the user is an active participant or a
     * planner.
     * @throws NotFoundException when no adequate plan was found
     */
    private PlanService createPlanService( User user ) throws NotFoundException {
        for ( Plan readablePlan : planManager.getReadablePlans( user ) ) {
            PlanService service = new PlanService( planManager, null, userService, readablePlan );

            if ( user.isPlanner( readablePlan.getUri() )
                 || service.findParticipation( user.getUsername() ) != null )
                return service;
        }

        throw new NotFoundException();
    }

    private PlanService createPlanService( Plan plan ) throws NotFoundException {
        return createPlanService( plan.getUri(), plan.getVersion() );
    }

    private PlanService createPlanService( String uri, int version ) throws NotFoundException {
        for ( Plan plan : planManager.getPlansWithUri( uri ) )
            if ( plan.getVersion() == version )
                return new PlanService( planManager, null, userService, plan );

        throw new NotFoundException();
    }
}
