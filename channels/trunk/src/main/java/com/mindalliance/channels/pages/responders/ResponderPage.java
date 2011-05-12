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
import com.mindalliance.channels.model.Availability;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Classification;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.EventPhase;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Flow.Intent;
import com.mindalliance.channels.model.Flow.Restriction;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Node;
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
import java.io.Serializable;
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

        List<Segment> segments = directAssignments.getSegments();

//        final List<User> planners = service.getUserService().getPlanners( plan.getUri() );

        // TODO password change fields

        List<Actor> actualActors = service.findAllActualActors( profile );
        String myAvail ;
        String myContact;
        if ( actualActors.isEmpty() ) {
            myAvail = "N/A";
            myContact = "N/A";
        } else {
            Actor actor = actualActors.get( 0 );
            Availability availability = actor.getAvailability();
            myAvail = availability == null ? "24/7" : availability.toString();
            myContact = actor.getChannelsString();
        }

        add( new Label( "userName", user.getUsername() ),
             new Label( "personName",
                        profile.displayString( 256 ) ),
/*
             new ListView<User>( "planners", planners ) {
                 @Override
                 protected void populateItem( ListItem<User> item ) {
                     User u = item.getModelObject();
                     item.add( new ExternalLink( "planner",
                                                 "mailTo:" + u.getEmail(),
                                                 u.getFullName() + " <" + u.getEmail() + ">" ),
                               new Label( "listSep",
                                          item.getIndex() == getViewSize() - 1 ? ". " :
                                          item.getIndex() == getViewSize() - 2 ? " or " : ", " )
                                   .setRenderBodyOnly( true ) );
                 }
             },
*/

             new Label( "planName", plan.getName() ),
             new Label( "planName2", plan.getName() ),
             new Label( "planVersion", "v" + plan.getVersion() ),
             new Label( "planDescription", plan.getDescription() ),

             new Label( "myRoles", profile.getReportTitle() ),
             new Label( "myAvail", "Availability: " + myAvail ),
             new Label( "myContact", myContact ),

             new ListView<Segment>( "phaseLinks", segments ) {
                 @Override
                 protected void populateItem( ListItem<Segment> item ) {
                     Segment segment = item.getModelObject();
                     item.add( new WebMarkupContainer( "phaseLink" )
                                   .add( new Label( "phaseLinkText",
                                                    fullTitle( segment ) ) )
                                   .add( new AttributeModifier( "href", true, new Model<String>(
                                       "#ep_" + item.getIndex() ) ) ) );
                 }
             },

             newPhases( directAssignments, myAssignments, segments, profile ) );
    }

    private static String fullTitle( Segment segment ) {
        return segment.getName() + " (" + lcFirst( segment.getPhaseEventTitle() ) + ')';
    }

    private static List<ReportTask> numberTasks(
        List<Segment> segments, List<Assignment> assignments ) {

        List<ReportTask> result = new ArrayList<ReportTask>();
        Map<Segment,Integer> counts = new HashMap<Segment, Integer>( segments.size() );

        for ( Assignment assignment : assignments ) {
            Segment segment = assignment.getPart().getSegment();

            // Phases of subtasks may not appear in index...
            int phaseSeq = segments.indexOf( segment );
            if ( phaseSeq == -1 )
                phaseSeq = segments.size() + 1;

            Integer oldSeq = counts.get( segment );
            if ( oldSeq == null )
                oldSeq = 0;

            int taskSeq = oldSeq + 1;
            counts.put( segment, taskSeq );
            result.add( new ReportTask( phaseSeq + 2, assignment, taskSeq ) );
        }

        Collections.sort( result, new Comparator<ReportTask>() {
            @Override
            public int compare( ReportTask o1, ReportTask o2 ) {
                int i = o1.getPhaseSeq() - o2.getPhaseSeq();
                return i == 0 ? o1.getTaskSeq() - o2.getTaskSeq() : i;
            }
        } );
        return result;
    }

    //-----------------------------------
    private ListView<ReportTask> newTasks(
        final Assignments myAssignments, List<ReportTask> tasks ) {

        return new ListView<ReportTask>( "tasks", tasks ) {
            @Override
            protected void populateItem( ListItem<ReportTask> item ) {
                ReportTask t = item.getModelObject();
                Assignment a = t.getAssignment();
                Part part = t.getPart();

                PlanService planService = getPlanService();
                Assignments allAssignments = planService.getAssignments( false );

                // TODO back link to phase

                List<ElementOfInformation> eois = findStartingEois( part );
                String category = part.getCategory() == null ? ""
                                                 : part.getCategory().getLabel().toLowerCase();
                List<Part> subTasks = myAssignments.from( a ).getParts();
                item.add(
                    new WebMarkupContainer( "taskAnchor" )
                        .add( new Label( "taskName", part.getTask() ) )
                        .add( new AttributeModifier( "name", true, new Model<String>(
                              "t_" + part.getId() ) ) ),

                    new WebMarkupContainer( "backTask" )
                        .add( new AttributeModifier( "href", true,
                                        new Model<String>( "#ep_" + ( t.getPhaseSeq() - 2 ) ) ) ),


                    new Label( "taskSeq", t.getPhaseSeq() + "." + t.getTaskSeq() + "." ),
                    new Label( "taskSummary", ensurePeriod(
                                                    getTaskSummary( part, a.getLocation() ) ) ),

                    new Label( "taskLoc", part.getLocation() == null ? ""
                                        : ensurePeriod( "This task is located in " + part.getLocation() ) )
                        .setVisible( part.getLocation() != null ),

                    newPromptedByDiv( part, planService, eois, category ),
                    newDetailsDiv( part, planService, eois ),

                    new WebMarkupContainer( "prohibited" )
                        .setVisible( part.isProhibited() ),

                    new WebMarkupContainer( "teamDiv" )
                        .add( new Label( "teamSpec", new ResourceSpec( part ).getReportTitle() ) )
                        .setVisible( part.isAsTeam() ),

                    new WebMarkupContainer( "subtaskDiv" )
                        .add( newTaskLinks( subTasks ) )
                        .setVisible( !subTasks.isEmpty() ),

                    newIncomingFlows( "criticalDiv", listInputs( part ) ),
                    newOutgoingFlows( "distribDiv", listOutgoing( part ) ),
                    newOutgoingFlows( "taskRfiDiv", listRequests( part ) ),
                    newOutgoingFlows( "failDiv", listFailures( part ) ),

                    newDocSection( planService.getAttachmentManager().getMediaReferences( part ) )
                );
            }
        };
    }

    private Component newPromptedByDiv(
        Part part, PlanService planService, List<ElementOfInformation> eois, String category ) {
        return new WebMarkupContainer( "promptedBy" )
            .add( new WebMarkupContainer( "notifTask" ).add( new Label( "taskType", category ).setRenderBodyOnly(
                true ),
                                                             new Label( "taskRecur",
                                                                        part.isRepeating() ? part
                                                                            .getRepeatsEvery()
                                                                            .toString() : "" )
                                                                 .setVisible( part.isRepeating() ),
                                                             new Label( "reqFlow",
                                                                        getTriggeringFlowName( part ) ),
                                                             new Label( "reqFlowSrc",
                                                                        getTriggeringFlowSrc(
                                                                            getTriggeringFlows( part.receives() ) ) ),
                                                             newSimpleEoiList( eois ) ).setVisible(
                Assignments.isNotification( part, planService ) ),
                  new WebMarkupContainer( "reqTask" ).add( new Label( "taskType", category )
                                                               .setRenderBodyOnly( true ),
                                                           new Label( "reqFlow",
                                                                      getTriggeringFlowName( part ) ),
                                                           new Label( "reqFlowSrc",
                                                                      getTriggeringFlowSrc(
                                                                          getTriggeringFlows( part.receives() ) ) ) )
                      .setVisible( Assignments.isRequest( part ) ) )
            .setVisible( Assignments.isNotification( part, planService ) || Assignments.isRequest(
                part ) );
    }

    private Component newDetailsDiv(
        Part part, PlanService planService, List<ElementOfInformation> eois ) {

        Delay completionTime = part.getCompletionTime();
        List<Goal> risks = getRisks( part );
        List<Goal> gains = getGains( part );

        boolean partIsImmediate = Assignments.isImmediate( part, planService );
        boolean optional = Assignments.isOptional( part, planService );
        return new WebMarkupContainer( "details" )
            .add( new Label( "instruct", ensurePeriod( part.getDescription() ) )
                    .setVisible( !part.getDescription().isEmpty() ),

                  new WebMarkupContainer( "routineTask" )
                      .add( new Label( "taskRecur",
                                       part.isRepeating() ? "It is repeated every "
                                                            + part.getRepeatsEvery() + '.' : "" )
                                .setVisible( part.isRepeating() ) )
                      .setVisible( partIsImmediate ),

                  new WebMarkupContainer( "taskDuration" )
                      .add( new Label( "dur",
                                       completionTime == null ? "" : completionTime.toString() ) )
                      .setVisible( completionTime != null && completionTime.getSeconds() != 0 ),

                  new WebMarkupContainer( "term1" )
                      .add( new Label( "eventPhase", lcFirst( part.getSegment().getEventPhase()
                                                                  .toString() ) ) )
                      .setVisible( part.isStartsWithSegment() ),

                  // TODO super-event override notice
                  new WebMarkupContainer( "superNote" )
                      .setVisible( false ),

                  new WebMarkupContainer( "riskDiv" )
                      .add( new ListView<Goal>( "risks", risks ) {
                                @Override
                                protected void populateItem( ListItem<Goal> item ) {
                                    item.add( new Label( "type", item.getModelObject().getPartialTitle() ) );
                                }
                            },

                            new ListView<Goal>( "gains", gains ) {
                                @Override
                                protected void populateItem(
                                    ListItem<Goal> item ) {
                                    item.add( new Label( "type",
                                                         item.getModelObject().getFullTitle() ) );
                                }
                            },

                            // TODO Add consequences of task failure
                            new WebMarkupContainer( "conseqs" ).setVisible( false ) )

                      .setVisible( !risks.isEmpty() || !gains.isEmpty() ),

                  new WebMarkupContainer( "subTask" )
                      .add( newSimpleEoiList( eois ) )
                      .setVisible( optional )

            )

            .setVisible( !risks.isEmpty() || !gains.isEmpty()
                         || part.isStartsWithSegment() || partIsImmediate || optional
                         || ( completionTime != null && completionTime.getSeconds() != 0 ) );
    }

    private static String getTriggeringFlowSrc( List<AggregatedFlow> flows ) {

        Set<String> sourcesStrings = new HashSet<String>(  );
        for ( AggregatedFlow flow : flows ) {
            Set<? extends Specable> specables = flow.getSources();
            for ( Specable spec : specables ) {
                String source = spec.toString();
                Restriction restriction = flow.getRestriction();
                if ( restriction != null && restriction != Restriction.Self ) {
                    source += " if " + restriction.getLabel( true );
                }
                sourcesStrings.add( source );
            }
        }
        List<String> sources = new ArrayList<String>( sourcesStrings );
        Collections.sort( sources );

        StringWriter writer = new StringWriter();
        for ( int i = 0, sourcesSize = sources.size(); i < sourcesSize; i++ ) {
            String source = sources.get( i );
            writer.append( source );
            if ( i == sourcesSize - 2 )
                writer.append( " or " );
            else if ( i != sourcesSize - 1  )
                writer.append( ", " );
        }
        return writer.toString();
    }

    private static String getTriggeringFlowName( Part part ) {
        List<AggregatedFlow> triggeringFlows = getTriggeringFlows( part.receives() );
        return triggeringFlows.isEmpty() ? "" : triggeringFlows.get( 0 ).getLabel();
    }

    private static String lcFirst( String phrase ) {
        if ( phrase.length() < 2 )
            return phrase;

        return Character.toLowerCase( phrase.charAt( 0 ) )
             + phrase.substring( 1 );
    }

    private static String ensurePeriod( String sentence ) {
        return sentence == null || sentence.isEmpty()
               || sentence.endsWith( "." ) || sentence.endsWith( ";" ) ? sentence
                                        : sentence + '.';
    }

    private static Component newOutgoingFlows( String id, final List<AggregatedFlow> flows ) {

        return new WebMarkupContainer( id )
            .add(
                new ListView<AggregatedFlow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {
                        AggregatedFlow flow = item.getModelObject();

                        item.add(
                            new Label( "flowName2", flow.getFormattedLabel() ),
                            new Label( "flowTargets", ensurePeriod( flow.getSourcesString() ) ),
                            new Label( "flowTiming", lcFirst( flow.getTiming() ) ),
                            new Label( "flowCard", flow.isAll() ? "all" : "any" ),
                            new WebMarkupContainer( "critical" )
                                .setVisible( flow.isCritical() ),
                            new WebMarkupContainer( "eoisRow" )
                                .add( newEoiList( flow.getElementOfInformations() ) )
                                .setRenderBodyOnly( true )
                                .setVisible( flow.hasEois() ),

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

    //-----------------------------------
    private static String getTaskSummary( Part part, Place location ) {
        StringWriter w = new StringWriter();
        w.append( fullTitle( part.getSegment() ) );
        if ( location != null ) {
            w.append( " in " );
            w.append( location.toString() );
        }

        return w.toString();
    }

    //-----------------------------------
    private ListView<Segment> newPhases(
        final Assignments directAssignments, final Assignments myAssignments,
        final List<Segment> segments, final ResourceSpec profile ) {

        return new ListView<Segment>( "phases", segments ) {
            @Override
            protected void populateItem( ListItem<Segment> item ) {
                Segment segment = item.getModelObject();
                EventPhase eventPhase = segment.getEventPhase();
                Assignments segmentAssignments = directAssignments.with( segment );
                Assignments mySegmentAssignments = myAssignments.with( segment );

                PlanService planService = getPlanService();
                List<Part> immeds = segmentAssignments.getImmediates( planService ).getParts();
                List<Part> opts = segmentAssignments.getOptionals( planService ).getParts();

                List<ReportTask> tasks = numberTasks( segments,
                                                      mySegmentAssignments.getAssignments() );

                List<AggregatedContact> segmentContacts = findContacts( segment, mySegmentAssignments, profile );

                item.add(
                    new WebMarkupContainer( "phaseAnchor" )
                        .add( new Label( "phaseText", fullTitle( segment ) ) )
                        .add( new AttributeModifier( "name", true, new Model<String>(
                            "ep_" + item.getIndex() ) ) ),

                    // TODO add back link to top
                    new Label( "segDesc", ensurePeriod( segment.getDescription() ) )
                        .setVisible( !segment.getDescription().isEmpty() )
                        .setVisible( !eventPhase.getEvent().getDescription().isEmpty() ),

                    new Label( "phaseSeq", item.getIndex() + 2 + "." ),

                    new WebMarkupContainer( "routineDiv" )
                        .add( newTaskLinks( immeds ) )
                        .setVisible( !immeds.isEmpty() ),

                    newInputDiv( segmentAssignments.getNotifications( planService ),
                                 segmentAssignments.getRequests() ),

                    newDocSection( getAttachments( planManager.getAttachmentManager(),
                                                   segmentAssignments.getSegments() ) ),
                    newTasks( mySegmentAssignments, tasks ),

                    new WebMarkupContainer( "contactList" )
                        .add(
                            new Label( "contactSeq", item.getIndex() + 2 + "." + ( tasks.size() + 1 ) + "." ),
                            newContacts( segmentContacts )
                        )
                        .setVisible( !segmentContacts.isEmpty() )
                    );

            }
        };
    }

    private List<AggregatedContact> findContacts(
        Segment segment, Assignments mySegmentAssignments, ResourceSpec profile ) {

        PlanService planService = getPlanService();
        Assignments assignments = planService.getAssignments( false );

        Map<Actor,AggregatedContact> map = new HashMap<Actor, AggregatedContact>();
        for ( Commitment commitment : planService.findAllCommitmentsOf(
                                            profile,
                                            assignments.with( segment ),
                                            segment.getAllSharingFlows() ) ) {

            Employment employment = commitment.getBeneficiary().getEmployment();
            Actor actor = employment.getActor();
            AggregatedContact aggregatedContact = map.get( actor );
            if ( aggregatedContact == null )
                map.put( actor, new AggregatedContact( planService, employment ) );
            else
                aggregatedContact.merge( planService, employment );
        }

        List<AggregatedContact> result = new ArrayList<AggregatedContact>( map.values() );
        Collections.sort( result );
        return result;
    }

    //-----------------------------------
    private static Component newSimpleEoiList( List<ElementOfInformation> eois ) {
        return new WebMarkupContainer( "eois" )
            .add( newEoiList( eois ) )
            .setVisible( !eois.isEmpty() );
    }

    private static ListView<ElementOfInformation> newEoiList(
        final List<ElementOfInformation> eois ) {
        return new ListView<ElementOfInformation>( "eoi", eois ) {
            @Override
            protected void populateItem( ListItem<ElementOfInformation> item ) {
                ElementOfInformation eoi = item.getModelObject();
                item.add( new Label( "eoi.name", eoi.getContent() ),
                          new Label( "eoi.desc",
                                     notAvailable( ensurePeriod( eoi.getDescription() ) ) ),
                          new Label( "eoi.handling", notAvailable( eoi.getSpecialHandling() ) ),
                          new Label( "eoi.class",
                                     getClassificationString( eoi.getClassifications() ) ) );
                if ( item.getIndex() == 0 )
                    item.add( new AttributeAppender( "class",
                                                     true,
                                                     new Model<String>( "first" ),
                                                     " " ) );
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
    private static String getClassificationString( List<Classification> classifications ) {

        if ( classifications.isEmpty() )
            return "N/A";

        StringWriter w = new StringWriter();
        for ( int i = 0; i < classifications.size(); i++ ) {
            Classification classification = classifications.get( i );
            w.append( classification.toString() );
            if ( i == classifications.size() - 2 )
                w.append( " or " );
            else if ( i != classifications.size() - 1 )
                w.append( ", " );
        }

        return w.toString();
    }

    //-----------------------------------
    private static List<ElementOfInformation> findStartingEois( Part part ) {
        Set<ElementOfInformation> eois = new HashSet<ElementOfInformation>();
        Iterator<Flow> receives = part.receives();
        Map<String,ElementOfInformation> seen = new HashMap<String, ElementOfInformation>();
        while ( receives.hasNext() ) {
            Flow flow = receives.next();
            if ( flow.isTriggeringToTarget() ) {
                for ( ElementOfInformation e : flow.getEois() ) {
                    ElementOfInformation old = seen.get( e.getContent() );
                    if ( old == null ) {
                        seen.put( e.getContent(), e );
                        eois.add( e );
                    }
                }
            }
        }

        List<ElementOfInformation> result = new ArrayList<ElementOfInformation>( eois );
        Collections.sort( result, new Comparator<ElementOfInformation>() {
            @Override
            public int compare( ElementOfInformation o1, ElementOfInformation o2 ) {
                return o1.getContent().compareToIgnoreCase( o2.getContent() );
            }
        } );
        return result;
    }

    private static Component newIncomingFlows( String id, final List<AggregatedFlow> flows ) {
        return new WebMarkupContainer( id )
            .add(
                new ListView<AggregatedFlow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {
                        AggregatedFlow flow = item.getModelObject();

                        item.add(
                            new Label( "flowName2", flow.getFormattedLabel() ),
                            new Label( "flowSources", flow.getSourcesString() ),
                            new Label( "flowTiming", lcFirst( flow.getTiming() ) ),
                            new WebMarkupContainer( "critical" )
                                .setVisible( flow.isCritical() ),
                            new WebMarkupContainer( "eoisRow" )
                                .add( newEoiList( flow.getElementOfInformations() ) )
                                .setRenderBodyOnly( true )
                                .setVisible( flow.hasEois() ),

                            new WebMarkupContainer( "flowEnding" )
                                .setVisible( flow.isTerminatingToTarget() )

                        );
                    }
                }
                    .setRenderBodyOnly( true )


            )
            .setVisible( !flows.isEmpty() );
    }

    private static ListView<AggregatedContact> newContacts(
        final List<AggregatedContact> sources ) {

        return new ListView<AggregatedContact>( "perFlowContact", sources ) {
            @Override
            protected void populateItem( ListItem<AggregatedContact> sourceItem ) {
                AggregatedContact aContact = sourceItem.getModelObject();

                AggregatedContact sup = aContact.getSupervisor();

                MarkupContainer contact = newContact( "contact",
                                                      aContact.getRoles(),
                                                      aContact.getOrganization(),
                                                      aContact.getTitle(),
                                                      aContact.getActor(),
                                                      aContact.getChannels() );
                Component supervisor = newContact( "supervisor",
                                                   sup.getRoles(),
                                                   sup.getOrganization(),
                                                   sup.getTitle(),
                                                   sup.getActor(),
                                                   sup.getChannels() )
                    .setVisible( sup.getActor() != null );

                sourceItem.add( contact, supervisor );

                if ( sourceItem.getIndex() == 0 )
                    contact.add( new AttributeAppender( "class", true, new Model<String>( "first" ), " " ) );
                if ( sourceItem.getIndex() == getViewSize() - 1 ) {
                    Component c = sup == null ? contact : supervisor;
                    c.add( new AttributeAppender( "class",
                                                  true,
                                                  new Model<String>( "last" ),
                                                  " " ) );
                }
            }
        };
    }

    private static MarkupContainer newContact(
        String id, List<Role> roles, Organization organization, String title, Actor actor,
        final List<Channel> channels ) {

        return new WebMarkupContainer( id )
            .add( new Label( "contact.name", actor == null ? "" : actor.getName() ),
                  new Label( "contact.title", title.isEmpty() ? "" : ", " + title ),
                  new Label( "contact.classification",
                             actor == null ? "" : ResponderPage.getClassificationString(
                                                    actor.getClassifications() ) ),
                  new Label( "contact.organization", organization == null ? "" : organization.toString() ),
                  new ListView<Role>( "contactRoles", roles ) {
                      @Override
                      protected void populateItem( ListItem<Role> roleListItem ) {
                          roleListItem.add( new Label( "contactRole",
                                                       roleListItem.getModelObject().toString() ) );
                      }
                  },

                  new WebMarkupContainer( "contactInfos" )
                      .add( new ListView<Channel>( "contactInfo", channels ) {
                          @Override
                          protected void populateItem( ListItem<Channel> item ) {
                              Channel channel = item.getModelObject();
                              item.add(
                                  new Label( "channelType",
                                             channel.getMedium().getLabel() + ":" ),
                                  new Label( "channel", channel.getAddress() ) );
                          }
                      } ).setVisible( !channels.isEmpty() ),

                  new WebMarkupContainer( "noInfo" ).setVisible( channels.isEmpty() ) );
    }

    private static List<Employment> getEmployments( Assignments assignments ) {
        Map<Actor,Employment> employments = new HashMap<Actor,Employment>();
        for ( Assignment assignment : assignments.getAssignments() )
            employments.put( assignment.getActor(), assignment.getEmployment() );

        List<Employment> result = new ArrayList<Employment>( employments.values() );
        Collections.sort( result, new Comparator<Employment>() {
            @Override
            public int compare( Employment o1, Employment o2 ) {
                return o1.toString().compareToIgnoreCase( o2.toString() );
            }
        } );
        return result;
    }

    private static List<AggregatedFlow> aggregate( List<Flow> flows, boolean incoming ) {
        Map<String,AggregatedFlow> map = new HashMap<String, AggregatedFlow>();
        for ( Flow flow : flows ) {
            AggregatedFlow aggregatedFlow = map.get( flow.getName() );
            if ( aggregatedFlow == null ) {
                aggregatedFlow = new AggregatedFlow( flow, incoming );
                map.put( flow.getName(), aggregatedFlow );
            } else
                aggregatedFlow.addFlow( flow );
        }

        List<AggregatedFlow> result = new ArrayList<AggregatedFlow>( map.values() );
        Collections.sort( result, new Comparator<AggregatedFlow>() {
            @Override
            public int compare( AggregatedFlow o1, AggregatedFlow o2 ) {
                return o1.getFormattedLabel().compareToIgnoreCase( o2.getFormattedLabel() );
            }
        } );
        return result;
    }

    //-----------------------------------
    private static List<AggregatedFlow> listInputs( Part part ) {
        List<Flow> inputs = new ArrayList<Flow>();
        for ( Flow flow : part.getAllSharingReceives() )
            if ( !flow.isTriggeringToTarget() )
                inputs.add( flow );

        return aggregate( inputs, true );
    }

    //-----------------------------------
    private static List<AggregatedFlow> listOutgoing( Part part ) {
        List<Flow> result = new ArrayList<Flow>();

        for ( Flow flow : part.getAllSharingSends() )
            if ( !flow.isAskedFor() && !flow.isIfTaskFails() )
                result.add( flow );

        return aggregate( result, false );
    }

    //-----------------------------------
    private static List<AggregatedFlow> listFailures( Part part ) {
        List<Flow> result = new ArrayList<Flow>();
        for ( Flow flow : part.getAllSharingSends() )
            if ( !flow.isAskedFor() && flow.isIfTaskFails() )
                result.add( flow );

        return aggregate( result, false );
    }

    //-----------------------------------
    private static List<AggregatedFlow> listRequests( Part part ) {
        List<Flow> result = new ArrayList<Flow>();
        for ( Flow flow : part.getAllSharingSends() )
            if ( flow.isAskedFor() )
                result.add( flow );

        return aggregate( result, true );
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
    private Component newInputDiv( final Assignments notifications, Assignments requests ) {

        return new WebMarkupContainer( "inputDiv" )
           .add(
               new ListView<Assignment>( "notLinks", notifications.getAssignments() ) {
                   @Override
                   protected void populateItem( ListItem<Assignment> item ) {
                       Assignment a = item.getModelObject();
                       final Part part = a.getPart();
                       item.add(
                           new ListView<AggregatedFlow>( "flow", getTriggeringFlows( part.receives() ) ) {
                               @Override
                               protected void populateItem( ListItem<AggregatedFlow> flowListItem ) {
                                   AggregatedFlow flow = flowListItem.getModelObject();
                                   flowListItem.add(
                                       new Label( "flowName", flow.getLabel() ),
                                       new Label( "flowSources", flow.getSourcesString() ),
                                       new Label( "flowSep", flowListItem.getIndex() == getViewSize() - 1 ? "."
                                                           : flowListItem.getIndex() == getViewSize() - 2 ? " or "
                                                           : ", ")
                                   );
                               }
                           },
                           newTaskLink( part ) );
                   }
               },
               new ListView<Assignment>( "rfiLinks", requests.getAssignments() ) {
                   @Override
                   protected void populateItem( ListItem<Assignment> item ) {
                       Assignment a = item.getModelObject();
                       final Part part = a.getPart();
                       item.add(
                           new ListView<AggregatedFlow>( "flow", getTriggeringFlows( part.receives() ) ) {
                               @Override
                               protected void populateItem( ListItem<AggregatedFlow> flowListItem ) {
                                   AggregatedFlow flow = flowListItem.getModelObject();
                                   flowListItem.add(
                                       new Label( "flowName", flow.getLabel() ),
                                       new Label( "flowSources", flow.getSourcesString() ),
                                       new Label( "flowSep", flowListItem.getIndex() == getViewSize() - 1 ? "."
                                                           : flowListItem.getIndex() == getViewSize() - 2 ? " or "
                                                           : ", ")

                                   );
                               }
                           },
                           newTaskLink( part ) );
                   }
               }

           )
           .setVisible( !notifications.isEmpty() || !requests.isEmpty() );
    }

    //-----------------------------------
    private static Component newTaskLink( Part part ) {
        return new WebMarkupContainer( "link" )
            .add( new Label( "linkName", part.getTask() ) )
            .add( new AttributeModifier( "href", true, new Model<String>( "#t_" + part.getId() ) ) );
    }

    //-----------------------------------
    private static List<AggregatedFlow> getTriggeringFlows( Iterator<Flow> flows ) {
        List<Flow> result = new ArrayList<Flow>();

        while ( flows.hasNext() ) {
            Flow flow = flows.next();
            if ( flow.isTriggeringToTarget() ) {
                Node source = flow.getSource();
                if ( !source.isConnector()
                     || !( (Connector) source ).getExternalFlows().isEmpty() )
                    result.add( flow );
            }
        }

        return aggregate( result, true );
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

    //================================================
    /**
     *  Some report-specific extra information for an assignment.
     */
    private static class ReportTask implements Serializable {
        private final int phaseSeq;
        private final Assignment assignment;
        private final int taskSeq;

        private ReportTask( int phaseSeq, Assignment assignment, int taskSeq ) {
            this.phaseSeq = phaseSeq;
            this.assignment = assignment;
            this.taskSeq = taskSeq;
        }

        public int getPhaseSeq() {
            return phaseSeq;
        }

        public Assignment getAssignment() {
            return assignment;
        }

        public int getTaskSeq() {
            return taskSeq;
        }

        public Part getPart() {
            return assignment.getPart();
        }
    }

    //================================================
    private static final class AggregatedContact implements Comparable<AggregatedContact> {
        private final Actor actor;
        private AggregatedContact supervisor;
        private final String title;
        private final Organization organization;
        private final Set<Role> roles = new HashSet<Role>();
        private final Set<Channel> channels = new HashSet<Channel>();

        private AggregatedContact() {
            actor = null;
            title = "";
            organization = null;
        }

        private AggregatedContact( PlanService service, Employment employment ) {
            actor = employment.getActor();
            title = employment.getJob().getTitle();
            organization = employment.getOrganization();

            if ( employment.getSupervisor() != null ) {
                List<Employment> employments =
                    service.findAllEmploymentsForActor( employment.getSupervisor() );
                if ( !employments.isEmpty() ) {
                    supervisor = new AggregatedContact( service, employments.get( 0 ) );

                }

            } else
                supervisor = new AggregatedContact();

            merge( service, employment );
        }

        public void merge( PlanService service, Employment employment ) {
            roles.add( employment.getRole() );
            channels.addAll( service.findAllChannelsFor( new ResourceSpec( employment ) ) );
        }

        public AggregatedContact getSupervisor() {
            return supervisor;
        }

        public String getName() {
            return actor.getNormalizedName();
        }

        public String getTitle() {
            return title;
        }

        public List<Role> getRoles() {
            List<Role> result = new ArrayList<Role>( roles );
            Collections.sort( result );
            return result;
        }

        public List<Channel> getChannels() {
            List<Channel> result = new ArrayList<Channel>( channels );
            Collections.sort( result );
            return result;
        }

        public Organization getOrganization() {
            return organization;
        }

        public List<Classification> getClearances() {
            return actor.getClearances();
        }

        public Actor getActor() {
            return actor;
        }


        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;

            if ( obj == null || getClass() != obj.getClass() )
                return false;

            AggregatedContact that = (AggregatedContact) obj;
            return actor == null ? that.getActor() == null
                                 : actor.equals( that.getActor() );
        }

        @Override
        public int hashCode() {
            return actor != null ? actor.hashCode() : 0;
        }

        @Override
        public int compareTo( AggregatedContact o ) {
            int i = organization.compareTo( o.getOrganization() );
            return i == 0 ? actor.compareTo( o.getActor() ) : i ;
        }
    }

    //================================================
    private static class AggregatedFlow {

        private final String label;
        private final Set<ResourceSpec> sources = new HashSet<ResourceSpec>();
        private final Map<String,ElementOfInformation> eoiIndex =
                    new HashMap<String, ElementOfInformation>();
        private final boolean incoming;
        private final Flow flow;

        private Delay maxDelay = null;

        private AggregatedFlow( Flow flow, boolean incoming ) {
            label = flow.getName();
            this.flow = flow;
            this.incoming = incoming;
            addFlow( flow );
        }

        private String getSourcesString() {
            List<ResourceSpec> specList = new ArrayList<ResourceSpec>( sources );
            Collections.sort( specList, new Comparator<ResourceSpec>() {
                @Override
                public int compare( ResourceSpec o1, ResourceSpec o2 ) {
                    return o1.toString().compareToIgnoreCase( o2.toString() );
                }
            } );

            StringWriter w = new StringWriter();
            for ( int i = 0; i < specList.size(); i++ ) {
                w.append( specList.get( i ).getReportSource() );
                if ( i == specList.size() - 2 )
                    w.append( " or " );
                else if ( i != specList.size() - 1 )
                    w.append( ", " );
            }
            if ( flow.getRestriction() != null ) {
                w.append( " if ");
                w.append( flow.getRestriction().getLabel( !incoming ) );
            }
            return w.toString();
        }

        private String getFormattedLabel() {
            String verb;
            Intent intent = getIntent();
            if ( incoming )
                verb = "{0}";
            else if ( isAskedFor() )
                verb = "Answer about {0}";
            else if ( intent == null )
                verb = "Send {0}";
            else
                switch ( intent ) {
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

            return MessageFormat.format( verb, label );
        }

        private String getTiming() {
            StringWriter w = new StringWriter();
            if ( incoming ) {
                w.append( isAskedFor() ? "Available upon request" : "" );
                w.append( maxDelay.getSeconds() == 0 ? ""
                                                     : " after at most " + maxDelay.toString() );
            } else
                w.append( maxDelay.getSeconds() == 0 ? ""
                                                     : "Within " + maxDelay.toString() );

            return w.toString();
        }

        private void addFlow( Flow flow ) {
            if ( incoming ) {
                Node source = flow.getSource();
                if ( source.isPart() )
                    sources.add( new ResourceSpec( (Specable) source ) );
                else
                    for ( ExternalFlow externalFlow : ((Connector) source).getExternalFlows() ) {
                        Node node = externalFlow.getSource();
                        if ( node.isPart() )
                            sources.add( new ResourceSpec( (Specable) node ) );
                        // TODO else?
                        }
            } else {
                Node target = flow.getTarget();
                if ( target.isPart() )
                    sources.add( new ResourceSpec( (Specable) target ) );
                else
                    for ( ExternalFlow externalFlow : ((Connector) target).getExternalFlows() ) {
                        Node node = externalFlow.getTarget();
                        if ( node.isPart() )
                            sources.add( new ResourceSpec( (Specable) node ) );
                        // TODO else?
                        }
            }

            List<ElementOfInformation> eois = flow.getEois();
            for ( ElementOfInformation eoi : eois ) {
                String key = eoi.getContent();
                if ( !eoiIndex.containsKey( key ) )
                    eoiIndex.put( key, eoi );
            }

            // TODO compute minimum max delay
            if ( maxDelay == null )
                maxDelay = flow.getMaxDelay();
        }

        public String getLabel() {
            return label;
        }

        public List<ElementOfInformation> getElementOfInformations() {
            List<ElementOfInformation> result = new ArrayList<ElementOfInformation>( eoiIndex.values() );
            Collections.sort( result, new Comparator<ElementOfInformation>() {
                @Override
                public int compare( ElementOfInformation o1, ElementOfInformation o2 ) {
                    return o1.getContent().compareToIgnoreCase( o2.getContent() );
                }
            } );
            return result;
        }

        public boolean hasEois() {
            return !eoiIndex.isEmpty();
        }

        public boolean isAskedFor() {
            return flow.isAskedFor();
        }

        public boolean isAll() {
            return flow.isAll();
        }

        public boolean isCritical() {
            return flow.isCritical();
        }

        public boolean isTerminatingToTarget() {
            return flow.isTerminatingToTarget();
        }

        public boolean isTriggeringToTarget() {
            return flow.isTriggeringToTarget();
        }

        public boolean isTerminatingToSource() {
            return flow.isTerminatingToSource();
        }

        public Intent getIntent() {
            return flow.getIntent();
        }

        public Set<ResourceSpec> getSources() {
            return Collections.unmodifiableSet( sources );
        }

        public Flow.Restriction getRestriction() {
            return flow.getRestriction();
        }
    }
}
