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
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.EventPhase;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Job;
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
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RedirectToUrlException;
import org.apache.wicket.behavior.AttributeAppender;
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
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    //-----------------------------------
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

    //-----------------------------------
    private void init( PlanService service, ResourceSpec profile ) {

        Plan plan = service.getPlan();
        Assignments myAssignments = service.getAssignments( true ).with( profile );
        Assignments directAssignments = myAssignments.notFrom( profile );

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

             newPhases( directAssignments, eventPhases ),
             newTasks( myAssignments )
        );
    }

    //-----------------------------------
    private ListView<Assignment> newTasks( final Assignments myAssignments ) {
        return new ListView<Assignment>( "tasks", myAssignments.getAssignments() ) {
            @Override
            protected void populateItem( ListItem<Assignment> item ) {
                Assignment a = item.getModelObject();
                Part part = a.getPart();
                PlanService planService = getPlanService();
                List<Part> subtasks = myAssignments.from( a ).getParts();

                List<EOI> eois = findStartingEois( part );
                String category = part.getCategory() == null ? ""
                                                 : part.getCategory().getLabel().toLowerCase();
                List<Goal> risks = getRisks( part );
                List<Goal> gains = getGains( part );
                item.add(
                    new WebMarkupContainer( "taskAnchor" )
                        .add( new Label( "taskName", part.getTask() ) )
                        .add( new AttributeModifier( "name", true, new Model<String>(
                              "t_" + part.getId() ) ) ),
                    new Label( "taskSummary", ensurePeriod( getTaskSummary( a ) ) ),
                    new Label( "instruct", ensurePeriod( part.getDescription() ) )
                        .setVisible( !part.getDescription().isEmpty() ),
                    new Label( "taskLoc", part.getLocation() == null ? ""
                                        : ensurePeriod( "This task is located in " + part.getLocation() ) )
                        .setVisible( part.getLocation() != null ),
                    new Label( "taskDuration", part.getCompletionTime() == null ? ""
                                        : ensurePeriod( "This task should be completed " + part.getCompletionTime() ) )
                        .setVisible( part.getCompletionTime() != null ),
                    new WebMarkupContainer( "routineTask" )
                        .add(
                            new Label( "taskType", category )
                                .setRenderBodyOnly( true ),
                            new Label( "taskRecur", part.isRepeating() ? "It is repeated every " + part.getRepeatsEvery() + "."
                                                                       : "" )
                                .setVisible( part.isRepeating() ),
                            new WebMarkupContainer( "operational" )
                                .setRenderBodyOnly( true )
                                .setVisible( part.isEffectivelyOperational() )
                        )
                        .setVisible( Assignments.isImmediate( part, planService ) ),
                    new WebMarkupContainer( "notifTask" )
                        .add(
                            new Label( "taskType", category )
                                .setRenderBodyOnly( true ),
                            new Label( "taskRecur", part.isRepeating() ? part.getRepeatsEvery().toString()
                                                                       : "" )
                                .setVisible( part.isRepeating() ),
                            new WebMarkupContainer( "operational" )
                                .setRenderBodyOnly( true )
                                .setVisible( part.isEffectivelyOperational() ),
                            newSimpleEoiList( eois )
                        )
                        .setVisible( Assignments.isNotification( part, planService ) ),
                    new WebMarkupContainer( "reqTask" )
                        .setVisible( Assignments.isRequest( part ) ),
                    new WebMarkupContainer( "subTask" )
                        .add( newSimpleEoiList( eois ) )
                        .setVisible( Assignments.isOptional( part, planService ) ),
                    new WebMarkupContainer( "prohibited" )
                        .setVisible( part.isProhibited() ),
                    new WebMarkupContainer( "term1" )
                        .add( new Label( "eventPhase", lcFirst( part.getSegment().getPhaseEventTitle() ) ) )
                        .setVisible( part.isStartsWithSegment() ),
                    new WebMarkupContainer( "canStart" )
                        .setVisible( false ),
                    new WebMarkupContainer( "canStop" )
                        .setVisible( false ),
                    new WebMarkupContainer( "riskDiv" )
                        .add( new ListView<Goal>( "risks", risks ) {
                                    @Override
                                    protected void populateItem( ListItem<Goal> item ) {
                                        item.add( new Label( "type",
                                                             item.getModelObject().getFullTitle() ) );
                                    }
                              },
                              new ListView<Goal>( "gains", gains ) {
                                    @Override
                                    protected void populateItem( ListItem<Goal> goalItem ) {
                                        goalItem.add( new Label( "type",
                                                                 goalItem.getModelObject()
                                                                     .getFullTitle() ) );
                                    }
                              },
                              new WebMarkupContainer( "conseqs" ).setVisible( false )
                        ).setVisible( !risks.isEmpty() || !gains.isEmpty() ),

                    new WebMarkupContainer( "superNote" )
                       .setVisible( false ),

                    new WebMarkupContainer( "teamDiv" )
                       .setVisible( part.isAsTeam() ),

                    newIncomingFlows( "criticalDiv",
                                      part.getEssentialFlows( false, planService ),
                                      planService ),
                    newIncomingFlows( "inputDiv", getInputDiv( part, planService ), planService ),
                    newDistribFlows( "distribDiv", getDistribDiv( part ), planService ),
                    newDistribFlows( "taskRfiDiv", getRfiDiv( part ), planService ),

                    new WebMarkupContainer( "subtaskDiv" )
                       .add( newTaskLinks( subtasks ) )
                       .setVisible( !subtasks.isEmpty() ),
                    new WebMarkupContainer( "failDiv" )
                       .setVisible( !getFailDiv( part ).isEmpty() ),

                    newDocSection( planService.getAttachmentManager().getMediaReferences( part ) )
                );
            }
        };
    }

    private static String lcFirst( String phrase ) {
        if ( phrase.length() < 2 )
            return phrase;

        return Character.toLowerCase( phrase.charAt( 0 ) )
             + phrase.substring( 1 );
    }

    private static String ensurePeriod( String sentence ) {
        return sentence == null || sentence.isEmpty() || sentence.endsWith( "." ) ? sentence
                                        : sentence + "." ;
    }

    private Component newDistribFlows(
        String id, final List<Flow> flows, final PlanService planService ) {
        return new WebMarkupContainer( id )
            .add(
                new ListView<Flow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<Flow> item ) {
                        Flow flow = item.getModelObject();
                        List<Employment> targets = getEmployments(
                            planService.getAssignments().from( (Specable) flow.getSource() ) );

                        item.add(
                            new Label( "flowName2", MessageFormat.format( getVerb( flow ),
                                                                          flow.getLabel() ) ),
                            new Label( "flowTiming", getTiming( flow ) ),
                            new WebMarkupContainer( "eoisRow" )
                                .add( newEoiList( findEois( flow ) ) )
                                .setRenderBodyOnly( true )
                                .setVisible( !flow.getEois().isEmpty() ),

                            newContacts( targets, planService )
                                .setRenderBodyOnly( true ),

                            new WebMarkupContainer( "flowEnding" )
                                .setVisible( flow.isTerminatingToSource() ),

                            // TODO ask JF about where to get this
                            new WebMarkupContainer( "noContext" )
                                .setVisible( false )

                        );
                    }
                }
                    .setRenderBodyOnly( true )


            )
            .setVisible( !flows.isEmpty() );
    }

    private static String getVerb( Flow flow ) {
        if ( flow.isAskedFor() )
            return "Answer about {0}";

        if ( flow.getIntent() == null )
            return "Send {0}";

        String verb;
        switch ( flow.getIntent() ) {
            case Alarm:
                verb = "Send {0} alert";
                break;
            case Announcement:
                verb = "Make announcement about {0}";
                break;
            case Command:
                verb = "Issue {0} command";
                break;
            case Feedback:
                verb = "Provide feedback about {0}";
                break;
            case Report:
                verb = "Report about {0}";
                break;
            default:
                verb = "Notify of {0}";
        }

        return verb;
    }

    //-----------------------------------
    private static String getTaskSummary( Assignment assignment ) {
        StringWriter w = new StringWriter();
        w.append( assignment.getPart().getSegment().getEventPhase().toString() );
        Place location = assignment.getLocation();
        if ( location != null ) {
            w.append( " in " );
            w.append( location.toString() );
        }

        return w.toString();
    }

    //-----------------------------------
    private ListView<EventPhase> newPhases(
        final Assignments directAssignments, final List<EventPhase> eventPhases ) {
        return new ListView<EventPhase>( "phases", eventPhases ) {
            @Override
            protected void populateItem( ListItem<EventPhase> item ) {
                EventPhase eventPhase = item.getModelObject();
                Assignments phaseEventAssignments = directAssignments.with( eventPhase );

                List<Part> routines = phaseEventAssignments.getImmediates( getPlanService() )
                                       .getParts();
                List<Part> routines1 = phaseEventAssignments.getOptionals( getPlanService() )
                                     .getParts();
                item.add(
                    new WebMarkupContainer( "phaseAnchor" )
                        .add( new Label( "phaseText", eventPhase.toString() ) )
                        .add( new AttributeModifier( "name", true, new Model<String>(
                            "ep_" + item.getIndex() ) ) ),

                    new Label( "eventDesc", ensurePeriod( eventPhase.getEvent().getDescription() ) )
                        .setVisible( !eventPhase.getEvent().getDescription().isEmpty() ),
                    new Label( "phaseDesc", ensurePeriod( eventPhase.getPhase().getDescription() ) )
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
        };
    }

    //-----------------------------------
    private static Component newSimpleEoiList( List<EOI> eois ) {
        return new WebMarkupContainer( "eois" )
            .add( newEoiList( eois ) )
            .setVisible( !eois.isEmpty() );
    }

    private static ListView<EOI> newEoiList( final List<EOI> eois ) {
        return new ListView<EOI>( "eoi", eois ) {
            @Override
            protected void populateItem( ListItem<EOI> item ) {
                EOI eoi = item.getModelObject();
                item.add( new Label( "eoi.name", eoi.label ),
                          new Label( "eoi.desc",
                                     notAvailable( ensurePeriod( eoi.eoi.getDescription() ) ) ),
                          new Label( "eoi.handling", notAvailable( eoi.eoi.getSpecialHandling() ) ),
                          new Label( "eoi.class",
                                     getClassificationString( eoi.eoi.getClassifications(), " or " ) ) );
                if ( item.getIndex() == getViewSize() - 1 )
                    item.add( new AttributeAppender( "class",
                                                     true,
                                                     new Model<String>( "last" ),
                                                     " " ) );
            }
        };
    }

    private static String notAvailable( String description ) {
        return description == null || description.isEmpty() ? "N/A" : description ;
    }

    //-----------------------------------
    private static String getClassificationString(
        List<Classification> classifications, String lastSep ) {

        if ( classifications.isEmpty() )
            return "N/A";

        StringWriter w = new StringWriter();
        for ( int i = 0; i < classifications.size(); i++ ) {
            Classification classification = classifications.get( i );
            w.append( classification.getName() );
            if ( i == classifications.size() - 2 )
                w.append( lastSep );
            else if ( i != classifications.size() - 1 )
                w.append( ", " );
        }

        return w.toString();
    }

    //-----------------------------------
    private List<EOI> findStartingEois( Part part ) {
        Set<EOI> eois = new HashSet<EOI>();
        Iterator<Flow> receives = part.receives();
        Map<String,EOI> seen = new HashMap<String, EOI>();
        while ( receives.hasNext() ) {
            Flow flow = receives.next();
            if ( flow.isTriggeringToTarget() ) {
                for ( ElementOfInformation e : flow.getEois() ) {
                    EOI eoi = new EOI( e, flow, null );
                    EOI old = seen.get( eoi.label );
                    if ( old == null ) {
                        seen.put( eoi.label, eoi );
                        eois.add( eoi );
                    }
                }
            }
        }

        List<EOI> result = new ArrayList<EOI>( eois );
        Collections.sort( result );
        return result;
    }

    private List<EOI> findEois( Flow flow ) {
        Set<EOI> eois = new HashSet<EOI>();
        Map<String, EOI> seen = new HashMap<String, EOI>();

        for ( ElementOfInformation e : flow.getEois() ) {
            EOI eoi = new EOI( e, flow, null );
            EOI old = seen.get( eoi.label );
            if ( old == null ) {
                seen.put( eoi.label, eoi );
                eois.add( eoi );
            }
        }

        List<EOI> result = new ArrayList<EOI>( eois );
        Collections.sort( result );
        return result;
    }

    private Component newIncomingFlows(
        String id, final List<Flow> flows, final PlanService planService ) {
        return new WebMarkupContainer( id )
            .add(
                new ListView<Flow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<Flow> item ) {
                        Flow flow = item.getModelObject();
                        List<Employment> sources = getEmployments(
                            planService.getAssignments().with( flow.getSource() ) );

                        item.add(
                            new Label( "flowName2", flow.getLabel() ),
                            new Label( "flowTiming", getTiming( flow ) ),
                            new WebMarkupContainer( "eoisRow" )
                                .add( newEoiList( findEois( flow ) ) )
                                .setRenderBodyOnly( true )
                                .setVisible( !flow.getEois().isEmpty() ),

                            newContacts( sources, planService )
                                .setVisible( flow.isAskedFor() )
                                .setRenderBodyOnly( true ),

                            new WebMarkupContainer( "flowEnding" )
                                .setVisible( flow.isTerminatingToTarget() )

                        );
                    }
                }
                    .setRenderBodyOnly( true )


            )
            .setVisible( !flows.isEmpty() );
    }

    private static ListView<Employment> newContacts(
        final List<Employment> sources, final PlanService planService ) {

        return new ListView<Employment>( "perFlowContact", sources ) {
            @Override
            protected void populateItem( ListItem<Employment> sourceItem ) {
                Employment employment = sourceItem.getModelObject();
                Organization organization = employment.getOrganization();

                Actor sup = employment.getSupervisor();
                Job supJob = null;
                if ( sup != null ) {
                    List<Job> jobs = planService.findAllJobs( organization, sup );
                    if ( !jobs.isEmpty() )
                        supJob = jobs.get( 0 );
                }

                sourceItem.add( newContact( "contact",
                                            employment.getJob(),
                                            employment.getActor(),
                                            organization ), newContact( "supervisor",
                                                                        supJob,
                                                                        sup,
                                                                        organization ).setVisible(
                    sup != null ) );
            }
        };
    }

    private static MarkupContainer newContact(
        String id, Job job, Actor actor, Organization organization ) {

        return new WebMarkupContainer( id )
            .add( new Label( "contact.name", actor == null ? "" : actor.getName() ), new Label(
                "contact.title",
                job == null || job.getTitle().isEmpty() ? "" : ", " + job.getTitle() ), new Label(
                "contact.classification",
                actor == null ? "" : ResponderPage.getClassificationString( actor
                                                                                .getClassifications(),
                                                                            " or " ) ),
                  new Label( "contact.organization", organization.toString() ) );
    }

    private static String getTiming( Flow flow ) {
        StringWriter w = new StringWriter();
        w.append( flow.isAskedFor() ? "Available upon request"
                                    : "Provided" );

        w.append( flow.getMaxDelay().getSeconds() == 0L ? "" : ", in at most " + flow.getMaxDelay().toString() );

        return w.toString();
    }

    private static List<Employment> getEmployments( Assignments assignments ) {
        Map<Actor,Employment> employments = new HashMap<Actor,Employment>();
        for ( Assignment assignment : assignments.getAssignments() )
            employments.put( assignment.getActor(), assignment.getEmployment() );

        List<Employment> result = new ArrayList<Employment>( employments.values() );
        Collections.sort( result, new Comparator<Employment>() {
            @Override
            public int compare( Employment o1, Employment o2 ) {
                return o1.getActor().getNormalizedName().compareToIgnoreCase(
                            o2.getActor().getNormalizedName() );
            }
        } );
        return result;
    }

    //-----------------------------------
    private List<Flow> getInputDiv( Part part, PlanService planService ) {
        Set<Flow> essentials = new HashSet<Flow>(
            planService.findEssentialFlowsFrom( part, false ) );
        List<Flow> others = new ArrayList<Flow>();
        Iterator<Flow> flows = part.flows();
        while ( flows.hasNext() ) {
            Flow next = flows.next();
            if ( !essentials.contains( next ) && !next.isTriggeringToTarget() )
                others.add( next );
        }

        return others;
    }

    //-----------------------------------
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

    //-----------------------------------
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

    //-----------------------------------
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

    //-----------------------------------
    private static List<Goal> getGains( Part part ) {
        Collection<Goal> inputCollection = part.getGoals();
        List<Goal> answer = new ArrayList<Goal>( inputCollection.size());
        for ( Goal item : inputCollection )
            if ( item.isPositive() )
                answer.add( item );

        return answer;
    }

    //-----------------------------------
    private static List<Goal> getRisks( Part part ) {
        Collection<Goal> inputCollection = part.getGoals();
        List<Goal> answer = new ArrayList<Goal>( inputCollection.size());
        for ( Goal item : inputCollection )
            if ( !item.isPositive() )
                answer.add( item );

        return answer;
    }

    //-----------------------------------
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

    //-----------------------------------
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

    //-----------------------------------
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

    //-----------------------------------
    private static Component newTaskLink( Part part ) {
        return new WebMarkupContainer( "link" )
            .add( new Label( "linkName", part.getTask() ) )
            .add( new AttributeModifier( "href", true, new Model<String>( "#t_" + part.getId() ) ) );
    }

    //-----------------------------------
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

    //-----------------------------------
    private List<? extends Specable> getSources( Flow flow ) {
        return getPlanService().getAssignments( true ).with( flow.getSource() ).getActors();
    }

    //-----------------------------------
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

    //-----------------------------------
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

    //-----------------------------------
    private static ListView<Part> newTaskLinks( final List<Part> routines ) {

        return new ListView<Part>( "links", routines ) {
            @Override
            protected void populateItem( ListItem<Part> item ) {
                item.add( newTaskLink( item.getModelObject() ) );
            }
        };
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

    //-----------------------------------
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

    //-----------------------------------
    private static ResourceSpec getProfile( PlanService service, User user )
        throws NotFoundException {

        Participation participation = service.findParticipation( user.getUsername() );
        if ( participation == null || participation.getActor() == null )
            throw new NotFoundException();

        return new ResourceSpec( participation.getActor(), null, null, null );
    }

    //-----------------------------------
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

    //-----------------------------------
    private PlanService createPlanService( String uri, int version ) throws NotFoundException {
        for ( Plan plan : planManager.getPlansWithUri( uri ) )
            if ( plan.getVersion() == version )
                return new PlanService( planManager, null, userService, plan );

        throw new NotFoundException();
    }

    //=======================================================
    private static class EOI implements Comparable<EOI> {

        private final ElementOfInformation eoi;

        private final String label;
        private final int level;
        private final EOI parent;

        private EOI( ElementOfInformation eoi, Flow flow, EOI parent ) {
            this.eoi = eoi;
            label = eoi.getLabel() ;//+ ( flow == null ? "" : " (from " + flow.getName() + ")" );
            level = parent == null ? 0 : parent.level + 1;
            this.parent = parent;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;

            if ( obj != null && getClass() == obj.getClass() ) {
                EOI eoi1 = (EOI) obj;
                if ( eoi.equals( eoi1.eoi ) && label.equals( eoi1.label ) )
                    return parent == null ? eoi1.parent == null
                                          : parent.equals( eoi1.parent );
            }

            return false;
        }

        @Override
        public int hashCode() {
            int result = eoi.hashCode();
            result = 31 * result + label.hashCode();
            result = 31 * result + ( parent != null ? parent.hashCode() : 0 );
            return result;
        }

        @Override
        public int compareTo( EOI o ) {
            return parent == null ? o.parent == null ? label.compareTo( o.label )
                                                     : -1
                                  : parent.equals( o.parent ) ? label.compareTo( o.label )
                                                              : parent.compareTo( o.parent );
        }
    }
}
