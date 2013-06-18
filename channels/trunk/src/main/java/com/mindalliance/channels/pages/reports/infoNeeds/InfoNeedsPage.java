/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.reports.infoNeeds;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.PagePathItem;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.pages.reports.AbstractParticipantPage;
import com.mindalliance.channels.pages.reports.ReportSegment;
import com.mindalliance.channels.pages.reports.ReportTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Information needs report.
 */
public class InfoNeedsPage extends AbstractParticipantPage {

    private static final Logger LOG = LoggerFactory.getLogger( InfoNeedsPage.class );

    /**
     * Called for access without parameters. Find the actor and plan corresponding to the current user and redirect to
     * that page. Otherwise, redirect to access denied.
     */
    public InfoNeedsPage() {
        super( new PageParameters() );
    }

    public InfoNeedsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getContentsCssClass() {
        return "guidelines-infoNeeds-contents";
    }

    @Override
    public String getPageName() {
        return "Info Needs";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.INFO_NEEDS;
    }

    private static Commitments realizable(
            Commitments allCommitments,
            Analyst analyst,
            Plan plan,
            QueryService queryService ) {
        Commitments result = new Commitments();
        for ( Commitment commitment : allCommitments )
            if ( analyst.canBeRealized( commitment, plan, queryService ) )
                result.add( commitment );

        return result;
    }

    @Override
    protected String getReportTitle() {
        return "Channels - Participant Information Needs Report";
    }

    @Override
    protected String getReportName() {
        return " Participant Information Needs Report";
    }

    @Override
    protected String getReportType() {
        return "Information Needs";
    }

    @Override
    protected String getFeedbackTopic() {
        return Feedback.INFO_NEEDS;
    }

    public List<PagePathItem> getIntermediatePagesPathItems() {
        List<PagePathItem> intermediates = new ArrayList<PagePathItem>();

        intermediates.add( new PagePathItem(
                AllInfoNeedsPage.class,
                getParameters(),
                "All info needs" ) );
        return intermediates;
    }


    @Override
    protected void initReportBody( Plan plan, QueryService queryService, ResourceSpec profile, String override,
                                   AggregatedContact contact ) {
        List<InfoNeedsReportSegment> reportSegments = getReportSegments( queryService, profile );
        getContainer().add( new ListView<ReportSegment>( "segmentLinks", reportSegments ) {
            @Override
            protected void populateItem( ListItem<ReportSegment> item ) {
                item.getModelObject().addLinkTo( item );
            }
        } );
        addSegments( reportSegments );
    }

    private void addSegments( List<InfoNeedsReportSegment> segments ) {
        getContainer().add( new ListView<InfoNeedsReportSegment>( "segments", segments ) {
            @Override
            protected void populateItem( ListItem<InfoNeedsReportSegment> item ) {
                InfoNeedsReportSegment reportSegment = item.getModelObject();
                item.add( new WebMarkupContainer( "segmentAnchor" ).add( new Label( "segmentText",
                        reportSegment.getName() ) ).add( new AttributeModifier(
                        "name",
                        reportSegment.getAnchor() ) ),
                        new UserFeedbackPanel( "segmentFeedback",
                                reportSegment.getSegment(),
                                "Send feedback",
                                Feedback.INFO_NEEDS ),

                        new Label( "context", reportSegment.getContext() ),
                        new Label( "segDesc",
                                ChannelsUtils.ensurePeriod( reportSegment.getDescription() ) ).setVisible( !reportSegment.getDescription().isEmpty() ),
                        new Label( "segmentSeq", Integer.toString( reportSegment.getSeq() ) ),
                        newTasks( reportSegment ) );
            }
        } );
    }

    private ListView<InfoNeedsReportTask> newTasks( final InfoNeedsReportSegment reportSegment ) {
        return new ListView<InfoNeedsReportTask>( "tasks", reportSegment.getReportTasks() ) {
            @Override
            protected void populateItem( ListItem<InfoNeedsReportTask> item ) {
                InfoNeedsReportTask task = item.getModelObject();
                item.add( new WebMarkupContainer( "taskAnchor" ).add( new Label( "taskName", task.getTitle() ) ).add(
                        new AttributeModifier( "name", task.getAnchor() ) ),
                        new UserFeedbackPanel( "taskFeedback", task.getPart(), "Send feedback", Feedback.INFO_NEEDS ),
                        new WebMarkupContainer( "backTask" ).add( new AttributeModifier( "href",
                                reportSegment.getLink() ) ),
                        new Label( "taskSeq", task.getSeqString() ),
                        new Label( "taskSummary",
                                "The context is " + ChannelsUtils.lcFirst( task.getTaskSummary() ) ),
                        new Label( "taskRole", ChannelsUtils.ensurePeriod( task.getRoleString() ) ),
                        new Label( "taskLoc", task.getLocationString() ).setVisible( task.getLocation() != null ),
                        new Label( "taskRecur", task.getRepetition() ).setVisible( task.isRepeating() ),
                        new WebMarkupContainer( "prohibited" ).setVisible( task.isProhibited() ),
                        newInfoNeeds( task ) );
            }
        };
    }

    private Component newInfoNeeds( final InfoNeedsReportTask task ) {
        return new ListView<TaskInfoNeeds>( "taskInfoNeeds", task.getTaskInfoNeeds() ) {
            @Override
            protected void populateItem( ListItem<TaskInfoNeeds> item ) {
                TaskInfoNeeds taskInfoNeeds = item.getModelObject();
                item.add( new UserFeedbackPanel(
                        "infoNeededFeedback",
                        task.getPart(),
                        "Send feedback",
                        Feedback.INFO_NEEDS ),
                        new Label( "intent", taskInfoNeeds.getIntentString() ),
                        new Label( "info", taskInfoNeeds.getInfo() ),
                        new Label( "impact", taskInfoNeeds.getImpactString() ),
                        newEoisNeeded( taskInfoNeeds ),
                        new Label( "failureSeverity",
                                taskInfoNeeds.failureImpact( getQueryService() ).getNegativeLabel().toLowerCase() ) );
            }
        };
    }

    private Component newEoisNeeded( TaskInfoNeeds taskInfoNeeds ) {
        return new ListView<EoiNeed>( "eois", taskInfoNeeds.getEoiNeeds() ) {
            @Override
            protected void populateItem( ListItem<EoiNeed> item ) {
                EoiNeed eoiNeed = item.getModelObject();
                WebMarkupContainer statusContainer = new WebMarkupContainer( "status" );
                statusContainer.add(
                        new AttributeModifier( "src",
                                new PropertyModel<String>( eoiNeed, "icon" ) ) );
                addTipTitle( statusContainer, new Model<String>( eoiNeed.getStatusString() ) );
                item.add( statusContainer );
                item.add( new Label( "eoi.name", eoiNeed.getContentString() ) );
                item.add( newEoiSources( "eoi.sources", eoiNeed.getEoiSources() ) );
                item.add( newEoiSources( "eoi.requiredSources", eoiNeed.getRequiredEoiSources() ) );
                item.add( new Label( "eoi.status", eoiNeed.getStatusString() ) );
            }
        };
    }

    private static Component newEoiSources( String id, List<EoiSource> eoiSources ) {
        return new ListView<EoiSource>( id, eoiSources ) {
            @Override
            protected void populateItem( ListItem<EoiSource> item ) {
                EoiSource eoiSource = item.getModelObject();
                item.add( new Label( "source", eoiSource.toString() ) );
            }
        };
    }

    private List<InfoNeedsReportSegment> getReportSegments( QueryService queryService, ResourceSpec profile ) {

        Assignments allAssignments = queryService.getAssignments( false, false );
        Assignments assignments = allAssignments.with( profile );
        Commitments commitments = realizable(
                new Commitments( queryService, profile, allAssignments ),
                getAnalyst(),
                queryService.getPlan(),
                queryService );

        List<InfoNeedsReportSegment> reportSegments = new ArrayList<InfoNeedsReportSegment>();
        int i = 1;
        for ( Segment segment : getPlan().getSegments() ) {
            Assignments segmentAssignments = assignments.forSegment( segment );
            if ( !segmentAssignments.isEmpty() ) {
                InfoNeedsReportSegment reportSegment = new InfoNeedsReportSegment( i,
                        segment,
                        segmentAssignments,
                        commitments,
                        queryService,
                        getAnalyst() );
                if ( !reportSegment.isEmpty() )
                    reportSegments.add( reportSegment );
                i++;
            }
        }
        return reportSegments;
    }

    private static class InfoNeedsReportSegment extends ReportSegment {

        private Assignments assignments;

        private Commitments commitments;

        private List<InfoNeedsReportTask> reportTasks;

        private InfoNeedsReportSegment( int seq, Segment segment, Assignments assignments, Commitments commitments,
                                        QueryService queryService, Analyst analyst ) {
            super( seq, segment );
            this.assignments = assignments;
            this.commitments = commitments;
            reportTasks = findReportTasks( queryService, analyst );
        }

        private List<InfoNeedsReportTask> findReportTasks( QueryService queryService, Analyst analyst ) {
            List<InfoNeedsReportTask> results = new ArrayList<InfoNeedsReportTask>();
            int seq = 1;
            for ( Part part : assignments.getParts() ) {
                InfoNeedsReportTask reportTask = new InfoNeedsReportTask( getSeq(), part );
                reportTask.setTaskSeq( seq );
                seq++;
                reportTask.setTaskInfoNeeds( findTaskInfoNeedsForTask( assignments.assignedTo( part ),
                        queryService,
                        analyst ) );
                if ( !reportTask.isEmpty() )
                    results.add( reportTask );
            }
            return results;
        }

        public boolean isEmpty() {
            return reportTasks.isEmpty();
        }

        public List<Part> getAssignedTasks( final QueryService queryService ) {
            List<Part> parts = assignments.getParts();
            Collections.sort( parts, new Comparator<Part>() {
                @Override
                public int compare( Part part1, Part part2 ) {
                    return queryService.computePartPriority( part1 ).compareTo( queryService.computePartPriority( part2 ) );
                }
            } );
            return parts;
        }

        private List<TaskInfoNeeds> findTaskInfoNeedsForTask( Assignments assignmentsToTask,
                                                              final QueryService queryService, Analyst analyst ) {
            List<TaskInfoNeeds> taskInfoNeedsList = new ArrayList<TaskInfoNeeds>();
            Set<String> infos = new HashSet<String>();
            Part part = assignmentsToTask.getParts().get( 0 );
            for ( Flow input : part.getAllSharingReceives() ) {
                infos.add( input.getName() );
            }
            for ( Flow need : part.getNeeds() ) {
                infos.add( need.getName() );
            }
            for ( String info : infos ) {
                TaskInfoNeeds taskInfoNeeds = findTaskInfoNeedsForInfo( part, info, assignmentsToTask );
                taskInfoNeedsList.add( taskInfoNeeds );
            }
            Collections.sort( taskInfoNeedsList, new Comparator<TaskInfoNeeds>() {
                @Override
                public int compare( TaskInfoNeeds t1, TaskInfoNeeds t2 ) {
                    return t1.failureImpact( queryService ).compareTo( t2.failureImpact( queryService ) );
                }
            } );
            return taskInfoNeedsList;
        }

        private TaskInfoNeeds findTaskInfoNeedsForInfo( Part part, String info, Assignments assignmentsToTask ) {
            TaskInfoNeeds taskInfoNeeds = new TaskInfoNeeds( part, info );
            Commitments commitmentsToTaskInfo = new Commitments();
            Iterator<Flow> receives = part.receivesNamed( info );
            while ( receives.hasNext() ) {
                Flow.Intent intent = receives.next().getIntent();
                if ( intent != null )
                    taskInfoNeeds.setIntent( intent );
            }
            for ( Assignment assignment : assignmentsToTask ) {
                commitmentsToTaskInfo.addAll( commitments.to( assignment ).withInfo( info ) );
            }
            for ( EoiNeed eoiNeed : findEoiNeeds( part, info, commitmentsToTaskInfo ) ) {
                taskInfoNeeds.addEoiNeed( eoiNeed );
            }
            return taskInfoNeeds;
        }

        @SuppressWarnings( "unchecked" )
        private List<EoiNeed> findEoiNeeds( Part part, String info, Commitments commitmentsToTaskInfo ) {
            List<EoiNeed> eoiNeeds = new ArrayList<EoiNeed>();
            Set<String> eoiContents = new HashSet<String>();
            List<Flow> incomingFlows = new ArrayList<Flow>();
            for ( Flow incoming : part.getAllSharingReceives() ) {
                if ( Matcher.same( incoming.getName(), info ) ) {
                    incomingFlows.add( incoming );
                    for ( ElementOfInformation eoi : incoming.getEffectiveEois() ) {
                        eoiContents.add( eoi.getContent() );
                    }
                }
            }
            List<Flow> needs = new ArrayList<Flow>();
            for ( Flow need : part.getNeeds() ) {
                if ( Matcher.same( need.getName(), info ) ) {
                    needs.add( need );
                    for ( ElementOfInformation eoi : need.getEffectiveEois() ) {
                        eoiContents.add( eoi.getContent() );
                    }
                }
            }
            if ( eoiContents.isEmpty() ) {
                eoiContents.add( "" );
            }
            for ( final String content : eoiContents ) {
                List<Flow> needsForEoi = (List<Flow>) CollectionUtils.select( needs, new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return content.isEmpty() || CollectionUtils.exists( ( (Flow) object ).getEffectiveEois(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate(
                                            Object object ) {
                                        return Matcher.same( ( (ElementOfInformation) object ).getContent(),
                                                content );
                                    }
                                } );
                    }
                } );
                List<Flow> incomingWithEoi = (List<Flow>) CollectionUtils.select( incomingFlows, new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return content.isEmpty() || CollectionUtils.exists( ( (Flow) object ).getEffectiveEois(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate(
                                            Object object ) {
                                        return Matcher.same( ( (ElementOfInformation) object ).getContent(),
                                                content );
                                    }
                                } );
                    }
                } );
                Commitments commitmentsToTaskInfoWithEoi =
                        content.isEmpty() ? commitmentsToTaskInfo : commitmentsToTaskInfo.withEoi( content );
                EoiNeed eoiNeed = makeEoiNeed( content, incomingWithEoi, needsForEoi, commitmentsToTaskInfoWithEoi );
                eoiNeeds.add( eoiNeed );
            }
            return eoiNeeds;
        }

        private EoiNeed makeEoiNeed( String content, List<Flow> incomingWithEoi, List<Flow> needsForEoi,
                                     Commitments commitmentsToTaskInfoEoi ) {
            EoiNeed eoiNeed = new EoiNeed( content );
            for ( EoiSource eoiSource : findEoiSources( commitmentsToTaskInfoEoi ) ) {
                eoiNeed.addEoiSource( eoiSource );
            }
            for ( EoiSource requiredEoiSource : findRequiredEoiSources( incomingWithEoi ) ) {
                eoiNeed.addRequiredEoiSource( requiredEoiSource );
            }
            Flow combinedNeed = findCombinedNeed( needsForEoi );
            if ( combinedNeed != null ) {
                eoiNeed.addRequiredEoiSource( new EoiSource( null,
                        combinedNeed.getRestrictions().isEmpty()
                                ? null
                                : combinedNeed.getRestrictions().get( 0 ), // Whole class is obsolete anyway
                        combinedNeed.getMaxDelay(),
                        !combinedNeed.isAskedFor(),
                        combinedNeed.isRequired(),
                        true ) );
                eoiNeed.setNeed( combinedNeed );
            }
            return eoiNeed;
        }

        private static List<EoiSource> findEoiSources( Commitments commitmentsToTaskInfoEoi ) {
            List<EoiSource> eoiSources = new ArrayList<EoiSource>();
            for ( Commitment commitment : commitmentsToTaskInfoEoi ) {
                Flow sharing = commitment.getSharing();
                eoiSources.add( new EoiSource( commitment.getCommitter().getResourceSpec(),
                        null,
                        sharing.getMaxDelay(),
                        !sharing.isAskedFor(),
                        sharing.isRequired(),
                        false ) );
            }
            return eoiSources;
        }

        private static List<EoiSource> findRequiredEoiSources( List<Flow> incomingWithEoi ) {
            List<EoiSource> requiredEoiSources = new ArrayList<EoiSource>();
            for ( Flow incoming : incomingWithEoi ) {
                ResourceSpec resourceSpec = ( (Part) incoming.getSource() ).resourceSpec();
                Actor actor = resourceSpec.getActor();
                if ( actor == null || actor.isType() ) {
                    Flow.Restriction restriction = incoming.getRestrictions().isEmpty() // The whole class is obsolete anyway
                            ? null
                            : incoming.getRestrictions().get( 0 );
                    Delay maxDelay = incoming.getMaxDelay();
                    boolean notified = !incoming.isAskedFor();
                    requiredEoiSources.add( new EoiSource( resourceSpec,
                            restriction,
                            maxDelay,
                            notified,
                            incoming.isRequired(),
                            incoming.isNeed() ) );
                }
            }
            return requiredEoiSources;
        }

        private static Flow findCombinedNeed( List<Flow> needsForEoi ) {
            return needsForEoi.size() > 0 ? needsForEoi.get( 0 ) : null;
            // TODO merge the attributes of multiple synonymous needs
        }

        public List<InfoNeedsReportTask> getReportTasks() {
            return reportTasks;
        }
    }

    private static class InfoNeedsReportTask extends ReportTask {

        private List<TaskInfoNeeds> taskInfoNeeds;

        public InfoNeedsReportTask( int segmentSeq, Part part ) {
            super( segmentSeq, part );
        }

        public List<TaskInfoNeeds> getTaskInfoNeeds() {
            return taskInfoNeeds;
        }

        public void setTaskInfoNeeds( List<TaskInfoNeeds> taskInfoNeeds ) {
            this.taskInfoNeeds = taskInfoNeeds;
        }

        public boolean isEmpty() {
            return taskInfoNeeds.isEmpty();
        }
    }

    private static class TaskInfoNeeds implements Serializable {

        private Part part;

        private String info;

        private List<EoiNeed> eoiNeeds;

        private Flow.Significance significance = Flow.Significance.None;

        private String intentString = "";

        private TaskInfoNeeds( Part part, String info ) {
            this.part = part;
            this.info = info;
            eoiNeeds = new ArrayList<EoiNeed>();
        }

        public Part getPart() {
            return part;
        }

        public String getInfo() {
            return info;
        }

        public List<EoiNeed> getEoiNeeds() {
            return eoiNeeds;
        }

        public void addEoiNeed( EoiNeed eoiNeed ) {
            eoiNeeds.add( eoiNeed );
        }

        public Level failureImpact( QueryService queryService ) {
            return queryService.computePartPriority( part );
        }

        public String getImpactString() {
            String str;
            switch ( findSignificance() ) {
                case Useful:
                    str = "better execute";
                    break;
                case Critical:
                    str = "successfully execute";
                    break;
                case Triggers:
                    str = "trigger";
                    break;
                case Terminates:
                    str = "terminate";
                    break;
                default:
                    str = "";
            }
            return str;
        }

        private Flow.Significance findSignificance() {
            Flow.Significance significance = Flow.Significance.None;
            Iterator<Flow> receives = part.receivesNamed( info );
            while ( receives.hasNext() ) {
                Flow receive = receives.next();
                if ( receive.getSignificanceToTarget().compareTo( significance ) > 0 )
                    significance = receive.getSignificanceToTarget();
            }
            return significance;
        }

        public String getIntentString() {
            return intentString.isEmpty() ? "information" : intentString;
        }

        public void setIntent( Flow.Intent intent ) {
            if ( intentString.isEmpty() )
                intentString = intent.getLabel().toLowerCase();
            else
                intentString = "information";
        }
    }

    private static class EoiNeed implements Serializable {

        private String content;

        private Set<EoiSource> eoiSources;

        private Set<EoiSource> requiredEoiSources;

        private Flow need;

        private boolean required;

        private EoiNeed( String content ) {
            this.content = content;
            eoiSources = new HashSet<EoiSource>();
            requiredEoiSources = new HashSet<EoiSource>();
        }

        public String getContentString() {
            return content.isEmpty() ? "(none)" : content;
        }

        public Flow getNeed() {
            return need;
        }

        public void setNeed( Flow need ) {
            this.need = need;
            required = required || need.isRequired();
        }

        public List<EoiSource> getEoiSources() {
            ArrayList<EoiSource> list = new ArrayList<EoiSource>( eoiSources );
            Collections.sort( list, new Comparator<EoiSource>() {
                @Override
                public int compare( EoiSource s1, EoiSource s2 ) {
                    return s1.isNeed() ? -1
                            : s2.isNeed() ? 1
                            : 0;
                }
            } );
            return list;
        }

        public List<EoiSource> getRequiredEoiSources() {
            ArrayList<EoiSource> list = new ArrayList<EoiSource>( requiredEoiSources );
            Collections.sort( list, new Comparator<EoiSource>() {
                @Override
                public int compare( EoiSource s1, EoiSource s2 ) {
                    if ( s1.isNeed() )
                        return -1;
                    if ( s2.isNeed() )
                        return 1;
                    else
                        return 0;
                }
            } );
            return list;
        }

        private void addEoiSource( EoiSource source ) {
            eoiSources.add( source );
            required = required || source.isRequired();
        }

        private void addRequiredEoiSource( EoiSource source ) {
            requiredEoiSources.add( source );
            required = required || source.isRequired();
        }

        public Status status() {
            int timelySourceCount = countTimelySources();
            if ( required && timelySourceCount == 0 ) {
                return new Status( Status.RED, "Essential and no timely source" );
            } else if ( required && timelySourceCount == 1 ) {
                return new Status( Status.YELLOW, "Essential but only one timely source" );
            } else if ( !required && timelySourceCount == 0 ) {
                return new Status( Status.YELLOW, "Not essential and no timely source" );
            } else {
                return new Status( Status.GREEN, "OK" );
            }
        }

        private int countTimelySources() {
            int maxDelayAmount = Integer.MAX_VALUE;
            if ( need != null ) {
                maxDelayAmount = need.getMaxDelay().getAmount();
            }
            int count = 0;
            for ( EoiSource eoiSource : eoiSources ) {
                if ( eoiSource.getResourceSpec() != null && eoiSource.getMaxDelay().getAmount() <= maxDelayAmount ) {
                    count++;
                }
            }
            return count;
        }

        public String getIcon() {
            return "images/bullet-" + status().getColor() + ".png";
        }

        public String getStatusString() {
            return status().getText();
        }
    }

    private static class Status implements Serializable, Comparable<Status> {

        private static final String RED = "red";

        private static final String YELLOW = "yellow";

        private static final String GREEN = "green";

        private String color = GREEN;

        private String text = "OK";

        private Status( String color, String text ) {
            this.color = color;
            this.text = text;
        }

        public String getColor() {
            return color;
        }

        public void setColor( String color ) {
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public void setText( String text ) {
            this.text = text;
        }

        @Override
        public int compareTo( Status other ) {
            if ( color.equals( other.getColor() ) )
                return 0;
            if ( color.equals( RED ) )
                return -1;
            if ( color.equals( GREEN ) )
                return 1;
            if ( color.equals( YELLOW ) && other.getColor().equals( GREEN ) )
                return -1;
            else
                return 1;
        }
    }

    private static class EoiSource implements Serializable {

        private ResourceSpec resourceSpec;

        private Flow.Restriction restriction;

        private Delay maxDelay;

        boolean notified;

        boolean required;

        boolean need;

        private EoiSource( ResourceSpec resourceSpec, Flow.Restriction restriction, Delay maxDelay, boolean notified,
                           boolean required, boolean need ) {
            this.resourceSpec = resourceSpec;
            this.restriction = restriction;
            this.maxDelay = maxDelay;
            this.notified = notified;
            this.required = required;
            this.need = need;
        }

        public ResourceSpec getResourceSpec() {
            return resourceSpec;
        }

        public Delay getMaxDelay() {
            return maxDelay;
        }

        public boolean isNotified() {
            return notified;
        }

        public boolean isNeed() {
            return need;
        }

        public Flow.Restriction getRestriction() {
            return restriction;
        }

        public boolean isRequired() {
            return required;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            if ( resourceSpec == null )
                sb.append( "Anyone" );
            else
                sb.append( resourceSpec.getLabel() );
            if ( restriction != null ) {
                sb.append( " " );
                sb.append( restriction.getLabel( false ) );
            }
            sb.append( " (" );
            if ( !maxDelay.isImmediate() )
                sb.append( "within " );
            sb.append( maxDelay.toString() );
            sb.append( " )" );
            return sb.toString();
        }

        public boolean equals( Object other ) {
            return other instanceof EoiSource
                    && ResourceSpec.areEqualOrNull( resourceSpec, ( (EoiSource) other ).getResourceSpec() )
                    && Flow.Restriction.same( restriction, ( (EoiSource) other ).getRestriction() )
                    && maxDelay.equals( ( (EoiSource) other ).getMaxDelay() )
                    && notified == ( (EoiSource) other ).isNotified() && need == ( (EoiSource) other ).isNeed();
        }

        public int hashCode() {
            int hash = 1;
            if ( resourceSpec != null )
                hash = hash * 31 + resourceSpec.hashCode();
            if ( restriction != null )
                hash = hash * 31 + restriction.hashCode();
            hash = hash * 31 + maxDelay.hashCode();
            hash = hash * 31 + Boolean.valueOf( notified ).hashCode();
            hash = hash * 31 + Boolean.valueOf( need ).hashCode();
            return hash;
        }
    }
}
