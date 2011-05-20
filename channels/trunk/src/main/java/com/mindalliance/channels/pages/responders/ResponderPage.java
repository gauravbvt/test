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
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Flow.Intent;
import com.mindalliance.channels.model.Flow.Restriction;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Part.Category;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.util.ChannelsUtils;
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
import org.apache.wicket.model.IModel;
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
                init( service, getProfile( service, parameters ), parameters );
            }

        } catch ( StringValueConversionException e ) {
            LOG.info( "Bad parameter: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

        } catch ( NotFoundException e ) {
            LOG.info( "Not found: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    private static List<ResourceSpec> actualize(
                PlanService service, Specable spec, Restriction restriction, Specable origin ) {


        Organization org = spec.getOrganization();
        if ( org != null && !org.isUnknown() && !org.isActual() ) {
            List<ResourceSpec> result = new ArrayList<ResourceSpec>();
            for ( ModelEntity entity : service.findAllNarrowingOrEqualTo( org ) )
                if ( entity.isActual() ) {
                    Organization actualOrg = (Organization) entity;
                    if ( restriction == null || isAllowed( origin.getOrganization(),
                                                           actualOrg,
                                                           restriction ) )
                        result.add( new ResourceSpec( spec.getActor(),
                                                      spec.getRole(),
                                                      actualOrg,
                                                      spec.getJurisdiction() ) );
                }

            if ( result.size() < 2 )
                return result;
        }

        List<ResourceSpec> result = new ArrayList<ResourceSpec>();
        result.add( new ResourceSpec( spec ) );

        return result;
    }

    private static boolean isAllowed(
        Organization committer, Organization beneficiary, Restriction restriction ) {

        if ( restriction != null )
            switch ( restriction ) {
                    case SameTopOrganization:
                        return ModelObject.isNullOrUnknown( committer )
                                || ModelObject.isNullOrUnknown( beneficiary )
                                || committer.getTopOrganization()
                                .equals( beneficiary.getTopOrganization() );

                    case SameOrganization:
                        return ModelObject.isNullOrUnknown( committer )
                                || ModelObject.isNullOrUnknown( beneficiary )
                                || committer.narrowsOrEquals( beneficiary, null )
                                || beneficiary.narrowsOrEquals( committer, null );

                    case DifferentOrganizations:
                        return ModelObject.isNullOrUnknown( committer )
                                || ModelObject.isNullOrUnknown( beneficiary )
                                || !committer.narrowsOrEquals( beneficiary, null )
                                    && !beneficiary.narrowsOrEquals( committer, null );

                    case DifferentTopOrganizations:
                        return ModelObject.isNullOrUnknown( committer )
                                || ModelObject.isNullOrUnknown( beneficiary )
                                || !committer.getTopOrganization()
                                    .equals( beneficiary.getTopOrganization() );

                    default:
                }

        return true;
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
    private void init( PlanService service, ResourceSpec profile, PageParameters parameters ) {

        Plan plan = service.getPlan();

        // TODO password change fields

        List<Actor> actualActors = service.findAllActualActors( profile );
        User assignedUser = userService.getUserNamed( parameters.getString( "user", "" ) );
        if ( assignedUser == null && !user.isPlanner( plan.getUri() ) ) assignedUser = user;
        String myName = assignedUser == null ? "" : assignedUser.getFullName();
        String myAvail;
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

        List<ReportSegment> reportSegments = getSegments( service, profile );
        add(
            new Label( "userName", user.getUsername() ),
            new Label( "personName", profile.displayString( 256 ) ),

            new Label( "planName", plan.getName() ),
            new Label( "planName2", plan.getName() ),
            new Label( "planVersion", "v" + plan.getVersion() ),
            new Label( "planDescription", plan.getDescription() ),

            new Label( "myName", "Name: " + myName )
               .setVisible( !myName.isEmpty() ),
            new Label( "myRoles", profile.getReportTitle() ),
            new Label( "myAvail", "Availability: " + myAvail ),
            new Label( "myContact", "How to contact: " + myContact ),

            new ListView<ReportSegment>( "phaseLinks", reportSegments ) {
                @Override
                protected void populateItem( ListItem<ReportSegment> item ) {
                    ReportSegment segment = item.getModelObject();
                    item.add(
                        new Label( "phaseName", segment.getName() ),
                        new WebMarkupContainer( "phaseLink" )
                            .add( new Label( "phaseLinkText", segment.getTitle() )
                                      .setRenderBodyOnly( true ) )
                            .add( new AttributeModifier( "href", true, segment.getLink() ) ) );
                }
            },

            newPhases( reportSegments )
        );
    }

    //-----------------------------------
    private ListView<ReportSegment> newPhases( List<ReportSegment> segments ) {

        return new ListView<ReportSegment>( "phases", segments ) {
            @Override
            protected void populateItem( ListItem<ReportSegment> item ) {
                ReportSegment segment = item.getModelObject();

                item.add(
                    new WebMarkupContainer( "phaseAnchor" )
                        .add( new Label( "phaseText", segment.getName() ) )
                        .add( new AttributeModifier( "name", true, segment.getAnchor() ) ),

                    // TODO add back link to top
                    new Label( "context", segment.getContext() ),
                    new Label( "segDesc", ensurePeriod( segment.getDescription() ) )
                        .setVisible( !segment.getDescription().isEmpty() ),

                    new Label( "phaseSeq", Integer.toString( segment.getSeq() ) ),

                    new WebMarkupContainer( "routineDiv" )
                        .add( newTaskLinks( segment.getImmediates() ) )
                        .setVisible( !segment.getImmediates().isEmpty() ),

                    newInputDiv( segment.getPrompted() ),

                    newDocSection(
                        segment.getAttachmentsFrom( planManager.getAttachmentManager() ) ),

                    newTasks( segment, segment.getTasks() ),

                    new WebMarkupContainer( "contactList" )
                        .add(
                            new Label( "contactSeq", segment.getContactSeq() ),
                            new Label( "contactTitle", segment.getName() ),
                            newContacts( segment.getContacts(), segment.getContactSpecs() )
                        )
                        .setVisible( !segment.getContacts().isEmpty() )
                    );
            }
        };
    }

    //-----------------------------------
    private ListView<ReportTask> newTasks( final ReportSegment segment, List<ReportTask> tasks ) {

        return new ListView<ReportTask>( "tasks", tasks ) {
            @Override
            protected void populateItem( ListItem<ReportTask> item ) {
                ReportTask task = item.getModelObject();

                PlanService planService = getPlanService();

                // TODO back link to phase

                item.add( new WebMarkupContainer( "taskAnchor" )
                              .add( new Label( "taskName", task.getTitle() ) )
                              .add( new AttributeModifier( "name", true, task.getAnchor() ) ),

                          new WebMarkupContainer( "backTask" ).add(
                              new AttributeModifier( "href", true, segment.getLink() ) ),

                          new Label( "taskSeq", task.getSeqString() ),
                          new Label( "taskSummary", lcFirst( task.getTaskSummary() ) ),
                          new Label( "taskRole", ensurePeriod( task.getRoleString() ) ),

                          new Label( "taskLoc", task.getLocationString() )
                              .setVisible( task.getLocation() != null ),

                          newPromptedByDiv( task, planService ),
                          newDetailsDiv( task ),

                          new WebMarkupContainer( "prohibited" )
                              .setVisible( task.isProhibited() ),

                          new WebMarkupContainer( "teamDiv" )
                              .add( new Label( "teamSpec", task.getTeamSpec() ) )
                              .setVisible( task.isAsTeam() ),

                          new WebMarkupContainer( "subtaskDiv" )
                              .add( newTaskLinks( task.getSubtasks() ) )
                              .setVisible( !task.getSubtasks().isEmpty() ),

                          newIncomingFlows( "criticalDiv", task.getInputs() ),
                          newOutgoingFlows( "distribDiv", task.getOutgoing() ),
                          newOutgoingFlows( "taskRfiDiv", task.getRequests() ),
                          newOutgoingFlows( "failDiv", task.getFailures() ),

                          newDocSection( task.getAttachmentsFrom(
                                                planService.getAttachmentManager() ) ) );
            }
        };
    }

    private static Component newDetailsDiv( ReportTask task ) {

        List<Goal> risks = task.getRisks();
        List<Goal> gains = task.getGains();

        return new WebMarkupContainer( "details" )
            .add( new Label( "instruct", task.getDescription() )
                    .setVisible( !task.getDescription().isEmpty() ),

                  new WebMarkupContainer( "routineTask" )
                      .add( new Label( "taskRecur",
                                       task.isRepeating() ? "It is repeated every "
                                                            + task.getRepetition() + '.' : "" )
                                .setVisible( task.isRepeating() ) )
                      .setVisible( task.isImmediate() ),

                  new WebMarkupContainer( "taskDuration" )
                      .add( new Label( "dur", task.getCompletionString() ) )
                      .setVisible( task.getCompletionTime() ),

                  new WebMarkupContainer( "term1" )
                      .add( new Label( "eventPhase", task.getEventPhase() ) )
                      .setVisible( task.isStartWithSegment() ),

                  // TODO super-event override notice
                  new WebMarkupContainer( "superNote" )
                      .setVisible( false ),

                  new WebMarkupContainer( "riskDiv" )
                      .add( new ListView<Goal>( "risks", risks ) {
                                @Override
                                protected void populateItem( ListItem<Goal> item ) {
                                    item.add( new Label( "type",
                                                         item.getModelObject().getPartialTitle() ) );
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
                      .add( newSimpleEoiList( task.getStartingEois() ) )
                      .setVisible( task.isSubtask() )

            )

            .setVisible( !risks.isEmpty() || !gains.isEmpty() || task.isStartWithSegment()
                         || task.isImmediate() || task.isSubtask() || task.getCompletionTime() );
    }

    private static Component newPromptedByDiv( ReportTask task, PlanService planService ) {

        return new WebMarkupContainer( "promptedBy" )
            .add( new WebMarkupContainer( "notifTask" )
                      .add( new Label( "taskType", task.getCategoryString() )
                                .setRenderBodyOnly( true ),
                            new Label( "taskRecur", task.getRepetition() )
                                .setVisible( task.isRepeating() ),
                            new Label( "reqFlow",
                                       task.getTriggeringFlowName() ),
                            new Label( "reqFlowSrc", task.getTriggeringSources( planService ) ),
                            newSimpleEoiList( task.getStartingEois() ) )
                      .setVisible( task.isNotification( planService ) ),

                  new WebMarkupContainer( "reqTask" )
                      .add( new Label( "taskType", task.getCategoryString() )
                                .setRenderBodyOnly( true ),
                            new Label( "reqFlow", task.getTriggeringFlowName() ),
                            new Label( "reqFlowSrc",
                                        task.getTriggeringSources( planService ) ) )
                      .setVisible( task.isRequest() ) )

            .setVisible( task.isNotification( planService ) || task.isRequest() );
    }

    private static String lcFirst( String phrase ) {
        if ( phrase.length() < 2 )
            return phrase;

        return ChannelsUtils.smartUncapitalize( phrase );
    }

    private static String ensurePeriod( String sentence ) {
        return sentence == null
            || sentence.isEmpty()
            || sentence.endsWith( "." )
            || sentence.endsWith( ";" ) ? sentence
                                        : sentence + '.';
    }

    private Component newOutgoingFlows( String id, final List<AggregatedFlow> flows ) {

        return new WebMarkupContainer( id )
            .add(
                new ListView<AggregatedFlow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {
                        AggregatedFlow flow = item.getModelObject();

                        item.add(
                            new Label( "flowName2", flow.getFormattedLabel() ),
                            new Label( "flowTargets", ensurePeriod( flow.getSourcesString( getPlanService() ) ) ),
                            new Label( "flowTiming", lcFirst( flow.getTiming() ) ),
                            new WebMarkupContainer( "critical" )
                                .setVisible( flow.isCritical() ),
                            new WebMarkupContainer( "eoisRow" )
                                .add( newEoiList( flow.getElementsOfInformation() ) )
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

    private Component newIncomingFlows( String id, final List<AggregatedFlow> flows ) {
        return new WebMarkupContainer( id )
            .add(
                new ListView<AggregatedFlow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {
                        AggregatedFlow flow = item.getModelObject();

                        item.add(
                            new Label( "flowName2", flow.getFormattedLabel() ),
                            new Label( "flowSources", flow.getSourcesString( getPlanService() ) ),
                            new Label( "flowTiming", lcFirst( flow.getTiming() ) ),
                            new WebMarkupContainer( "critical" )
                                .setVisible( flow.isCritical() ),
                            new WebMarkupContainer( "eoisRow" )
                                .add( newEoiList( flow.getElementsOfInformation() ) )
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

    private static Component newContacts(
        final List<AggregatedContact> sources, List<ResourceSpec> contactSpecs ) {

        return new ListView<ResourceSpec>( "group", contactSpecs ) {
            @Override
            protected void populateItem( ListItem<ResourceSpec> specItem ) {
                ResourceSpec spec = specItem.getModelObject();
                final List<AggregatedContact> contactList = new ArrayList<AggregatedContact>();
                for ( AggregatedContact source : sources )
                    if ( source.getPartSpecs().contains( spec ) )
                            contactList.add( source );

                specItem.add(
                    new WebMarkupContainer( "specAnchor" )
                        .add( new Label( "spec", spec.getReportSource( "" ) )
                                .setRenderBodyOnly( true ) )
                        .add( new AttributeModifier( "name", true,
                                    new Model<String>( "s_" + spec.hashCode() ) ) ),
                    new ListView<AggregatedContact>( "perFlowContact", contactList ) {
                        @Override
                        protected void populateItem( ListItem<AggregatedContact> sourceItem ) {
                            AggregatedContact aContact = sourceItem.getModelObject();
                            AggregatedContact sup = aContact.getSupervisor();

                            MarkupContainer contact = newContact( "contact",
                                                                aContact.getPartSpecs(),
                                                                aContact
                                                                    .getOrganization(),
                                                                aContact.getTitle(),
                                                                aContact.getActor(),
                                                                aContact
                                                                    .getChannels() );
                            Component supervisor = newContact( "supervisor",
                                                             sup.getPartSpecs(),
                                                             sup.getOrganization(),
                                                             sup.getTitle(),
                                                             sup.getActor(),
                                                             sup.getChannels() )
                                .setVisible( sup.getActor() != null );

                            sourceItem.add( contact, supervisor );

                            if ( sourceItem.getIndex() == 0 )
                                contact.add( new AttributeAppender( "class",
                                                                  true,
                                                                  new Model<String>(
                                                                      "first" ),
                                                                  " " ) );
                            if ( sourceItem.getIndex() == getViewSize() - 1 ) {
                                Component component =
                                    sup.getActor() == null ? contact : supervisor;
                                component.add( new AttributeAppender( "class",
                                                                    true,
                                                                    new Model<String>(
                                                                        "last" ),
                                                                    " " ) );
                            }
                        }
                    } );
            }
        };


    }

    private static MarkupContainer newContact(
        String id, List<ResourceSpec> roles, Organization organization, String title, Actor actor,
        List<Channel> channels ) {

        return new WebMarkupContainer( id )
            .add( new Label( "contact.name", actor == null ? "" : actor.getName() ),
                  new Label( "contact.title", title.isEmpty() ? "" : ", " + title ),
                  new Label( "contact.classification",
                             actor == null ? ""
                                     : getClassificationString( actor.getClassifications() ) ),
                  new Label( "contact.organization",
                             organization == null ? "" : organization.toString() ),

                  new WebMarkupContainer( "contactInfos" )
                      .add( new ListView<Channel>( "contactInfo", channels ) {
                          @Override
                          protected void populateItem( ListItem<Channel> item ) {
                              Channel channel = item.getModelObject();
                              item.add( new Label( "channelType",
                                                   channel.getMedium().getLabel() + ":" ),
                                        new Label( "channel", channel.getAddress() ) );
                          }
                      } ).setVisible( !channels.isEmpty() ),

                  new WebMarkupContainer( "noInfo" ).setVisible( channels.isEmpty() ) );
    }

    private static List<ReportSegment> getSegments( PlanService planService, Specable profile ) {
        List<ReportSegment> result = new ArrayList<ReportSegment>();

        Assignments allAssignments = planService.getAssignments( false );
        Assignments profileAssignments = allAssignments.with( profile );
        Assignments assignedByOthers = profileAssignments.notFrom( profile );
        for ( Segment segment : assignedByOthers.getSegments() ) {
            ReportSegment reportSegment =
                new ReportSegment(
                    planService, profile,
                    result.size() + 1, segment, assignedByOthers.with( segment ) );
            result.add( reportSegment );
        }

        return result;
    }

    //-----------------------------------
    private Component newInputDiv( List<ReportTask> inputs ) {

        return new WebMarkupContainer( "inputDiv" )
           .add( new ListView<ReportTask>( "inputLinks", inputs ) {
               @Override
               protected void populateItem( ListItem<ReportTask> item ) {
                   ReportTask task = item.getModelObject();
                   item.add( new ListView<AggregatedFlow>( "flow", task.getTriggeringFlows() ) {
                       @Override
                       protected void populateItem( ListItem<AggregatedFlow> flowItem ) {
                           AggregatedFlow flow = flowItem.getModelObject();
                           PlanService service = getPlanService();
                           flowItem.add( new WebMarkupContainer( "nFlow" ).add( new Label(
                               "flowName",
                               flow.getLabel() ), new Label( "flowSources", flow.getSourcesString(
                               service ) ) ).setVisible( !flow.isAskedFor() ),
                                         new WebMarkupContainer( "rFlow" ).add( new Label(
                                             "flowName",
                                             flow.getLabel() ), new Label( "flowSources",
                                                                           flow.getSourcesString(
                                                                               service ) ) )
                                             .setVisible( flow.isAskedFor() )

                           );
                       }
                   }.setRenderBodyOnly( true ),
                             new Label( "linkName", task.getTitle() ),
                             newTaskLink( task ) );
               }
           }

           )
           .setVisible( !inputs.isEmpty() );
    }

    //-----------------------------------
    private static Component newTaskLink( ReportTask task ) {
        return new WebMarkupContainer( "link" )
            .add(
                new Label( "linkValue", task.getLabel() )
            )
            .add( new AttributeModifier( "href", true, task.getLink() ) );
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
    private static ListView<ReportTask> newTaskLinks( final List<ReportTask> routines ) {

        return new ListView<ReportTask>( "links", routines ) {
            @Override
            protected void populateItem( ListItem<ReportTask> item ) {
                ReportTask task = item.getModelObject();
                item.add(
                    new Label( "linkName", task.getTask() ),
                    newTaskLink( task ) );
            }
        };
    }

    public static String listToString( List<?> list ) {
        StringWriter writer = new StringWriter();

        for ( int i = 0; i < list.size(); i++ ) {
            writer.append( String.valueOf( list.get( i ) ) );
            if ( i == list.size() - 2 )
                writer.append( " or " );
            else if ( i != list.size() - 1 )
                writer.append( ", " );
        }

        return writer.toString();
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
    private static class ReportSegment implements Serializable {
        private final int seq;
        private final Segment segment;
        private final List<ReportTask> tasks = new ArrayList<ReportTask>();
        private final List<ReportTask> immediates = new ArrayList<ReportTask>();
        private final List<ReportTask> prompted = new ArrayList<ReportTask>();
        private final List<ReportTask> subtasks = new ArrayList<ReportTask>();

        private final List<AggregatedContact> contacts;

        private ReportSegment(
            PlanService planService, Specable profile, int seq, Segment segment,
            Assignments assignments ) {

            this.seq = seq;
            this.segment = segment;

            for ( Assignment assignment : assignments ) {
                ReportTask reportTask = new ReportTask( seq, assignment,
                                                        planService.getAssignments().with( profile ) );
                tasks.add( reportTask );

                if ( Assignments.isImmediate( assignment.getPart(), planService ) ) {
                    immediates.add( reportTask );
                    reportTask.setType( ReportTask.Type.IMMEDIATE );
                }
                else {
                    prompted.add( reportTask );
                    reportTask.setType( ReportTask.Type.PROMPTED );
                }

                List<ReportTask> subs = reportTask.getAllSubtasks();
                for ( ReportTask task : subs ) {
                    subtasks.add( task );
                    tasks.add( task );
                    task.setType( ReportTask.Type.SUBTASK );
                }
            }

            // Renumber
            int i = 1;
            for ( ReportTask task : prompted )
                task.setTaskSeq( i++ );
            for ( ReportTask immediate : immediates )
                immediate.setTaskSeq( i++ );
            for ( ReportTask sub : subtasks )
                sub.setTaskSeq( i++ );

            Collections.sort( tasks, new Comparator<ReportTask>() {
                @Override
                public int compare( ReportTask o1, ReportTask o2 ) {
                    return o1.getTaskSeq() - o2.getTaskSeq();
                }
            } );

            contacts = findContacts( planService, new ResourceSpec( profile ) );
        }

        private List<ResourceSpec> getContactSpecs() {
            Set<ResourceSpec> specs = new HashSet<ResourceSpec>();

            for ( ReportTask task : tasks )
                specs.addAll( task.getContactSpecs() );

            return new ArrayList<ResourceSpec>( specs );
        }

        private List<AggregatedContact> findContacts( PlanService service, ResourceSpec profile ) {

            List<ResourceSpec> contactSpecs = getContactSpecs();

            Map<Actor,AggregatedContact> map = new HashMap<Actor, AggregatedContact>();
            for ( Commitment commitment : service.findAllCommitmentsOf(
                                                profile,
                                                service.getAssignments( false ),
                                                segment.getAllSharingFlows() ) ) {

                Assignment beneficiary = commitment.getBeneficiary();
                Actor actor = beneficiary.getEmployment().getActor();
                AggregatedContact aggregatedContact = map.get( actor );
                if ( aggregatedContact == null )
                    map.put( actor, new AggregatedContact( service, beneficiary, contactSpecs ) );
                else
                    aggregatedContact.merge( service, beneficiary, contactSpecs );
            }

            List<AggregatedContact> result = new ArrayList<AggregatedContact>( map.values() );
            Collections.sort( result );
            return result;
        }

        public int getSeq() {
            return seq;
        }

        public List<ReportTask> getImmediates() {
            return Collections.unmodifiableList( immediates );
        }

        public List<ReportTask> getPrompted() {
            return Collections.unmodifiableList( prompted );
        }

        public Segment getSegment() {
            return segment;
        }

        public List<ReportTask> getTasks() {
            return Collections.unmodifiableList( tasks );
        }

        public String getName() {
            return segment.getName();
        }

        public String getTitle() {
            return "Situation " + seq;
        }

        public IModel<String> getLink() {
            return new Model<String>( "#ep_" + seq );
        }

        public IModel<String> getAnchor() {
            return new Model<String>( "ep_" + seq );
        }

        public String getDescription() {
            return segment.getDescription();
        }

        public List<AggregatedContact> getContacts() {
            return Collections.unmodifiableList( contacts );
        }

        private List<Attachment> getAttachmentsFrom( AttachmentManager attachmentManager ) {

            List<Attachment> attachments = new ArrayList<Attachment>();
            attachments.addAll( attachmentManager.getMediaReferences( segment ) );
            attachments.addAll( attachmentManager.getMediaReferences( segment.getEvent() ) );
            attachments.addAll( attachmentManager.getMediaReferences( segment.getPhase() ) );

            return attachments;
        }

        private String getContactSeq() {
            return seq + "." + ( tasks.size() + 1 );
        }

        public String getContext() {
            return segment.getPhaseEventTitle();
        }
    }

    //================================================
    /**
     *  Some report-specific extra information for an assignment.
     */
    private static class ReportTask implements Serializable {

        private final int phaseSeq;
        private int taskSeq;
        private Type type;
        private final Assignment assignment;
        private final Part part;
        private final List<ReportTask> subtasks = new ArrayList<ReportTask>();

        private ReportTask(
            int phaseSeq, Assignment assignment, Assignments assignments ) {

            this.phaseSeq = phaseSeq;
            this.assignment = assignment;
            part = assignment.getPart();

            // TODO take care of possibility of loops
            for ( Assignment sub : assignments.from( assignment ) )
                subtasks.add( new ReportTask( phaseSeq, sub, assignments ) );
        }

        private List<AggregatedFlow> aggregate( List<Flow> flows, boolean incoming ) {
            Map<String,AggregatedFlow> map = new HashMap<String, AggregatedFlow>();
            for ( Flow flow : flows ) {
                AggregatedFlow aggregatedFlow = map.get( flow.getName() );
                if ( aggregatedFlow == null ) {
                    AggregatedFlow newFlow = new AggregatedFlow( flow, incoming );
                    newFlow.setOrigin( assignment );
                    map.put( flow.getName(), newFlow );
                }
                else
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

        private boolean isStartWithSegment() {
            return part.isStartsWithSegment();
        }

        private String getEventPhase() {
            return lcFirst( String.valueOf( part.getSegment().getEventPhase() ) );
        }

        private boolean getCompletionTime() {
            Delay completionTime = part.getCompletionTime();
            return completionTime != null
                && completionTime.getSeconds() != 0;
        }

        private String getCompletionString() {
            return part.getCompletionTime() == null ? ""
                                                    : String.valueOf( part.getCompletionTime() );
        }

        private boolean isNotification( PlanService planService ) {
            return Assignments.isNotification( part, planService );
        }

        private boolean isRequest() {
            return Assignments.isRequest( part );
        }

        private String getTriggeringSources( PlanService service ) {

            Set<String> sourcesStrings = new HashSet<String>(  );
            for ( AggregatedFlow flow : getTriggeringFlows() )
                sourcesStrings.add( flow.getSourcesString( service ) );
            List<String> sources = new ArrayList<String>( sourcesStrings );
            Collections.sort( sources );

            return listToString( sources );
        }

        private String getRepetition() {
            return isRepeating() ? String.valueOf( part.getRepeatsEvery() )
                                 : "";
        }

        private String getDescription() {
            return ensurePeriod( part.getDescription() );
        }

        private boolean isRepeating() {
            return part.isRepeating();
        }

        private String getTriggeringFlowName() {
            List<AggregatedFlow> triggeringFlows = getTriggeringFlows();
            return triggeringFlows.isEmpty() ? "" : triggeringFlows.get( 0 ).getLabel();
        }

        private List<AggregatedFlow> getTriggeringFlows() {
            List<Flow> result = new ArrayList<Flow>();
            for ( Flow flow : part.getAllSharingReceives() )
                if ( flow.isTriggeringToTarget() ) {
                    Node source = flow.getSource();
                    if ( !source.isConnector()
                         || !( (Connector) source ).getExternalFlows().isEmpty() )
                        result.add( flow );
                }

            return aggregate( result, true );
        }

        public Type getType() {
            return type;
        }

        public void setType( Type type ) {
            this.type = type;
        }

        private boolean isSubtask() {
            return type == Type.SUBTASK;
        }

        private boolean isImmediate() {
            return type == Type.IMMEDIATE;
        }

        private List<Goal> getGains() {
            Collection<Goal> goals = part.getGoals();
            List<Goal> answer = new ArrayList<Goal>( goals.size());
            for ( Goal item : goals )
                if ( item.isPositive() )
                    answer.add( item );

            return answer;
        }

        private List<Goal> getRisks() {
            Collection<Goal> goals = part.getGoals();
            List<Goal> answer = new ArrayList<Goal>( goals.size());
            for ( Goal item : goals )
                if ( !item.isPositive() )
                    answer.add( item );

            return answer;
        }

        private String getRoleString() {
            return getRoleString( part.resourceSpec() );
        }

        private static String getRoleString( ResourceSpec spec ) {
            StringBuilder sb = new StringBuilder();

            if ( !spec.isAnyRole() && !spec.isAnyOrganization() ) {
               sb.append( spec.isAnyRole()
                       ? "Member "
                       : spec.getRole().getName() );
               if ( !spec.isAnyOrganization() ) {
                   Organization org =  spec.getOrganization();
                   sb.append( " at ");
                   sb.append( org.getName() );
               }
            }

            return sb.toString();
        }

        private String getTitle() {
            return part.getTask();
        }

        //-----------------------------------
        private List<AggregatedFlow> getFailures() {
            List<Flow> result = new ArrayList<Flow>();
            for ( Flow flow : part.getAllSharingSends() )
                if ( !flow.isAskedFor() && flow.isIfTaskFails() )
                    result.add( flow );

            return aggregate( result, false );
        }

        //-----------------------------------
        private List<AggregatedFlow> getRequests() {
            List<Flow> result = new ArrayList<Flow>();
            for ( Flow flow : part.getAllSharingSends() )
                if ( flow.isAskedFor() )
                    result.add( flow );

            return aggregate( result, true );
        }

        //-----------------------------------
        private List<AggregatedFlow> getOutgoing() {
            List<Flow> result = new ArrayList<Flow>();

            for ( Flow flow : part.getAllSharingSends() )
                if ( !flow.isAskedFor() && !flow.isIfTaskFails() )
                    result.add( flow );

            return aggregate( result, false );
        }

        //-----------------------------------
        private List<AggregatedFlow> getInputs() {
            List<Flow> inputs = new ArrayList<Flow>();
            for ( Flow flow : part.getAllSharingReceives() )
                if ( !flow.isTriggeringToTarget() )
                    inputs.add( flow );

            return aggregate( inputs, true );
        }

        //-----------------------------------
        private List<ElementOfInformation> getStartingEois() {

            Set<ElementOfInformation> elements = new HashSet<ElementOfInformation>();
            Map<String,ElementOfInformation> seen = new HashMap<String, ElementOfInformation>();
            for ( AggregatedFlow flow : getTriggeringFlows() )
                if ( flow.isTriggeringToTarget() )
                    for ( ElementOfInformation e : flow.getElementsOfInformation() ) {
                        ElementOfInformation old = seen.get( e.getContent() );
                        if ( old == null ) {
                            seen.put( e.getContent(), e );
                            elements.add( e );
                        }
                    }

            List<ElementOfInformation> result = new ArrayList<ElementOfInformation>( elements );
            Collections.sort( result, new Comparator<ElementOfInformation>() {
                @Override
                public int compare( ElementOfInformation o1, ElementOfInformation o2 ) {
                    return o1.getContent().compareToIgnoreCase( o2.getContent() );
                }
            } );
            return result;
        }

        private Model<String> getLink() {
            return new Model<String>( "#t_" + part.getId() );
        }

        private String getLabel() {
            return "Task " + getSeqString();
        }

        private String getSeqString() {
            return Integer.toString( phaseSeq ) + '.' + taskSeq;
        }

        public int getTaskSeq() {
            return taskSeq;
        }

        public void setTaskSeq( int taskSeq ) {
            this.taskSeq = taskSeq;
        }

        public Part getPart() {
            return part;
        }

        public String getTask() {
            return part.getTask();
        }

        public List<ReportTask> getSubtasks() {
            return Collections.unmodifiableList( subtasks );
        }

        private Model<String> getAnchor() {
            return new Model<String>( "t_" + part.getId() );
        }

        private String getTaskSummary() {
            StringWriter w = new StringWriter();
            w.append( String.valueOf( part.getSegment() ) );

            Place location = assignment.getLocation();
            if ( location != null )
                w.append( " in " )
                 .append( String.valueOf( location ) );

            return ensurePeriod( w.toString() );
        }

        public List<ReportTask> getAllSubtasks() {
            List<ReportTask> result = new ArrayList<ReportTask>();
            addAllSubtasks( result );
            return result;
        }

        private void addAllSubtasks( List<ReportTask> tasks ) {
            for ( ReportTask subtask : subtasks )
                if ( !tasks.contains( subtask ) ) {
                    tasks.add( subtask );
                    subtask.addAllSubtasks( tasks );
                    }
        }

        private String getCategoryString() {
            Category category = part.getCategory();
            return category == null ? "" : category.getLabel().toLowerCase();
        }

        private boolean isProhibited() {
            return part.isProhibited();
        }

        private Place getLocation() {
            return part.getLocation();
        }

        private String getLocationString() {
            return getLocation() == null ? ""
                                : ensurePeriod( "This task is located in " + getLocation() );
        }

        private String getTeamSpec() {
            return new ResourceSpec( part ).getReportTitle();
        }

        private boolean isAsTeam() {
            return part.isAsTeam();
        }

        private List<Attachment> getAttachmentsFrom( AttachmentManager attachmentManager ) {
            return attachmentManager.getMediaReferences( part );
        }

        public Set<ResourceSpec> getContactSpecs() {
            Set<ResourceSpec> specs = new HashSet<ResourceSpec>();

            for ( Flow receive : part.getAllSharingReceives() )
                if ( receive.isAskedFor() ) {
                    Node source = receive.getSource();
                    if ( source.isConnector() ) {
                        for ( ExternalFlow flow : ((Connector) source).getExternalFlows() ) {
                            Node externalSource = flow.getSource();
                            if ( !externalSource.isConnector() )
                                specs.add( new ResourceSpec( (Specable) externalSource ) );

                        }
                    } else
                        specs.add( new ResourceSpec( (Specable) source ) );

                }

            for ( Flow send : part.getAllSharingSends() ) {
                Node target = send.getTarget();
                if ( target.isConnector() )
                    for ( ExternalFlow flow : ( (Connector) target ).getExternalFlows() ) {
                        Node externalTarget = flow.getTarget();
                        if ( !externalTarget.isConnector() )
                            specs.add( new ResourceSpec( (Specable) externalTarget ) );
                    }
                else
                    specs.add( new ResourceSpec( (Specable) target ) );
            }

            return specs;
        }

        public enum Type { IMMEDIATE, PROMPTED, SUBTASK }
    }

    //================================================
    private static final class AggregatedContact implements Comparable<AggregatedContact>, Serializable {
        private final Actor actor;
        private AggregatedContact supervisor;
        private final String title;
        private final Organization organization;
        private final Set<Role> roles = new HashSet<Role>();
        private final Set<Channel> channels = new HashSet<Channel>();
        private final Set<ResourceSpec> partSpecs = new HashSet<ResourceSpec>();

        private AggregatedContact() {
            actor = null;
            title = "";
            organization = null;
        }

        private AggregatedContact(
            PlanService service, Assignment assignment, List<ResourceSpec> contactSpecs ) {

            Employment employment = assignment.getEmployment();
            actor = employment.getActor();
            title = employment.getJob().getTitle();
            organization = employment.getOrganization();

            Actor sup = employment.getSupervisor();
            if ( sup != null ) {
                Assignments employments = service.getAssignments().with( sup );
                supervisor = employments.isEmpty() ? new AggregatedContact()
                                                   : new AggregatedContact( service,
                                                                            employments
                                                                                .getAssignments()
                                                                                .get( 0 ),
                                                                            contactSpecs );

            } else
                supervisor = new AggregatedContact();

            merge( service, assignment, contactSpecs );
        }

        public void merge(
            PlanService service, Assignment assignment, List<ResourceSpec> contactSpecs ) {

            Employment employment = assignment.getEmployment();

            ResourceSpec partSpec = assignment.getPart().resourceSpec();
            for ( ResourceSpec contactSpec : contactSpecs ) {
                if ( partSpec.narrowsOrEquals( contactSpec, null ) )
                        partSpecs.add( contactSpec );
            }

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

        public List<ResourceSpec> getPartSpecs() {
            ArrayList<ResourceSpec> specs = new ArrayList<ResourceSpec>( partSpecs );
            Collections.sort( specs );
            return specs;
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
    private static class AggregatedFlow implements Serializable {

        private final String label;
        private final Set<ResourceSpec> sources = new HashSet<ResourceSpec>();
        private final Map<String,ElementOfInformation> eoiIndex =
                    new HashMap<String, ElementOfInformation>();
        private final boolean incoming;
        private final Flow flow;
        private Assignment origin;

        private Delay maxDelay = null;

        private AggregatedFlow( Flow flow, boolean incoming ) {
            label = flow.getName();
            this.flow = flow;
            this.incoming = incoming;
            addFlow( flow );
        }

        private String getSourcesString( PlanService service ) {
            Set<ResourceSpec> set = new HashSet<ResourceSpec>();
            for ( ResourceSpec source : sources )
                set.addAll( actualize( service, source, flow.getRestriction(), origin ) );

            List<ResourceSpec> specList = new ArrayList<ResourceSpec>( set );
            Collections.sort( specList, new Comparator<ResourceSpec>() {
                @Override
                public int compare( ResourceSpec o1, ResourceSpec o2 ) {
                    return o1.toString().compareToIgnoreCase( o2.toString() );
                }
            } );

            List<String> list = new ArrayList<String>( specList.size() );
            for ( ResourceSpec spec : specList )
                list.add( spec.getReportSource( isAll()? "all " : "any " ) );

            if ( flow.getRestriction() != null )
                switch ( flow.getRestriction() ) {
                    case SameTopOrganization:
                    case SameOrganization:
                    case DifferentOrganizations:
                    case DifferentTopOrganizations:
                        return listToString( list );

                    case SameLocation:
                    case DifferentLocations:
                    case Supervisor:
                    case Self:
                    case Other:
                }

            return listToString( list )
                + flow.getRestrictionString( !incoming );
        }

        private String getFormattedLabel() {
            String verb;
            Intent intent = getIntent();
            if ( incoming )
                verb = "{0}";
            else if ( isAskedFor() )
                verb = "Answer about {0}";
            else if ( intent == null )
                verb = "Notify of {0}";
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

        public Assignment getOrigin() {
            return origin;
        }

        public void setOrigin( Assignment origin ) {
            this.origin = origin;
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

        public List<ElementOfInformation> getElementsOfInformation() {
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
