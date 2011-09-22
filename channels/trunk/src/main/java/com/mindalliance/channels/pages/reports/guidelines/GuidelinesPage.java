// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports.guidelines;

import com.mindalliance.channels.core.Attachment;
import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Flow.Intent;
import com.mindalliance.channels.core.model.Flow.Restriction;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.pages.reports.AbstractParticipantPage;
import com.mindalliance.channels.pages.reports.ReportSegment;
import com.mindalliance.channels.pages.reports.ReportTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage.GuidelinesReportTask.Type.IMMEDIATE;
import static com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage.GuidelinesReportTask.Type.PROMPTED;
import static com.mindalliance.channels.pages.reports.guidelines.GuidelinesPage.GuidelinesReportTask.Type.SUBTASK;

/**
 * The participant report.  This page is different for every user.
 */
public class GuidelinesPage extends AbstractParticipantPage {

    private static final Logger LOG = LoggerFactory.getLogger( GuidelinesPage.class );

    @SpringBean
    private AttachmentManager attachmentManager;

    /**
     * Called for access without parameters. Find the actor and plan corresponding to the current
     * user and redirect to that page. Otherwise, redirect to access denied.
     */
    public GuidelinesPage() {
        super( GuidelinesPage.class );
    }

    public GuidelinesPage( PageParameters parameters ) {
        super( AllGuidelines.class, parameters );
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

    @Override
    protected String getReportTitle() {
        return "Channels - Participant IS Guidelines Report";
    }

    @Override
    protected String getReportName() {
        return " Participant IS Guidelines Report";
    }

    @Override
    protected String getReportType() {
        return "Information Sharing Guidelines";
    }

    //-----------------------------------
    protected void initReportBody(
            Plan plan,
            QueryService service,
            ResourceSpec profile,
            String override,
            AggregatedContact contact ) {


        List<GuidelinesReportSegment> reportSegments = getSegments( service, profile );
        add(
                new ListView<GuidelinesReportSegment>( "segmentLinks", reportSegments ) {
                    @Override
                    protected void populateItem( ListItem<GuidelinesReportSegment> item ) {
                        item.getModelObject().addLinkTo( item );
                    }
                },
                newPhases( reportSegments )
        );
    }

    //-----------------------------------
    private ListView<GuidelinesReportSegment> newPhases( List<GuidelinesReportSegment> segments ) {

        return new ListView<GuidelinesReportSegment>( "phases", segments ) {
            @Override
            protected void populateItem( ListItem<GuidelinesReportSegment> item ) {
                QueryService service = getQueryService();
                GuidelinesReportSegment segment = item.getModelObject();
                item.add(
                        new WebMarkupContainer( "phaseAnchor" )
                                .add( new Label( "phaseText", segment.getName() ) )
                                .add( new AttributeModifier( "name", true, segment.getAnchor() ) ),
                        new UserFeedbackPanel( "segmentFeedback", segment.getSegment(), "Send feedback", "Guidelines" ),

                        new Label( "context", segment.getContext() ),
                        new Label( "segDesc", ChannelsUtils.ensurePeriod( segment.getDescription() ) )
                                .setVisible( !segment.getDescription().isEmpty() ),
                        new Label( "phaseSeq", Integer.toString( segment.getSeq() ) ),
                        new WebMarkupContainer( "routineDiv" )
                                .add( newTaskLinks( segment.getImmediates() ) )
                                .setVisible( !segment.getImmediates().isEmpty() ),
                        newInputDiv( segment.getPrompted() ),
                        newDocSection( segment.getAttachmentsFrom( attachmentManager ) ),

                        newTasks( segment, segment.getTasks() ),
                        new WebMarkupContainer( "contactList" )
                                .add( new Label( "contactSeq", segment.getContactSeq() ),
                                        new Label( "contactTitle", segment.getName() ),
                                        new UserFeedbackPanel( "contactListFeedback",
                                                segment.getSegment(),
                                                "Send feedback",
                                                "Guidelines contact list" ),
                                        newContacts( segment.getContactSpecs( service ) ) )
                                .setVisible( !segment.getContacts().isEmpty() ) );
            }
        };
    }

    //-----------------------------------
    private ListView<GuidelinesReportTask> newTasks( final GuidelinesReportSegment segment, List<GuidelinesReportTask> tasks ) {

        return new ListView<GuidelinesReportTask>( "tasks", tasks ) {
            @Override
            protected void populateItem( ListItem<GuidelinesReportTask> item ) {

                GuidelinesReportTask task = item.getModelObject();
                QueryService planService = getQueryService();

                item.add(
                        new WebMarkupContainer( "taskAnchor" )
                                .add( new Label( "taskName", task.getTitle() ) )
                                .add( new AttributeModifier( "name", true, task.getAnchor() ) ),
                        new UserFeedbackPanel( "taskFeedback", task.getPart(), "Send feedback", "Guidelines" ),
                        new WebMarkupContainer( "backTask" )
                                .add( new AttributeModifier( "href", true, segment.getLink() ) ),
                        new Label( "taskSeq", task.getSeqString() ),
                        new Label( "taskSummary", "The context is " + ChannelsUtils.lcFirst( task.getTaskSummary() ) ),
                        new Label( "taskRole", ChannelsUtils.ensurePeriod( task.getRoleString() ) ),
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
                        newIncomingFlows( task.getInputs() ),
                        newOutgoingFlows( "distribDiv", task.getOutgoing() ),
                        newOutgoingFlows( "taskRfiDiv", task.getRequests() ),
                        newOutgoingFlows( "failDiv", task.getFailures() ),
                        newDocSection(
                                task.getAttachmentsFrom( attachmentManager ) ) );
            }
        };
    }

    private static Component newDetailsDiv( GuidelinesReportTask task ) {

        // TODO hide details when empty
        List<Goal> risks = task.getRisks();
        List<Goal> gains = task.getGains();
        return new WebMarkupContainer( "details" )
                .add( new Label( "instruct",
                        task.getDescription() ).setVisible( !task.getDescription().isEmpty() ),
                        new WebMarkupContainer( "routineTask" ).add( new Label( "taskRecur",
                                task.isRepeating() ?
                                        "It is repeated every "
                                                + task.getRepetition()
                                                + '.' :
                                        "" ).setVisible( task.isRepeating() ) ).setVisible(
                                task.isImmediate() ),
                        new WebMarkupContainer( "taskDuration" ).add( new Label( "dur",
                                task.getCompletionString() ) ).setVisible(
                                task.getCompletionTime() ),
                        new WebMarkupContainer( "term1" )/*.add( new Label( "eventPhase",
                                                                    task.getEventPhase() ) )*/.setVisible(
                                task.isStartWithSegment() ),
                        // TODO super-event override notice
                        new WebMarkupContainer( "superNote" ).setVisible( false ),
                        new WebMarkupContainer( "riskDiv" ).add( new ListView<Goal>( "risks",
                                        risks ) {
                                    @Override
                                    protected void populateItem(
                                            ListItem<Goal> item ) {
                                        item.add( new Label( "type",
                                                item.getModelObject().getPartialTitle() ) );
                                    }
                                },
                                new ListView<Goal>( "gains",
                                        gains ) {
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
                        new WebMarkupContainer( "subTask" ).add( newSimpleEoiList( task.getStartingEois() ) ).setVisible( task.isSubtask() ) )

                .setVisible( !risks.isEmpty() || !gains.isEmpty() || task.isStartWithSegment()
                        || task.isImmediate() || task.isSubtask() || task.getCompletionTime() );
    }

    private Component newPromptedByDiv( GuidelinesReportTask task, QueryService planService ) {

        // TODO handle tasks prompted by multiple different requests or notification mixes

        boolean notification = task.isNotification( planService );
        return new WebMarkupContainer( "promptedBy" )
                .add(
                        new ListView<AggregatedFlow>( "notifTask", task.getTriggeringFlows() ) {
                            @Override
                            protected void populateItem( ListItem<AggregatedFlow> item ) {
                                AggregatedFlow flow = item.getModelObject();
                                List<ElementOfInformation> eois = flow.getElementsOfInformation();
                                String sourcesString = flow.getSourcesString();
                                item.add(
                                        new WebMarkupContainer( "notifTask1" )
                                                .add( new Label( "reqFlow", flow.getLabel() ),
                                                        new Label( "reqFlowSrc", sourcesString ) )
                                                .setVisible( eois.isEmpty() ),

                                        new WebMarkupContainer( "notifTask2" )
                                                .add( new Label( "reqFlow", flow.getLabel() ),
                                                        new Label( "reqFlowSrc", sourcesString ),
                                                        newSimpleEoiList( eois ) )
                                                .setVisible( !eois.isEmpty() ) );
                            }
                        }.setVisible( notification ),

                        new WebMarkupContainer( "reqTask" )
                                .add( new Label( "taskType", task.getCategoryString() ).setRenderBodyOnly(
                                        true ),
                                        new Label( "reqFlow", task.getTriggeringFlowName() ),
                                        new Label( "reqFlowSrc", task.getTriggeringSources( planService ) ) )
                                .setVisible( task.isRequest() ) )

                .setVisible( notification || task.isRequest() );
    }


    private static Component newOutgoingFlows( String id, List<AggregatedFlow> flows ) {
        return new WebMarkupContainer( id )
                .add( new FlowTable( "flowTable", flows ) )
                .setVisible( !flows.isEmpty() );
    }

    //-----------------------------------
    private static Component newSimpleEoiList( List<ElementOfInformation> eois ) {
        return new WebMarkupContainer( "eois" )
                .add( newEoiList( eois ) )
                .setVisible( !eois.isEmpty() );
    }

    public static ListView<ElementOfInformation> newEoiList( List<ElementOfInformation> eois ) {

        return new ListView<ElementOfInformation>( "eoi", eois ) {
            @Override
            protected void populateItem( ListItem<ElementOfInformation> item ) {

                ElementOfInformation eoi = item.getModelObject();
                item.add(
                        new Label( "eoi.name", eoi.getContent() ),
                        new Label( "eoi.desc", notAvailable( ChannelsUtils.ensurePeriod( eoi.getDescription() ) ) ),
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
    private Component newIncomingFlows( final List<AggregatedFlow> flows ) {

        return new WebMarkupContainer( "criticalDiv" )
                .add( new ListView<AggregatedFlow>( "perFlow", flows ) {
                    @Override
                    protected void populateItem( ListItem<AggregatedFlow> item ) {
                        AggregatedFlow flow = item.getModelObject();
                        item.add( new Label( "flowName2", flow.getFormattedLabel() ),
                                new Label( "flowSources",
                                        flow.getSourcesString() ),
                                new Label( "flowTiming", ChannelsUtils.lcFirst( flow.getTiming() ) ),
                                new WebMarkupContainer( "critical" ).setVisible( flow.isCritical() ),
                                new UserFeedbackPanel( "incomingFeedback",
                                        flow.getBasis(),
                                        "Send feedback",
                                        "Guidelines"),
                                new WebMarkupContainer( "eoisRow" ).add( newEoiList( flow.getElementsOfInformation() ) ).setRenderBodyOnly(
                                        true ).setVisible( flow.hasEois() ),
                                new WebMarkupContainer( "flowEnding" ).setVisible( flow.isTerminatingToTarget() ) );
                    }
                }.setRenderBodyOnly( true ) )
                .setVisible( !flows.isEmpty() );
    }

    private Component newContacts( List<ContactSpec> contactSpecs ) {

        return new ListView<ContactSpec>( "group", contactSpecs ) {
            @Override
            protected void populateItem( ListItem<ContactSpec> specItem ) {

                ContactSpec spec = specItem.getModelObject();
                final List<AggregatedContact> contactList = spec.getContactList();
                if ( contactList.isEmpty() )
                    contactList.add( new AggregatedContact() );

                QueryService service = getQueryService();
                for ( AggregatedContact contact : contactList )
                    contact.resolveChannels( service );

                specItem.add(
                        new WebMarkupContainer( "specAnchor" )
                                .add( new Label( "spec",
                                        spec.getLabel() ).setRenderBodyOnly( true ) )
                                .add( new AttributeModifier( "name",
                                        true,
                                        new Model<String>( spec.getLink() ) ) ),

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


    private static List<GuidelinesReportSegment> getSegments( QueryService planService, Specable myProfile ) {

        List<GuidelinesReportSegment> result = new ArrayList<GuidelinesReportSegment>();

        Assignments allAssignments = planService.getAssignments( false, false );
        Commitments commitments = new Commitments( planService, myProfile, allAssignments );

        Assignments myAssignments = allAssignments.with( myProfile );
        Assignments assignedByOthers = myAssignments.notFrom( myProfile );

        for ( Segment segment : assignedByOthers.getSegments() )
            result.add( new GuidelinesReportSegment( planService,
                    result.size() + 1,
                    segment,
                    assignedByOthers.with( segment ),
                    allAssignments,
                    commitments ) );
        return result;
    }

    //-----------------------------------
    private Component newInputDiv( List<GuidelinesReportTask> inputs ) {

        return new WebMarkupContainer( "inputDiv" )
                .add( new ListView<GuidelinesReportTask>( "inputLinks", inputs ) {
                    @Override
                    protected void populateItem( ListItem<GuidelinesReportTask> item ) {
                        GuidelinesReportTask task = item.getModelObject();
                        item.add( new ListView<AggregatedFlow>( "flow",
                                task.getTriggeringFlows() ) {
                            @Override
                            protected void populateItem( ListItem<AggregatedFlow> flowItem ) {
                                AggregatedFlow flow = flowItem.getModelObject();
                                QueryService planService = getQueryService();
                                flowItem.add( new WebMarkupContainer( "nFlow" ).add( new Label(
                                        "flowName",
                                        flow.getLabel() ),
                                        new Label(
                                                "flowSources",
                                                flow.getSourcesString() ) ).setVisible(
                                        !flow.isAskedFor() ),
                                        new WebMarkupContainer( "rFlow" ).add( new Label(
                                                "flowName",
                                                flow.getLabel() ),
                                                new Label(
                                                        "flowSources",
                                                        flow.getSourcesString() ) ).setVisible(
                                                flow.isAskedFor() ) );
                            }
                        }.setRenderBodyOnly( true ),
                                new Label( "linkName", task.getTitle() ),
                                newTaskLink( task ) );
                    }
                } )
                .setVisible( !inputs.isEmpty() );
    }

    //-----------------------------------
    private static Component newTaskLink( GuidelinesReportTask task ) {

        return new WebMarkupContainer( "link" )
                .add( new Label( "linkValue", task.getLabel() ) )
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
    private static ListView<GuidelinesReportTask> newTaskLinks( final List<GuidelinesReportTask> routines ) {

        return new ListView<GuidelinesReportTask>( "links", routines ) {
            @Override
            protected void populateItem( ListItem<GuidelinesReportTask> item ) {

                GuidelinesReportTask task = item.getModelObject();
                item.add( new Label( "linkName", task.getTask() ), newTaskLink( task ) );
            }
        };
    }


    //================================================
    public static class ContactSpec implements Serializable, Comparable<ContactSpec> {

        private static final int TOO_MANY = 2;
        private final ResourceSpec spec;
        private final Restriction restriction;
        private final Set<AggregatedContact> contacts = new HashSet<AggregatedContact>();
        private final Map<Employment, AggregatedContact> contactIndex =
                new HashMap<Employment, AggregatedContact>();
        private String link;

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
                case SameOrganizationAndLocation:
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

        private List<ContactSpec> actualize( QueryService service, Specable origin ) {

            Organization org = spec.getOrganization();
            if ( org != null && !org.isUnknown() && !org.isActual() ) {
                List<ContactSpec> result = new ArrayList<ContactSpec>();
                List<? extends ModelEntity> narrowed = service.findAllNarrowingOrEqualTo( org );
                for ( Iterator<? extends ModelEntity> iterator =
                              narrowed.iterator(); iterator.hasNext() && result.size() < TOO_MANY; ) {
                    ModelEntity entity = iterator.next();
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
                }
                if ( result.size() < TOO_MANY )
                    return result;
            }

            List<ContactSpec> result = new ArrayList<ContactSpec>();
            result.add( this );
            return result;
        }

        private void addContacts( Set<AggregatedContact> list ) {
            contacts.addAll( list );
        }

        private String getLabel() {
            return restriction == null ?
                    getLabel( "" ) :
                    getLabel( "" ) + ' ' + restriction.getLabel( true );
        }

        private String getLabel( String prefix ) {
            return spec.getReportSource( prefix );
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
                    restriction != null && o.getRestriction() != null
                            ? restriction.compareTo( o.getRestriction() )
                            : 0
                    : i;
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

        private void addContact(
                QueryService service, Assignment beneficiary, Assignments assignments ) {
            Employment employment = beneficiary.getEmployment();
            if ( !contactIndex.containsKey( employment ) ) {
                AggregatedContact e = new AggregatedContact( service, beneficiary, assignments );
                contacts.add( e );
                contactIndex.put( employment, e );
            }
        }

        public void setLink( String link ) {
            this.link = link;
        }

        public String getLink() {
            return link;
        }
    }

    //================================================
    private static class GuidelinesReportSegment extends ReportSegment {

        private final List<GuidelinesReportTask> tasks = new ArrayList<GuidelinesReportTask>();
        private final List<GuidelinesReportTask> immediates = new ArrayList<GuidelinesReportTask>();
        private final List<GuidelinesReportTask> prompted = new ArrayList<GuidelinesReportTask>();
        private final List<GuidelinesReportTask> subtasks = new ArrayList<GuidelinesReportTask>();
        private final List<AggregatedContact> contacts;

        private GuidelinesReportSegment(
                QueryService planService, int seq, Segment segment, Assignments segmentAssignments,
                Assignments allAssignments, Commitments commitments ) {

            super( seq, segment );
            for ( Assignment assignment : segmentAssignments ) {
                GuidelinesReportTask reportTask = new GuidelinesReportTask( seq, assignment );
                reportTask.resolve( segmentAssignments, planService, allAssignments, commitments );

                tasks.add( reportTask );
                if ( Assignments.isImmediate( assignment.getPart(), planService ) ) {
                    immediates.add( reportTask );
                    reportTask.setType( IMMEDIATE );
                } else {
                    prompted.add( reportTask );
                    reportTask.setType( PROMPTED );
                }

                for ( GuidelinesReportTask task : reportTask.getAllSubtasks() ) {
                    subtasks.add( task );
                    tasks.add( task );
                    task.setType( SUBTASK );
                }
            }

            // Renumber
            int i = 1;
            for ( GuidelinesReportTask task : prompted )
                task.setTaskSeq( i++ );
            for ( GuidelinesReportTask immediate : immediates )
                immediate.setTaskSeq( i++ );
            for ( GuidelinesReportTask sub : subtasks )
                sub.setTaskSeq( i++ );
            Collections.sort( tasks );

            contacts = findContacts( planService );
        }

        private List<ContactSpec> getContactSpecs( QueryService service ) {

            Set<ContactSpec> specs = new HashSet<ContactSpec>();
            for ( GuidelinesReportTask task : tasks )
                for ( ContactSpec taskSpec : task.getContactSpecs() )
                    specs.addAll( taskSpec.actualize( service, task.getAssignment() ) );

            List<ContactSpec> result = new ArrayList<ContactSpec>( specs );
            Collections.sort( result );
            for ( int i = 0, resultSize = result.size(); i < resultSize; i++ ) {
                ContactSpec contactSpec = result.get( i );
                contactSpec.setLink( "cs" + getSeq() + "_" + i );
            }
            return result;
        }

        private List<AggregatedContact> findContacts( QueryService service ) {

            List<ContactSpec> contactSpecs = getContactSpecs( service );
            List<AggregatedContact> result = new ArrayList<AggregatedContact>( contactSpecs.size() );
            for ( ContactSpec contactSpec : contactSpecs )
                result.addAll( contactSpec.getContacts() );

            Collections.sort( result );
            return result;
        }


        public List<GuidelinesReportTask> getImmediates() {
            return Collections.unmodifiableList( immediates );
        }

        public List<GuidelinesReportTask> getPrompted() {
            return Collections.unmodifiableList( prompted );
        }

        public List<GuidelinesReportTask> getTasks() {
            return Collections.unmodifiableList( tasks );
        }


        public List<AggregatedContact> getContacts() {
            return Collections.unmodifiableList( contacts );
        }

        private List<Attachment> getAttachmentsFrom( AttachmentManager attachmentManager ) {
            List<Attachment> attachments = new ArrayList<Attachment>();
            attachments.addAll( attachmentManager.getMediaReferences( getSegment() ) );
            attachments.addAll( attachmentManager.getMediaReferences( getSegment().getEvent() ) );
            attachments.addAll( attachmentManager.getMediaReferences( getSegment().getPhase() ) );
            return attachments;
        }

        private String getContactSeq() {
            return getSeq() + "." + ( tasks.size() + 1 );
        }

    }

    //================================================

    /**
     * Some report-specific extra information for an assignment.
     */
    public static class GuidelinesReportTask extends ReportTask {

        private Type type;
        private final Assignment assignment;
        private final List<GuidelinesReportTask> subtasks = new ArrayList<GuidelinesReportTask>();
        private Commitments commitmentsOf;
        private Commitments commitmentsTo;

        private List<AggregatedFlow> triggeringFlows;
        private List<AggregatedFlow> inputs;
        private List<AggregatedFlow> outgoing;
        private List<AggregatedFlow> requests;
        private List<AggregatedFlow> failures;

        private GuidelinesReportTask( int segmentSeq, Assignment assignment ) {
            super( segmentSeq, assignment.getPart());
            this.assignment = assignment;
        }

        private void resolve(
                Assignments segmentAssignments, QueryService service,
                Assignments allAssignments, Commitments commitments ) {

            // TODO take care of possibility of loops
            for ( Assignment sub : segmentAssignments.from( assignment ) ) {
                GuidelinesReportTask subtask = new GuidelinesReportTask( getSegmentSeq(), sub );
                subtask.resolve( segmentAssignments, service, allAssignments, commitments );
                subtasks.add( subtask );
            }

            commitmentsOf = commitments.of( assignment );
            commitmentsTo = commitments.to( assignment );

            triggeringFlows = findTriggeringFlows();
            inputs = findInputs();
            outgoing = findOutgoing();
            requests = findRequests();
            failures = findFailures();
            Set<AggregatedFlow> allFlows = new HashSet<AggregatedFlow>( triggeringFlows );
            allFlows.addAll( inputs );
            allFlows.addAll( outgoing );
            allFlows.addAll( requests );
            allFlows.addAll( failures );
            for ( AggregatedFlow flow : allFlows )
                flow.resolveContacts( service, allAssignments, commitments );
        }

        public List<AggregatedFlow> getFailures() {
            return Collections.unmodifiableList( failures );
        }

        public List<AggregatedFlow> getInputs() {
            return Collections.unmodifiableList( inputs );
        }

        public List<AggregatedFlow> getOutgoing() {
            return Collections.unmodifiableList( outgoing );
        }

        public List<AggregatedFlow> getRequests() {
            return Collections.unmodifiableList( requests );
        }

        public List<AggregatedFlow> getTriggeringFlows() {
            return Collections.unmodifiableList( triggeringFlows );
        }

        public Assignment getAssignment() {
            return assignment;
        }

        private List<AggregatedFlow> aggregate( Collection<Flow> flows, boolean incoming ) {

            List<AggregatedFlow> result = new ArrayList<AggregatedFlow>( flows.size() );
            Map<String, AggregatedFlow> flowMap = new HashMap<String, AggregatedFlow>();

            for ( Flow flow : flows ) {
                AggregatedFlow old = flowMap.get( flow.getName() );
                if ( old == null ) {
                    AggregatedFlow newFlow = new AggregatedFlow( flow, incoming );
                    newFlow.setOrigin( assignment );
                    flowMap.put( flow.getName(), newFlow );
                    result.add( newFlow );
                } else
                    old.addFlow( flow );
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

        public boolean isStartWithSegment() {
            return getPart().isAutoStarted();
        }

        public boolean getCompletionTime() {
            Delay completionTime =getPart().getCompletionTime();
            return completionTime != null && completionTime.getSeconds() != 0;
        }

        public String getCompletionString() {
            return getPart().getCompletionTime() == null ?
                    "" :
                    String.valueOf( getPart().getCompletionTime() );
        }

        private boolean isNotification( QueryService planService ) {
            return Assignments.isNotification( getPart(), planService );
        }

        private boolean isRequest() {
            return Assignments.isRequest( getPart() );
        }

        private String getTriggeringSources( QueryService service ) {

            Set<String> sourcesStrings = new HashSet<String>();
            for ( AggregatedFlow flow : triggeringFlows )
                sourcesStrings.add( flow.getSourcesString() );
            List<String> sources = new ArrayList<String>( sourcesStrings );
            Collections.sort( sources );
            return ChannelsUtils.listToString( sources, " or " );
        }

        private String getTriggeringFlowName() {
            return triggeringFlows.isEmpty() ? "" : triggeringFlows.get( 0 ).getLabel();
        }

        private List<AggregatedFlow> findTriggeringFlows() {

            Set<Flow> result = new HashSet<Flow>();
            for ( Commitment commitment : commitmentsTo ) {
                Flow flow = commitment.getSharing();
                if ( flow.isTriggeringToTarget() ) {
                    Node source = flow.getSource();
                    if ( !source.isConnector()
                            || !( (Connector) source ).getExternalFlows().isEmpty() )
                        result.add( flow );
                }
            }
            return aggregate( result, true );
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

            Collection<Goal> goals = getPart().getGoals();
            List<Goal> answer = new ArrayList<Goal>( goals.size() );
            for ( Goal item : goals )
                if ( item.isPositive() )
                    answer.add( item );
            return answer;
        }

        private List<Goal> getRisks() {

            Collection<Goal> goals = getPart().getGoals();
            List<Goal> answer = new ArrayList<Goal>( goals.size() );
            for ( Goal item : goals )
                if ( !item.isPositive() )
                    answer.add( item );
            return answer;
        }

        //-----------------------------------
        private List<AggregatedFlow> findFailures() {

            Set<Flow> result = new HashSet<Flow>();
            for ( Commitment commitment : commitmentsOf ) {
                Flow flow = commitment.getSharing();
                if ( !flow.isAskedFor() && flow.isIfTaskFails() )
                    result.add( flow );
            }

            return aggregate( result, false );
        }

        //-----------------------------------
        private List<AggregatedFlow> findRequests() {

            Set<Flow> result = new HashSet<Flow>();

            for ( Commitment commitment : commitmentsOf ) {
                Flow flow = commitment.getSharing();
                if ( flow.isAskedFor() )
                    result.add( flow );
            }

            return aggregate( result, true );
        }

        //-----------------------------------
        private List<AggregatedFlow> findOutgoing() {
            Set<Flow> result = new HashSet<Flow>();
            for ( Commitment commitment : commitmentsOf ) {
                Flow flow = commitment.getSharing();
                if ( !flow.isAskedFor() && !flow.isIfTaskFails() )
                    result.add( flow );
            }
            return aggregate( result, false );
        }

        //-----------------------------------
        private List<AggregatedFlow> findInputs() {
            Set<Flow> set = new HashSet<Flow>();
            for ( Commitment commitment : commitmentsTo ) {
                Flow flow = commitment.getSharing();
                if ( !flow.isTriggeringToTarget() )
                    set.add( flow );
            }

            return aggregate( set, true );
        }

        //-----------------------------------
        private List<ElementOfInformation> getStartingEois() {

            Set<ElementOfInformation> elements = new HashSet<ElementOfInformation>();
            Map<String, ElementOfInformation> seen = new HashMap<String, ElementOfInformation>();
            for ( AggregatedFlow flow : triggeringFlows )
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


        public List<GuidelinesReportTask> getSubtasks() {
            return Collections.unmodifiableList( subtasks );
        }

        public List<GuidelinesReportTask> getAllSubtasks() {

            List<GuidelinesReportTask> result = new ArrayList<GuidelinesReportTask>();
            addAllSubtasks( result );
            return result;
        }

        private void addAllSubtasks( List<GuidelinesReportTask> tasks ) {

            for ( GuidelinesReportTask subtask : subtasks )
                if ( !tasks.contains( subtask ) ) {
                    tasks.add( subtask );
                    subtask.addAllSubtasks( tasks );
                }
        }

        public Set<ContactSpec> getContactSpecs() {
             Set<ContactSpec> specs = new HashSet<ContactSpec>();
             for ( AggregatedFlow flow : outgoing )
                 if ( !flow.isAskedFor() )
                     specs.addAll( flow.getSpecs() );
             for ( AggregatedFlow input : inputs )
                 if ( input.isAskedFor() )
                     specs.addAll( input.getSpecs() );

             return specs;
         }

        public enum Type {IMMEDIATE, PROMPTED, SUBTASK}
    }

    //================================================
    public static class AggregatedFlow implements Serializable {

        private final String label;
        private final Set<ContactSpec> specs = new HashSet<ContactSpec>();
        private final Map<String, ElementOfInformation> eoiIndex =
                new HashMap<String, ElementOfInformation>();
        private final boolean incoming;
        private final Flow basis;
        private final Set<Flow> flows = new HashSet<Flow>();
        private Assignment origin;
        private Delay maxDelay;
        private final Set<ContactSpec> reducedSpecs = new HashSet<ContactSpec>();

        private AggregatedFlow( Flow basis, boolean incoming ) {

            label = basis.getName();
            this.basis = basis;
            this.incoming = incoming;
            addFlow( basis );
        }

        public String getSourcesString() {

            List<ContactSpec> specList = new ArrayList<ContactSpec>( reducedSpecs );
            Collections.sort( specList );

            List<String> list = new ArrayList<String>( specList.size() );
            boolean every = !incoming && isAll();
            for ( ContactSpec spec : specList ) {
                list.add( spec.getLabel( every ? "every " : "any " ) );
            }
            return ChannelsUtils.listToString( list, every ? " and " : " or " );
        }

        public String getFormattedLabel() {

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

        public String getTiming() {

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
                Flow flow, Node node, Commitments commitments, QueryService service,
                Assignments assignments ) {

            ContactSpec spec = new ContactSpec( flow.getRestriction(),
                    new ResourceSpec( (Specable) node ) );

            if ( !specs.contains( spec ) ) {
                for ( Commitment commitment : commitments )
                    spec.addContact( service,
                            incoming ? commitment.getCommitter()
                                    : commitment.getBeneficiary(), assignments );
                specs.add( spec );
            }
        }

        private void addFlow( Flow flow ) {

            flows.add( flow );

            List<ElementOfInformation> eois = flow.getEois();
            for ( ElementOfInformation eoi : eois ) {
                String key = eoi.getContent();
                if ( !eoiIndex.containsKey( key ) )
                    eoiIndex.put( key, eoi );
            }

            // TODO compute minimum max delay
            maxDelay = flow.getMaxDelay();
        }

        private void resolveContacts(
                QueryService service, Assignments assignments, Commitments commitments ) {
            for ( Flow flow : flows ) {
                Node node = incoming ? flow.getSource() : flow.getTarget();
                Commitments flowCommitments = commitments.with( flow );
                if ( node.isPart() )
                    addSpec( flow, node, flowCommitments, service, assignments );
                else
                    for ( ExternalFlow externalFlow : ( (Connector) node ).getExternalFlows() ) {
                        Node xNode = incoming ? externalFlow.getSource() : externalFlow.getTarget();
                        if ( xNode.isPart() )
                            addSpec( flow, xNode, flowCommitments, service, assignments );
                    }
            }

            for ( ContactSpec source : specs )
                reducedSpecs.addAll( source.actualize( service, origin ) );
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
            return basis.isAskedFor();
        }

        public boolean isAll() {
            // An external flow is by definition always to all assigned to targeted task.
            return basis.isExternal() || basis.isAll();
        }

        public boolean isCritical() {
            return basis.isCritical();
        }

        public boolean isTerminatingToTarget() {
            return basis.isTerminatingToTarget();
        }

        public boolean isTriggeringToTarget() {
            return basis.isTriggeringToTarget();
        }

        public boolean isTerminatingToSource() {
            return basis.isTerminatingToSource();
        }

        public Intent getIntent() {
            return basis.getIntent();
        }

        public Flow getBasis() {
            return basis;
        }

        public Set<ContactSpec> getSpecs() {
            return Collections.unmodifiableSet( specs );
        }

        public List<ContactSpec> getSpecList() {
            List<ContactSpec> result = new ArrayList<ContactSpec>( specs );
            Collections.sort( result );
            return result;
        }

        public boolean isCantReferenceEventPhase() {
            return !incoming && !basis.isReferencesEventPhase();
        }

        public boolean isReceiptConfirmationRequested() {
            return basis.isReceiptConfirmationRequested();
        }

        public boolean isCanBypassIntermediate() {
            return !incoming && basis.isCanBypassIntermediate() && !basis.intermediatedTargets().isEmpty();
        }

        public String getOtherInstructions() {
            return !incoming ? basis.getDescription() : "";
        }

        public String getReferenceContextInstructions() {
            return "Do not mention the current situation.";
        }

        public String getConfirmReceiptInstructions() {
            return incoming ? "Confirm receipt of the information." : "Request receipt confirmation.";
        }

        // todo: add bypassed-to contacts to list of contacts
        @SuppressWarnings( "unchecked" )
        public String getBypassInstructions() {
            StringBuilder sb = new StringBuilder( ); // "Bypass the intermediate if unreachable.";
            sb.append( "If unreachable, contact " );
            List<ContactSpec> directContacts = new ArrayList<ContactSpec>(  );
            sb.append(
                ChannelsUtils.listToString(
                    (List<String>) CollectionUtils.collect(
                        basis.intermediatedTargets(), new Transformer() {
                        @Override
                        public Object transform( Object input ) {
                            Specable spec = ( (Part) input ).resourceSpec();
                            // todo deal with restriction (merging)
                            ContactSpec contactSpec = new ContactSpec( null, ( (Part) input ).resourceSpec() );
                            // todo set any vs every
                            return contactSpec.getLabel( "a " );
                        }
                    } ), " and" ) );
            return sb.toString();
        }
    }
}
