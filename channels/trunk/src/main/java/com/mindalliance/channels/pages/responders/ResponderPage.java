// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.pages.responders;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.EventPhase;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RedirectToUrlException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
                PlanService service = initPlanService( uri, parameters.getInt( "v" ) );
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

    private PlanService initPlanService( final String uri, final int version )
        throws NotFoundException {

        if ( planManager.getPlan( uri, version ) == null )
            throw new NotFoundException();

        setDefaultModel( new LoadableDetachableModel<PlanService>() {
            @Override
            protected PlanService load() {
                return new PlanService( planManager, null, userService,
                                        planManager.getPlan( uri, version ) );
            }
        } );

        return (PlanService) getDefaultModelObject();
    }

    private PlanService getPlanService() {
        return (PlanService) getDefaultModelObject();
    }

    private void init( PlanService service, ResourceSpec profile ) {

        Plan plan = service.getPlan();
        final Assignments myAssignments = service.getAssignments( true ).with( profile );
        final Assignments directAssignments = myAssignments.notFrom( profile );

        List<EventPhase> eventPhases = directAssignments.getEventPhases();

        final List<User> planners = service.getUserService().getPlanners( plan.getUri() );

        add(
            new Label( "userName", user.getUsername() ),
            new Label( "personName", profile.displayString( 256 ) ),
            new ListView<User>( "planners", planners ) {
                 @Override
                 protected void populateItem( ListItem<User> item ) {
                     User u = item.getModelObject();
                     item.add(
                         new ExternalLink( "planner", "mailTo:" + u.getEmail(), u.getFullName() ),
                         new Label( "listSep",
                                    item.getIndex() == getViewSize() - 1 ?  ". "
                                  : item.getIndex() == getViewSize() - 2 ?  " or " : ", " )
                            .setRenderBodyOnly( true )
                         );
                 }
            },

            new Label( "planName", plan.getName() ),
            new Label( "planDescription", plan.getDescription() ),

             new ListView<EventPhase>( "phaseLinks", eventPhases ) {
                 @Override
                 protected void populateItem( ListItem<EventPhase> item ) {
                     item.add(
                         new WebMarkupContainer( "phaseLink" )
                            .add( new Label( "phaseLinkText", item.getModelObject().toString() ) )
                            .add( new AttributeModifier( "href", true,
                                        new Model<String>( "#ep_" + item.getIndex() ) ) )
                     );
                 }
             },

             new ListView<EventPhase>( "phases", eventPhases ) {
                 @Override
                 protected void populateItem( ListItem<EventPhase> item ) {
                     EventPhase eventPhase = item.getModelObject();
                     Assignments phaseEventAssignments = myAssignments.with( eventPhase );

                     List<Part> routines = phaseEventAssignments.getImmediates( getPlanService() )
                                            .getParts();
                     List<Part> routines1 = phaseEventAssignments.getOptionals( getPlanService() )
                                          .getParts();
                     item.add(
                         new WebMarkupContainer( "phaseAnchor" )
                             .add( new Label( "phaseText", eventPhase.toString() ) )
                             .add( new AttributeModifier( "name", true, new Model<String>(
                                 "ep_" + item.getIndex() ) ) ),

                         new Label( "eventDesc", eventPhase.getEvent().getDescription() )
                             .setVisible( !eventPhase.getEvent().getDescription().isEmpty() ),
                         new Label( "phaseDesc", eventPhase.getPhase().getDescription() )
                             .setVisible( !eventPhase.getPhase().getDescription().isEmpty() ),

                         new WebMarkupContainer( "routineDiv" )
                             .add( newTaskLinks( routines ) )
                             .setVisible( !routines.isEmpty() ),

                         newNotifSection( phaseEventAssignments
                                              .getNotifications( getPlanService() ) ),

                         newRfiSection( phaseEventAssignments.getRequests() ),

                         new WebMarkupContainer( "otherDiv" )
                             .add( newTaskLinks( routines1 ) )
                             .setVisible( !routines1.isEmpty() ),

                         newDocSection( getAttachments( planManager.getAttachmentManager(),
                                                        phaseEventAssignments.getSegments() ) )

                     );
                 }
             },

             new ListView<Assignment>( "tasks", myAssignments.getAssignments() ) {
                 @Override
                 protected void populateItem( ListItem<Assignment> item ) {
                     Assignment a = item.getModelObject();
                     Part part = a.getPart();
                     PlanService planService = getPlanService();
                     List<Part> subtasks = myAssignments.from( a ).getParts();
                     item.add(
                         new WebMarkupContainer( "taskAnchor" )
                               .add( new Label( "taskName", part.getTask() ) )
                               .add( new AttributeModifier( "name", true,
                                            new Model<String>( "t_" + part.getId() ) ) ),
                         new WebMarkupContainer( "routineTask" )
                            .setVisible( Assignments.isImmediate( part, planService ) ),
                         new WebMarkupContainer( "notifTask" )
                            .setVisible( Assignments.isNotification( part, planService ) ),
                         new WebMarkupContainer( "reqTask" )
                            .setVisible( Assignments.isRequest( part ) ),
                         new WebMarkupContainer( "subTask" )
                            .setVisible( Assignments.isOptional( part, planService ) ),
                         new WebMarkupContainer( "prohibited" )
                            .setVisible( part.isProhibited() ),
                         new WebMarkupContainer( "term1" )
                            .setVisible( part.isStartsWithSegment() ),
                         new WebMarkupContainer( "term2" )
                            .setVisible( isTerminated( part, true ) ),
                         new WebMarkupContainer( "term3" )
                            .setVisible( isTerminated( part, false ) ),
                         new WebMarkupContainer( "canStart" )
                            .setVisible( false ),
                         new WebMarkupContainer( "canStop" )
                            .setVisible( false ),
                         new ListView<Goal>( "risks", getRisks( part ) ) {
                             @Override
                             protected void populateItem( ListItem<Goal> item ) {
                                 item.add(
                                     new Label( "type", item.getModelObject().getFullTitle() )
                                 );
                             }
                         },
                         new ListView<Goal>( "gains", getGains( part ) ) {
                             @Override
                             protected void populateItem( ListItem<Goal> goalItem ) {
                                 goalItem.add(
                                     new Label( "type", goalItem.getModelObject().getFullTitle() )
                                 );
                             }
                         },
                         new WebMarkupContainer( "conseqs" )
                            .setVisible( false ),
                         new WebMarkupContainer( "superNote" )
                            .setVisible( false ),

                         new WebMarkupContainer( "teamDiv" )
                            .setVisible( part.isAsTeam() ),

                         new WebMarkupContainer( "criticalDiv" )
                            .setVisible( !part.getEssentialFlows( false, planService ).isEmpty() ),
                         new WebMarkupContainer( "inputDiv" )
                            .setVisible( !getInputDiv( part, planService ).isEmpty() ),
                         new WebMarkupContainer( "distribDiv" )
                            .setVisible( !getDistribDiv( part ).isEmpty() ),
                         new WebMarkupContainer( "taskRfiDiv" )
                            .setVisible( !getRfiDiv( part ).isEmpty() ),
                         new WebMarkupContainer( "subtaskDiv" )
                            .add( newTaskLinks( subtasks ) )
                            .setVisible( !subtasks.isEmpty() ),
                         new WebMarkupContainer( "failDiv" )
                            .setVisible( !getFailDiv( part ).isEmpty() ),

                         newDocSection( planService.getAttachmentManager().getMediaReferences( part ) )


                     );
                 }
             }


        );
    }

    private List<Flow> getInputDiv( Part part, PlanService planService ) {
        Set<Flow> essentials = new HashSet<Flow>(
            planService.findEssentialFlowsFrom( part, false ) );
        List<Flow> others = new ArrayList<Flow>();
        Iterator<Flow> flows = part.flows();
        while ( flows.hasNext() ) {
            Flow next = flows.next();
            if ( !essentials.contains( next ) )
                others.add( next );
        }

        return others;
    }

    private List<Flow> getDistribDiv( Part part ) {
        List<Flow> result = new ArrayList<Flow>();
        Iterator<Flow> flows = part.sends();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( !flow.isAskedFor() && !flow.isIfTaskFails() )
                result.add( flow );
        }

        return result;
    }

    private List<Flow> getFailDiv( Part part ) {
        List<Flow> result = new ArrayList<Flow>();
        Iterator<Flow> flows = part.sends();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( !flow.isAskedFor() && flow.isIfTaskFails() )
                result.add( flow );
        }

        return result;
    }

    private List<Flow> getRfiDiv( Part part ) {
        List<Flow> result = new ArrayList<Flow>();
        Iterator<Flow> flows = part.sends();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isAskedFor() )
                result.add( flow );
        }

        return result;
    }

    private static List<Goal> getGains( Part part ) {
        Collection<Goal> inputCollection = part.getGoals();
        List<Goal> answer = new ArrayList<Goal>( inputCollection.size());
        for ( Goal item : inputCollection )
            if ( item.isPositive() )
                answer.add( item );

        return answer;
    }

    private static List<Goal> getRisks( Part part ) {
        Collection<Goal> inputCollection = part.getGoals();
        List<Goal> answer = new ArrayList<Goal>( inputCollection.size());
        for ( Goal item : inputCollection )
            if ( !item.isPositive() )
                answer.add( item );

        return answer;
    }

    private static boolean isTerminated( Part part, boolean request ) {
        boolean terminated = false;
        if ( request ) {
            Iterator<Flow> receives = part.receives();
            while ( !terminated && receives.hasNext() ) {
                Flow receive = receives.next();
                terminated = receive.isTerminatingToTarget();
            }
        } else {
            Iterator<Flow> sends = part.sends();
            while ( !terminated && sends.hasNext() ) {
                Flow send = sends.next();
                terminated = send.isTerminatingToSource();
            }
        }

        return terminated;
    }

    private static List<Attachment> getAttachments(
        AttachmentManager attachmentManager, List<Segment> segments ) {

        List<Attachment> attachments = new ArrayList<Attachment>();
        for ( Segment segment : segments ) {
            attachments.addAll( attachmentManager.getMediaReferences( segment ) );
            attachments.addAll( attachmentManager.getMediaReferences( segment.getEvent() ) );
            attachments.addAll( attachmentManager.getMediaReferences( segment.getPhase() ) );
        }
        return attachments;
    }

    private Component newNotifSection( final Assignments notifications ) {

        return new WebMarkupContainer( "notDiv" )
           .add( new ListView<Assignment>( "notLinks", notifications.getAssignments() ) {
               @Override
               protected void populateItem( ListItem<Assignment> item ) {
                   Assignment a = item.getModelObject();
                   final Part part = a.getPart();
                   item.add(
                       new ListView<Flow>( "flow", getTriggeringFlows( part ) ) {
                           @Override
                           protected void populateItem( ListItem<Flow> flowListItem ) {
                               Flow flow = flowListItem.getModelObject();
                               flowListItem.add(
                                   new Label( "flowName", flow.getName() ),
                                   new ListView<Specable>( "sources", getSources( flow ) ) {
                                       @Override
                                       protected void populateItem( ListItem<Specable> specItem ) {
                                           Specable specable = specItem.getModelObject();
                                           int size = getViewSize();
                                           int index = specItem.getIndex();
                                           specItem.add( new Label( "source", specable.toString() ),
                                                         new Label( "sourceSep",
                                                                    index == size - 1 ? "" :
                                                                    index == size - 2 ? " or "
                                                                                      : ", " )
                                                             .setRenderBodyOnly( true )
                                           ).setRenderBodyOnly( true );


                                       }
                                   }.setRenderBodyOnly( true ),
                                   new Label( "flowSep",
                                              flowListItem.getIndex() == getViewSize() - 1 ? ". "
                                            : flowListItem.getIndex() == getViewSize() - 2 ? " or "
                                                                                           : ", " )
                                        .setRenderBodyOnly( true )
                               );
                           }
                       },
                       newTaskLink( part ) );
               }
           } )
           .setVisible( !notifications.isEmpty() );
    }

    private static Component newTaskLink( Part part ) {
        return new WebMarkupContainer( "link" )
            .add( new Label( "linkName", part.getTask() ) )
            .add( new AttributeModifier( "href", true, new Model<String>( "#t_" + part.getId() ) ) );
    }

    private Component newRfiSection( final Assignments rfis ) {

        return new WebMarkupContainer( "rfiDiv" )
           .add( new ListView<Assignment>( "rfiLinks", rfis.getAssignments() ) {
               @Override
               protected void populateItem( ListItem<Assignment> item ) {
                   Assignment a = item.getModelObject();
                   final Part part = a.getPart();
                   item.add(
                       new ListView<Flow>( "flow", getTriggeringFlows( part ) ) {
                           @Override
                           protected void populateItem( ListItem<Flow> flowListItem ) {
                               Flow flow = flowListItem.getModelObject();
                               flowListItem.add(
                                   new Label( "flowName", flow.getName() ),
                                   new ListView<Specable>( "sources", getSources( flow ) ) {
                                       @Override
                                       protected void populateItem( ListItem<Specable> specItem ) {
                                           Specable specable = specItem.getModelObject();
                                           specItem.add( new Label( "source", specable.toString() ),
                                                         new Label( "sourceSep",
                                                                    specItem.getIndex()
                                                                    == getViewSize() - 1 ? "" :
                                                                    specItem.getIndex()
                                                                    == getViewSize() - 2 ? " or "
                                                                                  : ", " )
                                                             .setRenderBodyOnly( true )
                                           ).setRenderBodyOnly( true );


                                       }
                                   }.setRenderBodyOnly( true ),
                                   new Label( "flowSep",
                                              flowListItem.getIndex() == getViewSize() - 1 ? ". "
                                            : flowListItem.getIndex() == getViewSize() - 2 ? " or "
                                                                                    : ", " )
                                        .setRenderBodyOnly( true )
                               );
                           }
                       },
                       newTaskLink( part ) );
               }
           } )
           .setVisible( !rfis.isEmpty() );
    }

    private List<? extends Specable> getSources( Flow flow ) {
        return getPlanService().getAssignments( true ).with( flow.getSource() ).getActors();
    }

    private static List<? extends Flow> getTriggeringFlows( Part part ) {
        List<Flow> result = new ArrayList<Flow>();

        Iterator<Flow> flows = part.flows();
        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( part.equals( flow.getTarget() )
                    && flow.isTriggeringToTarget()
                    && !flow.isAskedFor()
                    && !flow.isProhibited() )
                result.add( flow );
        }

        return result;
    }

    private static Component newDocSection( final List<Attachment> attachments ) {
        return new WebMarkupContainer( "documents" )
           .add( new ListView<Attachment>( "document", attachments ) {
               @Override
               protected void populateItem( ListItem<Attachment> item ) {
                   Attachment attachment = item.getModelObject();
                   item.add( new ExternalLink( "docLink",
                                               attachment.getUrl(),
                                               attachment.getLabel() ) );
               }
           } )
           .setVisible( !attachments.isEmpty() );
    }

    private static ListView<Part> newTaskLinks( final List<Part> routines ) {

        return new ListView<Part>( "links", routines ) {
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( newTaskLink( item.getModelObject() ) );
            }
        };
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

    private PlanService createPlanService( String uri, int version ) throws NotFoundException {
        for ( Plan plan : planManager.getPlansWithUri( uri ) )
            if ( plan.getVersion() == version )
                return new PlanService( planManager, null, userService, plan );

        throw new NotFoundException();
    }
}
