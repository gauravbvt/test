package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Goal;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Event-based playbook.
 */
public class TaskPlaybook extends PlaybookPage {

    /** Columns headers in the flow table. */
    private List<ResourceSpec> specList;

    /** Combine output flows. */
    private List<SynonymFlowSet> outputs;

    /** Combine input flows. */
    private List<SynonymFlowSet> inputs;

    /** Self-triggered tasks. */
    private List<Part> tasks;

    public TaskPlaybook( PageParameters parameters ) {
        super( parameters );
        Actor actor = getActor();
        Part part = getPart();

        if ( part == null ) {
            if ( parameters.getNamedKeys().contains( PART_PARM ) ) {
                // Invalid part parameter, trim it and redirect to summary
                PageParameters parms = new PageParameters();
                parms.set( ACTOR_PARM, parameters.get( ACTOR_PARM ).toString() );
                throw new RestartResponseException( getClass(), parms );
            } else {
                // Quietly show the actor summary, keeping same URL
                throw new RestartResponseException( ActorPlaybook.class, parameters );
            }
        }

        init( actor, part );
    }

    private void init( Actor actor, Part part ) {
        ResourceSpec actorSpec = new ResourceSpec( actor );
        String taskName = part.getTask();
        sortFlows( part, actorSpec, getQueryService() );

        add(
            new Label( "title", actor.getName() + " - " + taskName ),
            new Label( "header", taskName ),
            new Label( "role", getRoleString( part, actor ) ),
            createDescription( part ),

            new AttachmentListPanel( "attachments", part.getAttachments() ),
            createTaskList( actor, part, tasks ),

            new WebMarkupContainer( "flows" )
                    .add(
                        new ListView<ResourceSpec>( "actorSpec", specList ) {
                            @Override
                            protected void populateItem( ListItem<ResourceSpec> item ) {
                                Specable resourceSpec = item.getModelObject();
                                item.add( new Label( "actorLabel", resourceSpec.toString() )
                                                    .setRenderBodyOnly( true ) );
                            }
                        },

                        new FlowListPanel( "inputs", specList, actorSpec, inputs, true, 0 ),
                        new FlowListPanel( "outputs", specList, actorSpec, outputs, false,
                                           inputs.size() ) )

                    .setVisible( !( specList.isEmpty() || inputs.isEmpty() && outputs.isEmpty() ) ),

             new BookmarkablePageLink<TaskPlaybook>( "top", TaskPlaybook.class )
        );

    }

    private static Component createDescription( Part part ) {
        String partDescription = part.getDescription();
        boolean hasDescription = !partDescription.isEmpty();
        boolean hasGoals = !part.getGoals().isEmpty();

        return new WebMarkupContainer( "all" ).add(
                new Label( "desc", partDescription ).setVisible( hasDescription ),
                createRepeat( part.getRepeatsEvery() ).setVisible( part.isRepeating() ),
                createCompletion( part.getCompletionTime() ).setVisible( part.isSelfTerminating() ),
                createGoals( part.getGoals() ).setVisible( hasGoals )

        ).setVisible( hasDescription || part.isRepeating()
                                     || part.isSelfTerminating() || hasGoals );
    }

    private void sortFlows( Part part, ResourceSpec actorSpec, QueryService service ) {

        List<Flow> inputFlows = new ArrayList<Flow>();
        List<Flow> outputFlows = new ArrayList<Flow>();
        List<Flow> otherOutputFlows = new ArrayList<Flow>();
        Set<ResourceSpec> actorSpecs = new HashSet<ResourceSpec>();

        for ( Flow flow : getFlows( part, part.getSegment() ) )
            if ( part.equals( flow.getTarget() ) ) {
                if ( !flow.isTriggeringToTarget() ) {
                    Set<ResourceSpec> specs = expandSpecs( service, flow.getSource() );
                    if ( specs.size() > 1 || !specs.contains( actorSpec ) )
                        inputFlows.add( flow );
                    actorSpecs.addAll( specs );
                }
            } else {
                Set<ResourceSpec> specs = expandSpecs( service, flow.getTarget() );
                if ( specs.size() > 1 || !specs.contains( actorSpec ) )
                    otherOutputFlows.add( flow );
                outputFlows.add( flow );
                actorSpecs.addAll( specs );
            }

        actorSpecs.remove( actorSpec );
        specList = new ArrayList<ResourceSpec>( actorSpecs );
        Collections.sort( specList );

        ResourceSpec partSpec = part.resourceSpec();
        outputs = new FlowSet( partSpec, false, otherOutputFlows ).getSynonymSets();
        inputs = new FlowSet( partSpec, true, inputFlows ).getSynonymSets();

        FlowSet flowSet = new FlowSet( partSpec, false, outputFlows );
        tasks = getSubtasks( flowSet.getFlows( service, actorSpec ) );
    }

    private Component createTaskList( final Actor actor, Part part, List<Part> parts ) {
        return new WebMarkupContainer( "other" )
            .add(
                new ListView<Part>( "task", parts ) {
                    @Override
                    protected void populateItem( ListItem<Part> item ) {
                        Part otherPart = item.getModelObject();
                        item.add( new BookmarkablePageLink<TaskPlaybook>(
                                    "task-link", TaskPlaybook.class )
                                  .setParameter( ACTOR_PARM, actor.getId() )
                                  .setParameter( PART_PARM, otherPart.getId() )
                                  .add( new Label( "task-label", otherPart.getTask() ) ) );
                    }
                },

                new WebMarkupContainer( "end-event" )
                    .add( new Label( "event-phase", getEventPhaseName() ) )
                    .setVisible( part.isTerminatesEventPhase() ) )

            .setVisible( !parts.isEmpty() || part.isTerminatesEventPhase() );
    }

    private static List<Part> getSubtasks( List<Flow> flows ) {
        Set<Part> parts = new HashSet<Part>();
        for ( Flow flow : flows )
            parts.add( (Part) flow.getTarget() );

        List<Part> tasks = new ArrayList<Part>( parts );
        Collections.sort( tasks );
        return tasks;
    }

    private static List<Flow> getFlows( Part part, Segment segment ) {
        List<Flow> result = new ArrayList<Flow>();

        for ( Iterator<Flow> iterator = segment.flows(); iterator.hasNext() ; ) {
            Flow flow = iterator.next();
            addTargets( result, part, flow );
            addSources( result, part, flow );
        }

        return result;
    }

    private static void addSources( List<Flow> result, Part part, Flow flow ) {
        Node target = flow.getTarget();
        if ( part.equals( target ) ) {
            Node source = flow.getSource();
            if ( source instanceof Connector ) {
                Iterator<ExternalFlow> exts = ( (Connector) source ).externalFlows();
                while ( exts.hasNext() )
                    result.add( exts.next() );
            } else
                result.add( flow );
        }
    }

    private static void addTargets( List<Flow> result, Part part, Flow flow ) {
        Node source = flow.getSource();
        if ( part.equals( source ) ) {
            Node target = flow.getTarget();
            if ( target instanceof Connector ) {
                Iterator<ExternalFlow> exts = ( (Connector) target ).externalFlows();
                while ( exts.hasNext() )
                    result.add( exts.next() );
            } else
                result.add( flow );
        }
    }

    private static Component createGoals( List<Goal> goals ) {
        final int size = goals.size();
        return new WebMarkupContainer( "goal-note" )
            .add( new ListView<Goal>( "goals", goals ) {
                    @Override
                    protected void populateItem( ListItem<Goal> item ) {
                        Goal goal = item.getModelObject();
                        int index = item.getIndex();
                        String goalString = new StringBuilder( goal.getFullLabel() )
                                .append( index == size - 1 ? "."
                                       : index >= 0 && index == size - 2 ? " and "
                                       : ", " )
                                .toString();

                        item.add( new Label( "goal", goalString ).setRenderBodyOnly( true ) )
                            .setRenderBodyOnly( true );
                    }
                } )
            .setRenderBodyOnly( true )
            .setVisible( !goals.isEmpty() );
    }

    private static Component createCompletion( Delay time ) {
        String completion = time.getSeconds() > 0 ? "in " + time : time.toString();
        return new WebMarkupContainer( "completion-note" )
                .add( new Label( "completion", completion ).setRenderBodyOnly( true ) )
                .setRenderBodyOnly( true );
    }

    private static Component createRepeat( Delay time ) {
        String timeString = time.getSeconds() > 0 ? "every " + time : "continuously";
        return new WebMarkupContainer( "repeat-note" )
                .add( new Label( "repeat", timeString ).setRenderBodyOnly( true ) )
                .setRenderBodyOnly( true );
    }

    private static String getRoleString( Part part, Actor actor ) {
        return new ResourceSpec(
                actor, part.getRole(), part.getOrganization(), part.getJurisdiction()
        ).toString();
    }
}
