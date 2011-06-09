// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports.responders;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Attachment;
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
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
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

import static com.mindalliance.channels.pages.reports.responders.ParticipantPage.ReportTask.Type.IMMEDIATE;
import static com.mindalliance.channels.pages.reports.responders.ParticipantPage.ReportTask.Type.PROMPTED;
import static com.mindalliance.channels.pages.reports.responders.ParticipantPage.ReportTask.Type.SUBTASK;

/** The participant report.  This page is different for every user. */
public class ParticipantPage extends WebPage {

    private static final Logger LOG = LoggerFactory.getLogger( ParticipantPage.class );

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
     * Called for access without parameters. Find the actor and plan corresponding to the current
     * user and redirect to that page. Otherwise, redirect to access denied.
     */
    private ParticipantPage() {

        try {
            PlanService service = createPlanService();
            Plan plan = service.getPlan();
            setRedirect( true );
            setResponsePage(
                ParticipantPage.class,
                createParameters(
                    user.isPlanner( plan.getUri() ) ? new ResourceSpec()
                                                    : getProfile( service, user ),
                    plan.getUri(),
                    plan.getVersion() ) );

        } catch ( NotFoundException e ) {
            // User has no participant page
            LOG.info( user.getFullName() + " not a participant", e );
            throw new RedirectToUrlException( "/static/nonParticipant.html" );
        }
    }

    public ParticipantPage( PageParameters parameters ) {

        super( parameters );
        try {
            String uri = parameters.getString( "plan" );
            if ( user.isPlanner( uri ) && parameters.size() == 2 ) {
                setRedirect( false );
                setResponsePage( AllParticipants.class, parameters );
            } else {
                PlanService service = initPlanService( uri, parameters.getInt( "v" ) );
                init( service, getProfile( service, parameters ),
                      parameters.getString( "user", null ) );
            }
        } catch ( StringValueConversionException e ) {
            LOG.info( "Bad parameter: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
        } catch ( NotFoundException e ) {
            LOG.info( "Not found: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    private static List<ElementOfInformation> sortedEOIs( List<ElementOfInformation> result ) {

        Collections.sort( result, new Comparator<ElementOfInformation>() {
            @Override
            public int compare( ElementOfInformation o1, ElementOfInformation o2 ) {
                return o1.getContent().compareToIgnoreCase( o2.getContent() );
            }
        } );

        return result;
    }

    //-----------------------------------
    private PlanService initPlanService( final String uri, final int version )
        throws NotFoundException {

        if ( planManager.getPlan( uri, version ) == null )
            throw new NotFoundException();

        setDefaultModel(
            new LoadableDetachableModel<PlanService>() {
                @Override
                protected PlanService load() {
                    return new PlanService(
                        planManager, null, userService, planManager.getPlan( uri, version ) );
                }
            } );
        return (PlanService) getDefaultModelObject();
    }

    private PlanService getPlanService() {
        return (PlanService) getDefaultModelObject();
    }

    //-----------------------------------
    private void init( PlanService service, ResourceSpec profile, String override ) {

        Plan plan = service.getPlan();

        AggregatedContact contact =
            user.isPlanner( plan.getUri() ) ? new AggregatedContact( service, profile.getActor(), override )
                                            : new AggregatedContact( service, profile.getActor(), user.getUsername() );

        List<ReportSegment> reportSegments = getSegments( service, profile );
        add(
            new UserFeedbackPanel( "planFeedback", plan, "Send overall feedback" ),
/*
            new Label( "userName", user.getUsername() ),
*/
            new Label( "personName", contact.getActorName() ),
            new Label( "planName", plan.getName() ),
            new Label( "planName2", plan.getName() ),
            new Label( "planVersion", "v" + plan.getVersion() ),
            new Label( "planDescription", plan.getDescription() ),

            newContact( "contact", contact )
                .add( new Label( "myAvail", contact.getAvailability() ) )
                .setRenderBodyOnly( true ),

            new ListView<ReportSegment>( "phaseLinks", reportSegments ) {
                @Override
                protected void populateItem( ListItem<ReportSegment> item ) {
                    item.getModelObject().addLinkTo( item );
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
                PlanService service = getPlanService();
                ReportSegment segment = item.getModelObject();
                item.add(
                    new WebMarkupContainer( "phaseAnchor" )
                        .add( new Label( "phaseText", segment.getName() ) )
                        .add( new AttributeModifier( "name", true, segment.getAnchor() ) ),
                    new UserFeedbackPanel( "segmentFeedback", segment.getSegment(), "Send feedback" ),

                    new Label( "context", segment.getContext() ),
                    new Label( "segDesc", ensurePeriod( segment.getDescription() ) )
                        .setVisible( !segment.getDescription().isEmpty() ),
                    new Label( "phaseSeq", Integer.toString( segment.getSeq() ) ),
                    new WebMarkupContainer( "routineDiv" )
                        .add( newTaskLinks( segment.getImmediates() ) )
                        .setVisible( !segment.getImmediates().isEmpty() ),
                    newInputDiv( segment.getPrompted() ),
                    newDocSection( segment.getAttachmentsFrom( planManager.getAttachmentManager() ) ),

                    newTasks( segment, segment.getTasks() ),
                    new WebMarkupContainer( "contactList" )
                        .add(
                            new Label( "contactSeq", segment.getContactSeq() ),
                            new Label( "contactTitle", segment.getName() ),
                            new UserFeedbackPanel(
                                    "contactListFeedback",
                                    segment.getSegment(),
                                    "Send feedback",
                                    "Contact list"),
                            newContacts( segment.getContactSpecs( service ) ) )
                        .setVisible( !segment.getContacts().isEmpty() ) );
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

                item.add(
                    new WebMarkupContainer( "taskAnchor" )
                        .add( new Label( "taskName", task.getTitle() ) )
                        .add( new AttributeModifier( "name", true, task.getAnchor() ) ),
                    new UserFeedbackPanel( "taskFeedback", task.getPart(), "Send feedback" ),
                    new WebMarkupContainer( "backTask" )
                        .add( new AttributeModifier( "href", true, segment.getLink() ) ),
                    new Label( "taskSeq", task.getSeqString() ),
                    new Label( "taskSummary", "The context is " + lcFirst( task.getTaskSummary() ) ),
                    new Label( "taskRole", ensurePeriod( task.getRoleString() ) ),
                    new Label( "taskLoc", task.getLocationString() )
                        .setVisible( task.getLocation() != null ),
                    new Label( "taskRecur", task.getRepetition() ).setVisible( task.isRepeating() ),
                    newPromptedByDiv( task, planService ),
                    newDetailsDiv( task ),
                    new WebMarkupContainer( "prohibited" ).setVisible( task.isProhibited() ),
                    new WebMarkupContainer( "teamDiv" )
                        .add( new Label( "teamSpec", task.getTeamSpec() ) )
                        .setVisible( task.isAsTeam() ),
                    new WebMarkupContainer( "subtaskDiv" )
                        .add( newTaskLinks( task.getSubtasks() ) )
                        .setVisible( !task.getSubtasks().isEmpty() ),
                    newIncomingFlows( task.getInputs( planService ) ),
                    newOutgoingFlows( "distribDiv", task.getOutgoing( planService ) ),
                    newOutgoingFlows( "taskRfiDiv", task.getRequests( planService ) ),
                    newOutgoingFlows( "failDiv", task.getFailures( planService ) ),
                    newDocSection(
                        task.getAttachmentsFrom( planService.getAttachmentManager() ) ) );
            }
        };
    }

    private Component newDetailsDiv( ReportTask task ) {

        // TODO hide details when empty
        List<Goal> risks = task.getRisks();
        List<Goal> gains = task.getGains();
        return new WebMarkupContainer( "details" )
            .add( new Label( "instruct", task.getDescription() )
                      .setVisible( !task.getDescription().isEmpty() ),
                  new WebMarkupContainer( "routineTask" )
                      .add( new Label( "taskRecur",
                                       task.isRepeating() ?
                                       "It is repeated every " + task.getRepetition() + '.' :
                                       "" ).setVisible( task.isRepeating() ) )
                      .setVisible( task.isImmediate() ),
                  new WebMarkupContainer( "taskDuration" ).add( new Label( "dur",
                                                                           task.getCompletionString() ) ).setVisible(
                      task.getCompletionTime() ),
                  new WebMarkupContainer( "term1" )/*.add( new Label( "eventPhase",
                                                                    task.getEventPhase() ) )*/.setVisible(
                      task.isStartWithSegment() ),

                  // TODO super-event override notice
                  new WebMarkupContainer( "superNote" ).setVisible( false ),

                  new WebMarkupContainer( "riskDiv" ).add( new ListView<Goal>( "risks", risks ) {
                      @Override
                      protected void populateItem( ListItem<Goal> item ) {

                          item.add( new Label( "type", item.getModelObject().getPartialTitle() ) );
                      }
                                                           }, new ListView<Goal>( "gains", gains ) {
                                                               @Override
                                                               protected void populateItem(
                                                                   ListItem<Goal> item ) {

                                                                   item.add( new Label( "type",
                                                                                        item.getModelObject().getFullTitle() ) );
                                                               }
                                                           },
                                                           // TODO Add consequences of task failure
                                                           new WebMarkupContainer( "conseqs" ).setVisible(
                                                               false )
                  ).setVisible( !risks.isEmpty() || !gains.isEmpty() ),

                  new WebMarkupContainer( "subTask" ).add( newSimpleEoiList( task.getStartingEois(
                      getPlanService() ) ) ).setVisible(
                      task.isSubtask() ) )

            .setVisible( !risks.isEmpty() || !gains.isEmpty() || task.isStartWithSegment()
                         || task.isImmediate() || task.isSubtask() || task.getCompletionTime() );
    }

    private Component newPromptedByDiv( ReportTask task, PlanService planService ) {

        // TODO handle tasks prompted by multiple different requests or notification mixes

        boolean notification = task.isNotification( planService );
        return new WebMarkupContainer( "promptedBy" )
            .add(
                new ListView<AggregatedFlow>( "notifTask", task.getTriggeringFlows( planService ) ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {
                        AggregatedFlow flow = item.getModelObject();
                        List<ElementOfInformation> eois = flow.getElementsOfInformation();
                        String sourcesString = flow.getSourcesString( getPlanService() );
                        item.add(
                            new WebMarkupContainer( "notifTask1" )
                                .add(
                                    new Label( "reqFlow", flow.getLabel() ),
                                    new Label( "reqFlowSrc", sourcesString ) )
                                .setVisible( eois.isEmpty() ),
                            new WebMarkupContainer( "notifTask2" )
                                .add(
                                    new Label( "reqFlow", flow.getLabel() ),
                                    new Label( "reqFlowSrc", sourcesString ),
                                  newSimpleEoiList( eois )
                                )
                                .setVisible( !eois.isEmpty() ) );
                    }
                }.setVisible( notification ),

                new WebMarkupContainer( "reqTask" )
                    .add(
                        new Label(
                            "taskType", task.getCategoryString() ).setRenderBodyOnly( true ),
                        new Label( "reqFlow", task.getTriggeringFlowName( planService ) ),
                        new Label( "reqFlowSrc", task.getTriggeringSources( planService ) ) )
                    .setVisible( task.isRequest() )
            )

            .setVisible( notification || task.isRequest() );
    }

    private static String lcFirst( String phrase ) {

        if ( phrase.length() < 2 )
            return phrase;
        return ChannelsUtils.smartUncapitalize( phrase );
    }

    private static String ensurePeriod( String sentence ) {

        return sentence == null || sentence.isEmpty() || sentence.endsWith( "." )
               || sentence.endsWith( ";" ) ?
                    sentence :
                    sentence + '.';
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
                            new Label(
                                "flowTargets",
                                ensurePeriod( flow.getSourcesString( getPlanService() ) ) ),
                            new Label( "flowTiming", lcFirst( flow.getTiming() ) ),
                            new WebMarkupContainer( "critical" ).setVisible( flow.isCritical() ),
                            new UserFeedbackPanel( "outgoingFlowFeedback", flow.getFlow(), "Send feedback" ),
                            new WebMarkupContainer( "eoisRow" ).add( newEoiList( flow.getElementsOfInformation() ) ).setRenderBodyOnly(
                                true ).setVisible( flow.hasEois() ),

                            new WebMarkupContainer( "flowEnding" ).setVisible( flow.isTerminatingToSource() ),

                            // TODO ask JF about where to get this
                            new WebMarkupContainer( "noContext" ).setVisible( false ) );
                    }
                }.setRenderBodyOnly( true ) )
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
                item.add(
                    new Label( "eoi.name", eoi.getContent() ),
                    new Label( "eoi.desc", notAvailable( ensurePeriod( eoi.getDescription() ) ) ),
                    new Label( "eoi.handling", notAvailable( eoi.getSpecialHandling() ) ),
                    new Label( "eoi.class", getClassificationString( eoi.getClassifications() ) ) );

                if ( item.getIndex() == 0 )
                    item.add(
                        new AttributeAppender( "class", true, new Model<String>( "first" ), " " ) );
                if ( item.getIndex() == getViewSize() - 1 )
                    item.add(
                        new AttributeAppender( "class", true, new Model<String>( "last" ), " " ) );
            }
        };
    }

    private static String notAvailable( String description ) {

        return description == null || description.isEmpty() ? AggregatedContact.N_A : description;
    }

    //-----------------------------------
    private static String getClassificationString( List<Classification> classifications ) {

        return classifications.isEmpty() ? AggregatedContact.N_A : listToString( classifications,
                                                                                 " or " );
    }

    private Component newIncomingFlows( final List<AggregatedFlow> flows ) {

        return new WebMarkupContainer( "criticalDiv" )
            .add(
                new ListView<AggregatedFlow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {

                        AggregatedFlow flow = item.getModelObject();
                        item.add(
                            new Label( "flowName2", flow.getFormattedLabel() ),
                            new Label( "flowSources", flow.getSourcesString( getPlanService() ) ),
                            new Label( "flowTiming", lcFirst( flow.getTiming() ) ),
                            new WebMarkupContainer( "critical" ).setVisible( flow.isCritical() ),
                            new UserFeedbackPanel( "incomingFeedback", flow.getFlow(), "Send feedback" ),
                            new WebMarkupContainer( "eoisRow" ).add( newEoiList( flow.getElementsOfInformation() ) ).setRenderBodyOnly(
                                true ).setVisible( flow.hasEois() ),
                            new WebMarkupContainer( "flowEnding" ).setVisible( flow.isTerminatingToTarget() ) );
                    }
                }.setRenderBodyOnly( true ) )
            .setVisible( !flows.isEmpty() );
    }

    private static Component newContacts( List<ContactSpec> contactSpecs ) {

        return new ListView<ContactSpec>( "group", contactSpecs ) {
            @Override
            protected void populateItem( ListItem<ContactSpec> specItem ) {

                ContactSpec spec = specItem.getModelObject();
                final List<AggregatedContact> contactList = spec.getContactList();
                if ( contactList.isEmpty() )
                    contactList.add(  new AggregatedContact() );

                specItem.add(
                    new WebMarkupContainer( "specAnchor" )
                        .add( new Label( "spec", spec.getLabel() )
                                  .setRenderBodyOnly( true ) )
                        .add( new AttributeModifier( "name",
                                                     true,
                                                     new Model<String>( "s_" + spec.hashCode() ) ) ),

                    new ListView<AggregatedContact>( "perFlowContact", contactList ) {
                        @Override
                        protected void populateItem( ListItem<AggregatedContact> sourceItem ) {

                            AggregatedContact contact = sourceItem.getModelObject();
                            AggregatedContact supervisor = contact.getSupervisor();
                            MarkupContainer contactComp = newContact( "contact", contact );
                            Component superComp = newContact( "supervisor", supervisor )
                                                      .setVisible( supervisor.getActor() != null );

                            sourceItem.add( contactComp, superComp );
                            if ( sourceItem.getIndex() == 0 )
                                contactComp.add( new AttributeAppender( "class",
                                                                    true,
                                                                    new Model<String>( "first" ),
                                                                    " " ) );
                            if ( sourceItem.getIndex() == getViewSize() - 1 ) {
                                Component component = supervisor.getActor() == null ?
                                                      contactComp : superComp;
                                component.add( new AttributeAppender( "class",
                                                                      true,
                                                                      new Model<String>( "last" ),
                                                                      " " ) );
                          }
                      }
                  } );
            }
        };
    }

    private static MarkupContainer newContact( String id, AggregatedContact contact ) {
        List<Channel> channels = contact.getChannels();

        return new WebMarkupContainer( id )
            .add( new UserFeedbackPanel(
                    "contactFeedback",
                    contact.getParticipation(),
                    "Send feedback" ),
                    new Label( "contact.name", contact.getActorName() ),

                  new Label( "contact.roles", contact.getRoles() ),
                  new Label( "contact.classification", contact.getClassifications() ),

                  new WebMarkupContainer( "contactInfos" )
                      .add( new ListView<Channel>( "contactInfo", channels ) {
                          @Override
                          protected void populateItem( ListItem<Channel> item ) {

                              Channel channel = item.getModelObject();
                              String label = channel.getMedium().getLabel();
                              boolean isEmail = "Email".equals( label );
                              String address = channel.getAddress();
                              item.add(
                                  new WebMarkupContainer( "mail" )
                                      .add(
                                          new WebMarkupContainer( "mailTo" )
                                            .add( new Label( "channel", address ) )
                                            .add( new AttributeModifier( "href", true,
                                                    new Model<String>( "mailTo:" + address ) ) )

                                      )
                                      .setRenderBodyOnly( true )
                                      .setVisible( isEmail ),
                                  new WebMarkupContainer( "notMail" )
                                      .add(
                                          new Label( "channelType", label + ':' ),
                                          new Label( "channel", address )
                                      )
                                      .setRenderBodyOnly( true )
                                      .setVisible( !isEmail )
                              );
                          }
                      } ).setVisible( !channels.isEmpty() ),

                  new WebMarkupContainer( "noInfo" ).setVisible( channels.isEmpty() )  );
    }

    private static List<ReportSegment> getSegments( PlanService planService, Specable profile ) {

        List<ReportSegment> result = new ArrayList<ReportSegment>();
        Assignments allAssignments = planService.getAssignments( false, false );
        Assignments myAssignments = allAssignments.with( profile );
        Assignments assignedByOthers = myAssignments.notFrom( profile );

        List<Flow> allFlows = planService.findAllFlows();
        for ( Segment segment : assignedByOthers.getSegments() )
            result.add( new ReportSegment(
                                planService,
                                profile,
                                result.size() + 1,
                                segment,
                                assignedByOthers.with( segment ), allAssignments, allFlows ) );
        return result;
    }

    //-----------------------------------
    private Component newInputDiv( List<ReportTask> inputs ) {

        return new WebMarkupContainer( "inputDiv" )
            .add(
                new ListView<ReportTask>( "inputLinks", inputs ) {
                    @Override
                    protected void populateItem( ListItem<ReportTask> item ) {



                        PlanService service = getPlanService();
                        ReportTask task = item.getModelObject();
                        item.add(
                            new ListView<AggregatedFlow>( "flow", task.getTriggeringFlows( service ) ) {
                                @Override
                                protected void populateItem( ListItem<AggregatedFlow> flowItem ) {

                                    AggregatedFlow flow = flowItem.getModelObject();
                                    PlanService service = getPlanService();
                                    flowItem.add(
                                        new WebMarkupContainer( "nFlow" )
                                            .add( new Label( "flowName", flow.getLabel() ),
                                                  new Label( "flowSources",
                                                             flow.getSourcesString( service ) ) )
                                            .setVisible( !flow.isAskedFor() ),
                                        new WebMarkupContainer( "rFlow" )
                                            .add( new Label( "flowName", flow.getLabel() ),
                                                  new Label( "flowSources",
                                                             flow.getSourcesString( service ) ) )
                                            .setVisible( flow.isAskedFor() ) );
                                }
                            }.setRenderBodyOnly( true ),

                            new Label( "linkName", task.getTitle() ),
                            newTaskLink( task ) );
                    }
                } )
            .setVisible( !inputs.isEmpty() );
    }

    //-----------------------------------
    private static Component newTaskLink( ReportTask task ) {

        return new WebMarkupContainer( "link" )
            .add( new Label( "linkValue", task.getLabel() ) )
            .add( new AttributeModifier( "href", true, task.getLink() ) );
    }

    //-----------------------------------
    private static Component newDocSection( final List<Attachment> attachments ) {

        return new WebMarkupContainer( "documents" )
            .add(
                new ListView<Attachment>( "document", attachments ) {
                    @Override
                    protected void populateItem( ListItem<Attachment> item ) {

                        Attachment attachment = item.getModelObject();
                        item.add(
                            new ExternalLink(
                                "docLink", attachment.getUrl(), attachment.getLabel() ) );
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
                item.add( new Label( "linkName", task.getTask() ), newTaskLink( task ) );
            }
        };
    }

    private static String listToString( List<?> list, String lastSep ) {

        StringWriter w = new StringWriter();
        for ( int i = 0; i < list.size(); i++ ) {
            w.append( String.valueOf( list.get( i ) ) );
            if ( i == list.size() - 2 )
                w.append( lastSep );
            else if ( i != list.size() - 1 )
                w.append( ", " );
        }
        return w.toString();
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
                service.find( Actor.class, parameters.getLong( "agent" ) ) :
                null;
            Role role = parameters.containsKey( "role" ) ?
                service.find( Role.class, parameters.getLong( "role" ) ) :
                null;
            Organization organization = parameters.containsKey( "org" ) ?
                service.find( Organization.class, parameters.getLong( "org" ) ) :
                null;
            Place jurisdiction = parameters.containsKey( "place" ) ?
                service.find( Place.class, parameters.getLong( "place" ) ) :
                null;
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
     *
     * @return a service for the first readable plan in which the user is an active participant or a
     *         planner.
     * @throws NotFoundException when no adequate plan was found
     */
    private PlanService createPlanService() throws NotFoundException {

        for ( Plan readablePlan : planManager.getReadablePlans( user ) ) {
            PlanService service = new PlanService( planManager, null, userService, readablePlan );
            if ( user.isPlanner( readablePlan.getUri() )
                 || service.findParticipation( user.getUsername() ) != null )
                return service;
        }
        throw new NotFoundException();
    }

    //================================================
    private static class ContactSpec implements Serializable, Comparable<ContactSpec> {

        private final ResourceSpec spec;
        private final Restriction restriction;
        private final Set<AggregatedContact> contacts = new HashSet<AggregatedContact>();

        private ContactSpec( Restriction restriction, ResourceSpec spec ) {
            assert spec != null;
            this.restriction = restriction;
            this.spec = spec;
        }

        private boolean isAllowed( Organization committer, Organization beneficiary ) {

            if ( restriction == null )
                return true;

            switch ( restriction ) {
                case Supervisor:
                case SameTopOrganization:
                    return ModelObject.isNullOrUnknown( committer )
                           || ModelObject.isNullOrUnknown( beneficiary )
                           || committer.getTopOrganization().equals(
                                beneficiary.getTopOrganization() );
                case SameOrganization:
                    return committer != null && committer.equals( beneficiary );
                case DifferentOrganizations:
                    return ModelObject.isNullOrUnknown( committer )
                           || ModelObject.isNullOrUnknown( beneficiary )
                           || !committer.narrowsOrEquals( beneficiary, null )
                              && !beneficiary.narrowsOrEquals( committer, null );
                case DifferentTopOrganizations:
                    return ModelObject.isNullOrUnknown( committer )
                           || ModelObject.isNullOrUnknown( beneficiary )
                           || !committer.getTopOrganization().equals(
                                beneficiary.getTopOrganization() );
                default:
                    return true;
            }
        }

        private List<ContactSpec> actualize( PlanService service, Specable origin ) {

            Organization org = spec.getOrganization();
            if ( org != null && !org.isUnknown() && !org.isActual() ) {
                List<ContactSpec> result = new ArrayList<ContactSpec>();
                for ( ModelEntity entity : service.findAllNarrowingOrEqualTo( org ) )
                    if ( entity.isActual() ) {
                        Organization actualOrg = (Organization) entity;
                        if ( restriction == null || isAllowed( origin.getOrganization(),
                                                               actualOrg ) ) {
                            ContactSpec newSpec = new ContactSpec( restriction,
                                                                   new ResourceSpec( spec.getActor(),
                                                                                     spec.getRole(),
                                                                                     actualOrg,
                                                                                     spec.getJurisdiction() ) );
                            newSpec.addContacts( contacts );
                            result.add( newSpec );
                        }
                    }
                if ( result.size() < 2 )
                    return result;
            }

            List<ContactSpec> result = new ArrayList<ContactSpec>();
            result.add( this );
            return result;
        }

        private void addContacts( Set<AggregatedContact> contacts ) {
            this.contacts.addAll( contacts );
        }

        private String getLabel() {
            return spec.getReportSource( "" ) + ( restriction == null ? "" : " if " + restriction );
        }

        public Restriction getRestriction() {
            return restriction;
        }

        public ResourceSpec getSpec() {
            return spec;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;

            if ( obj == null || getClass() != obj.getClass() )
                return false;

            ContactSpec other = (ContactSpec) obj;
            return restriction == other.getRestriction() && spec.equals( other.getSpec() );
        }

        @Override
        public int hashCode() {
            int result = spec.hashCode();
            result = 31 * result + ( restriction != null ? restriction.hashCode() : 0 );
            return result;
        }

        @Override
        public int compareTo( ContactSpec o ) {
            int i = spec.compareTo( o.getSpec() );
            return i == 0 ?
                   restriction != null ?
                       restriction.compareTo( o.getRestriction() ) :
                       o.getRestriction() == null ? 0 : -1 :
                   i;
        }

        @Override
        public String toString() {
            return getLabel();
        }

        public Set<AggregatedContact> getContacts() {
            return Collections.unmodifiableSet( contacts );
        }

        public List<AggregatedContact> getContactList() {
            List<AggregatedContact> result = new ArrayList<AggregatedContact>( contacts );
            Collections.sort( result );
            return result;
        }

        private void addContact( PlanService service, Assignment beneficiary ) {
            contacts.add( new AggregatedContact( service, beneficiary ) );
        }
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
            Assignments assignments, Assignments allAssignments, List<Flow> allFlows ) {

            this.seq = seq;
            this.segment = segment;
            for ( Assignment assignment : assignments ) {
                ReportTask reportTask =
                    new ReportTask( seq, assignment,
                                    assignments, planService, allAssignments, allFlows );

                tasks.add( reportTask );
                if ( Assignments.isImmediate( assignment.getPart(), planService ) ) {
                    immediates.add( reportTask );
                    reportTask.setType( IMMEDIATE );
                } else {
                    prompted.add( reportTask );
                    reportTask.setType( PROMPTED );
                }

                for ( ReportTask task : reportTask.getAllSubtasks() ) {
                    subtasks.add( task );
                    tasks.add( task );
                    task.setType( SUBTASK );
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

            contacts = findContacts( planService );
        }

        private List<ContactSpec> getContactSpecs( PlanService service ) {

            Set<ContactSpec> specs = new HashSet<ContactSpec>();
            for ( ReportTask task : tasks )
                for ( ContactSpec taskSpec : task.getContactSpecs( service ) )
                    specs.addAll( taskSpec.actualize( service, task.getAssignment() ) );

            List<ContactSpec> result = new ArrayList<ContactSpec>( specs );
            Collections.sort( result );
            return result;
        }

        private List<AggregatedContact> findContacts( PlanService service ) {

            List<ContactSpec> contactSpecs = getContactSpecs( service );
            List<AggregatedContact> result = new ArrayList<AggregatedContact>( contactSpecs.size() );
            for ( ContactSpec contactSpec : contactSpecs )
                result.addAll( contactSpec.getContacts() );

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

        private void addLinkTo( WebMarkupContainer item ) {

            item.add(
                new Label( "phaseName", getName() ),
                new WebMarkupContainer( "phaseLink" )
                    .add(
                        new Label( "phaseLinkText", getTitle() ).setRenderBodyOnly( true ) )
                    .add( new AttributeModifier( "href", true, getLink() ) ) );
        }

        public Segment getSegment() {
            return segment;
        }
    }

    //================================================
    /** Some report-specific extra information for an assignment. */
    public static class ReportTask implements Serializable {

        private final int phaseSeq;
        private int taskSeq;
        private Type type;
        private final Assignment assignment;
        private final Part part;
        private final List<ReportTask> subtasks = new ArrayList<ReportTask>();
        private final List<Commitment> commitmentsOf;
        private final List<Commitment> commitmentsTo;

        private ReportTask(
            int phaseSeq, Assignment assignment, Assignments assignments, QueryService queryService,
            Assignments allAssignments, List<Flow> allFlows ) {

            this.phaseSeq = phaseSeq;
            this.assignment = assignment;
            part = assignment.getPart();
            // TODO take care of possibility of loops
            for ( Assignment sub : assignments.from( assignment ) )
                subtasks.add( new ReportTask( phaseSeq, sub, assignments, queryService, allAssignments, allFlows ) );
            commitmentsOf = queryService.findAllCommitmentsOf( assignment, allAssignments, allFlows );
            commitmentsTo = queryService.findAllCommitmentsTo( assignment, allAssignments, allFlows );

        }

        public Assignment getAssignment() {
            return assignment;
        }

        private List<AggregatedFlow> aggregate(
            List<Flow> flows, boolean incoming, PlanService service ) {

            List<AggregatedFlow> result = new ArrayList<AggregatedFlow>( flows.size() );
            Map<String,AggregatedFlow> flowMap = new HashMap<String, AggregatedFlow>();

            Collection<Commitment> commitments = incoming? commitmentsTo : commitmentsOf;
            for ( Flow flow : flows ) {
                AggregatedFlow old = flowMap.get( flow.getName() );
                if ( old == null ) {
                    AggregatedFlow newFlow = new AggregatedFlow( flow, incoming, commitments,
                                                                 service );
                    newFlow.setOrigin( assignment );
                    flowMap.put( flow.getName(), newFlow );
                    result.add( newFlow );
                }
                else
                    old.addFlow( flow, commitments, service );
            }

            Collections.sort(
                result, new Comparator<AggregatedFlow>() {
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

        private boolean getCompletionTime() {
            Delay completionTime = part.getCompletionTime();
            return completionTime != null && completionTime.getSeconds() != 0;
        }

        private String getCompletionString() {
            return part.getCompletionTime() == null ?
                "" :
                String.valueOf( part.getCompletionTime() );
        }

        private boolean isNotification( PlanService planService ) {
            return Assignments.isNotification( part, planService );
        }

        private boolean isRequest() {
            return Assignments.isRequest( part );
        }

        private String getTriggeringSources( PlanService service ) {

            Set<String> sourcesStrings = new HashSet<String>();
            for ( AggregatedFlow flow : getTriggeringFlows( service ) )
                sourcesStrings.add( flow.getSourcesString( service ) );
            List<String> sources = new ArrayList<String>( sourcesStrings );
            Collections.sort( sources );
            return listToString( sources, " or " );
        }

        private String getRepetition() {
            return isRepeating() ? "This is repeated every " + part.getRepeatsEvery() + '.' : "";
        }

        private String getDescription() {
            return ensurePeriod( part.getDescription() );
        }

        private boolean isRepeating() {
            return part.isRepeating();
        }

        private String getTriggeringFlowName( PlanService service ) {
            List<AggregatedFlow> triggeringFlows = getTriggeringFlows( service );
            return triggeringFlows.isEmpty() ? "" : triggeringFlows.get( 0 ).getLabel();
        }

        private List<AggregatedFlow> getTriggeringFlows( PlanService service ) {

            List<Flow> result = new ArrayList<Flow>();
            for ( Commitment commitment : commitmentsTo ) {
                Flow flow = commitment.getSharing();
                if ( flow.isTriggeringToTarget() ) {
                    Node source = flow.getSource();
                    if ( !source.isConnector()
                         || !( (Connector) source ).getExternalFlows().isEmpty() )
                        result.add( flow );
                }
            }
            return aggregate( result, true, service );
        }

        public void setType( Type type ) {
            this.type = type;
        }

        private boolean isSubtask() {
            return type == SUBTASK;
        }

        private boolean isImmediate() {
            return type == IMMEDIATE;
        }

        private List<Goal> getGains() {

            Collection<Goal> goals = part.getGoals();
            List<Goal> answer = new ArrayList<Goal>( goals.size() );
            for ( Goal item : goals )
                if ( item.isPositive() )
                    answer.add( item );
            return answer;
        }

        private List<Goal> getRisks() {

            Collection<Goal> goals = part.getGoals();
            List<Goal> answer = new ArrayList<Goal>( goals.size() );
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
                sb.append( spec.isAnyRole() ? "Member " : spec.getRole().getName() );
                if ( !spec.isAnyOrganization() ) {
                    Organization org = spec.getOrganization();
                    sb.append( " at " );
                    sb.append( org.getName() );
                }
            }
            return sb.toString();
        }

        private String getTitle() {
            return part.getTask();
        }

        //-----------------------------------
        private List<AggregatedFlow> getFailures( PlanService service ) {

            List<Flow> result = new ArrayList<Flow>();
            for ( Commitment commitment : commitmentsOf ) {
                Flow flow = commitment.getSharing();
                if ( !flow.isAskedFor() && flow.isIfTaskFails() )
                    result.add( flow );
            }

            return aggregate( result, false, service );
        }

        //-----------------------------------
        private List<AggregatedFlow> getRequests( PlanService service ) {

            List<Flow> result = new ArrayList<Flow>();

            for ( Commitment commitment : commitmentsOf ) {
                Flow flow = commitment.getSharing();
                if ( flow.isAskedFor() )
                    result.add( flow );
            }

            return aggregate( result, true, service );
        }

        //-----------------------------------
        private List<AggregatedFlow> getOutgoing( PlanService service ) {
            List<Flow> result = new ArrayList<Flow>();
            for ( Commitment commitment : commitmentsOf ) {
                Flow flow = commitment.getSharing();
                if ( !flow.isAskedFor() && !flow.isIfTaskFails() )
                    result.add( flow );
            }
            return aggregate( result, false, service );
        }

        //-----------------------------------
        private List<AggregatedFlow> getInputs( PlanService service ) {
            List<Flow> inputs = new ArrayList<Flow>();
            for ( Commitment commitment : commitmentsTo ) {
                Flow flow = commitment.getSharing();
                if ( !flow.isTriggeringToTarget() )
                    inputs.add( flow );
            }

            return aggregate( inputs, true, service );
        }

        //-----------------------------------
        private List<ElementOfInformation> getStartingEois( PlanService service ) {

            Set<ElementOfInformation> elements = new HashSet<ElementOfInformation>();
            Map<String, ElementOfInformation> seen = new HashMap<String, ElementOfInformation>();
            for ( AggregatedFlow flow : getTriggeringFlows( service ) )
                if ( flow.isTriggeringToTarget() )
                    for ( ElementOfInformation e : flow.getElementsOfInformation() ) {
                        ElementOfInformation old = seen.get( e.getContent() );
                        if ( old == null ) {
                            seen.put( e.getContent(), e );
                            elements.add( e );
                        }
                    }
            return sortedEOIs( new ArrayList<ElementOfInformation>( elements ) );
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
            w.append( String.valueOf( part.getSegment().getPhaseEventTitle() ) );
            Place location = assignment.getLocation();
            if ( location != null )
                w.append( " in " ).append( String.valueOf( location ) );
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

            return getLocation() == null ?
                "" :
                ensurePeriod( "This task is located in " + getLocation() );
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

        public Set<ContactSpec> getContactSpecs( PlanService service ) {
            Set<ContactSpec> specs = new HashSet<ContactSpec>();
            for ( AggregatedFlow outgoing : getOutgoing( service ) )
                if( !outgoing.isAskedFor() )
                     specs.addAll( outgoing.getOthers() );
            for ( AggregatedFlow input : getInputs( service ) )
                if ( input.isAskedFor() )
                    specs.addAll( input.getOthers() );

//            Map<ContactSpec,ContactSpec> map = new HashMap<ContactSpec, ContactSpec>();
//            for ( Commitment commitment : commitmentsOf ) {
//                Assignment beneficiary = commitment.getBeneficiary();
//                ContactSpec e = new ContactSpec(
//                                    commitment.getSharing().getRestriction(),
//                                    new ResourceSpec( beneficiary.getResourceSpec()  ) );
//                ContactSpec old = map.get( e );
//                if ( old == null ) {
//                    map.put( e, e );
//                    specs.add( e );
//                    old = e;
//                }
//                old.addContact( service, beneficiary );
//            }
            return specs;
        }

        public Part getPart() {
            return part;
        }

        public enum Type { IMMEDIATE, PROMPTED, SUBTASK }
    }

    //================================================
    private static final class AggregatedContact
        implements Comparable<AggregatedContact>, Serializable {

        private static final String N_A = "N/A";
        private final Actor actor;
        private AggregatedContact supervisor;
        private final String title;
        private final Organization organization;
        private final Set<Channel> channels = new HashSet<Channel>();
        private final Set<Employment> roles = new HashSet<Employment>();
        private final Participation participation;
        private final String actorName;

        private AggregatedContact() {
            actor = null;
            title = "";
            organization = null;
            participation = null;
            actorName = "";
            supervisor = this;
        }

        private AggregatedContact( PlanService service, Assignment assignment ) {

            this( service, assignment.getEmployment() );
        }

        private AggregatedContact( PlanService service, Employment employment ) {

            actor = employment.getActor();
            title = employment.getTitle();
            organization = employment.getOrganization();
            participation = findParticipation( service, actor, null );
            actorName = participation != null ?
                               participation.getUserFullName( service ) :
                               actor == null ? "" : actor.getName();

            Actor sup = employment.getSupervisor();
            if ( sup == null )
                supervisor = new AggregatedContact();
            else {
                Assignments employments = service.getAssignments().with( sup );
                supervisor = employments.isEmpty() ?
                             new AggregatedContact() :
                             new AggregatedContact( service,
                                                    employments.getAssignments().get( 0 ) );
            }

            merge( service, employment );
        }

        private AggregatedContact( PlanService service, Actor actor, String username ) {
             this( service, actor, findParticipation( service, actor, username ) );
         }

        private AggregatedContact( PlanService service, Actor actor, Participation participation ) {

             this.participation = participation;
             this.actor = actor;

             List<Employment> employments = service.findAllEmploymentsForActor( actor );
             if ( employments.isEmpty() ) {
                 title = "";
                 organization = null;
             } else {
                 Employment firstEmployment = employments.get( 0 );
                 title = firstEmployment.getTitle();
                 organization = firstEmployment.getOrganization();
             }

             actorName = participation != null ?
                                participation.getUserFullName( service ) :
                                actor == null ? "" : actor.getName();

             for ( Employment employment : employments )
                 merge( service, employment );

         }

          private static Participation findParticipation( PlanService service, Actor actor, String username ) {
              List<Participation> list = service.list( Participation.class );
              if ( username != null )
                  for ( Participation participation : list )
                      if ( username.equals( participation.getUsername() ) )
                          return participation;

              for ( Participation participation : list )
                if ( actor.equals( participation.getActor() ) )
                    return participation;

            return null;
        }

        public void merge( PlanService service, Employment employment ) {

            roles.add( employment );

            channels.addAll(
                participation == null ?
                    service.findAllChannelsFor( new ResourceSpec( employment ) ) :
                    participation.getEffectiveChannels()
            );
        }

        private String getClassifications() {
            return actor == null ? N_A : getClassificationString( actor.getClassifications() );
        }

        private String getActorName() {
            return actorName;
        }

        public String getRoles() {
            List<Employment> list = new ArrayList<Employment>( roles );
            Collections.sort( list, new Comparator<Employment>() {
                @Override
                public int compare( Employment o1, Employment o2 ) {
                    int i = o1.getOrganization().compareTo( o2.getOrganization() );
                    return i == 0 ? o1.getRole().compareTo( o2.getRole() ) : i ;
                }
            } );
            List<String> strings = new ArrayList<String>( list.size() );
            for ( Employment employment : list )
                strings.add( employment.getLabel() );

            return listToString( strings, " and " );
        }

        public AggregatedContact getSupervisor() {
            return supervisor;
        }

        public String getTitle() {
            return title;
        }

        public List<Channel> getChannels() {
            List<Channel> result = new ArrayList<Channel>( channels );
            Collections.sort( result );
            return result;
        }

        public Organization getOrganization() {
            return organization;
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
            AggregatedContact other = (AggregatedContact) obj;
            return actor == null ? other.getActor() == null : actor.equals( other.getActor() );
        }

        @Override
        public int hashCode() {
            return actor != null ? actor.hashCode() : 0;
        }

        @Override
        public int compareTo( AggregatedContact o ) {
            int i = organization.compareTo( o.getOrganization() );

            if ( i != 0 )
                return i;

            return actorName == null ?
                   -1 :
                   o.getActorName() == null ? 1 : actorName.compareTo( o.getActorName() );
        }

        public String getAvailability() {
            return actor == null || actor.getAvailability() == null ?
                   N_A :
                   actor.getAvailability().toString();
        }

        public Participation getParticipation() {
            return participation;
        }
    }

    //================================================
    private static class AggregatedFlow implements Serializable {

        private final String label;
        private final Set<ContactSpec> others = new HashSet<ContactSpec>();
        private final Map<String, ElementOfInformation> eoiIndex =
            new HashMap<String, ElementOfInformation>();
        private final boolean incoming;
        private final Flow flow;
        private Assignment origin;
        private Delay maxDelay;

        private AggregatedFlow(
            Flow flow, boolean incoming, Collection<Commitment> commitments, PlanService service ) {

            label = flow.getName();
            this.flow = flow;
            this.incoming = incoming;
            addFlow( flow, commitments, service );
        }

        private String getSourcesString( PlanService service ) {

            Set<ContactSpec> set = new HashSet<ContactSpec>();
            for ( ContactSpec source : others )
                set.addAll( source.actualize( service, origin ) );
            List<ContactSpec> specList = new ArrayList<ContactSpec>( set );
            Collections.sort( specList );

            List<String> list = new ArrayList<String>( specList.size() );
            for ( ContactSpec spec : specList )
                list.add( spec.getSpec().getReportSource( !incoming && isAll() ? "every " : "any " ) );
            if ( flow.getRestriction() != null )
                switch ( flow.getRestriction() ) {
                    case SameTopOrganization:
                    case SameOrganization:
                    case DifferentOrganizations:
                    case DifferentTopOrganizations:
                        return listToString( list, " or " );
                    case SameLocation:
                    case DifferentLocations:
                    case Supervisor:
                    case Self:
                    case Other:
                }
            return listToString( list, " or " ) + flow.getRestrictionString( !incoming );
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

        public void setOrigin( Assignment origin ) {
            this.origin = origin;
        }

        private String getTiming() {

            StringWriter w = new StringWriter();
            if ( incoming ) {
                w.append( isAskedFor() ? "Available upon request" : "" );
                w.append(
                    maxDelay.getSeconds() == 0 ? "" : " after at most " + maxDelay.toString() );
            } else
                w.append( maxDelay.getSeconds() == 0 ? "" : "Within " + maxDelay.toString() );
            return w.toString();
        }

        private void addSpec(
            Flow flow, Node node, Collection<Commitment> commitments, PlanService service ) {
            ContactSpec spec = new ContactSpec( flow.getRestriction(),
                                                new ResourceSpec( (Specable) node ) );
            for ( Commitment commitment : commitments ) {
                if ( flow.equals( commitment.getSharing() ) )
                    spec.addContact( service, incoming ? commitment.getCommitter() : commitment.getBeneficiary() );

            }
            others.add( spec );
        }

        private void addFlow( Flow flow, Collection<Commitment> commitments, PlanService service ) {
            Node node = incoming ? flow.getSource() : flow.getTarget();
            if ( node.isPart() )
                addSpec( flow, node, commitments, service );
            else
                for ( ExternalFlow externalFlow : ( (Connector) node ).getExternalFlows() ) {
                    Node xNode = incoming ? externalFlow.getSource() : externalFlow.getTarget();
                    if ( xNode.isPart() )
                        addSpec( flow, xNode, commitments, service );
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
            return sortedEOIs( new ArrayList<ElementOfInformation>( eoiIndex.values() ) );
        }

        public boolean hasEois() {
            return !eoiIndex.isEmpty();
        }

        public boolean isAskedFor() {
            return flow.isAskedFor();
        }

        public boolean isAll() {
            // An external flow is by definition always to all assigned to targeted task.
            return flow.isExternal() || flow.isAll();
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

        public Flow getFlow() {
            return flow;
        }

        public Set<ContactSpec> getOthers() {
            return Collections.unmodifiableSet( others );
        }
    }
}
