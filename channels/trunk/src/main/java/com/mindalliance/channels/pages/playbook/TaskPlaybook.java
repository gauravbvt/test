package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.QueryService;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

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

    private List<ResourceSpec> specList;

    private List<SynonymFlowSet> outputs;

    private List<SynonymFlowSet> inputs;

    private List<Part> tasks;

    public TaskPlaybook( PageParameters parameters ) {
        super( parameters );
        Actor actor = getActor();
        Part part = getPart();

        if ( part == null ) {
            if ( parameters.containsKey( PART_PARM ) ) {
                // Invalid part parameter, trim it and redirect to summary
                PageParameters parms = new PageParameters();
                parms.put( ACTOR_PARM, parameters.getString( ACTOR_PARM ) );
                setRedirect( true );
                throw new RestartResponseException( getClass(), parms );
            } else {
                // Quietly show the actor summary, keeping same URL
                throw new RestartResponseException( ActorPlaybook.class, parameters );
            }
        }

        init( actor, part, getUser() );
    }

    private void init( Actor actor, Part part, User user ) {
        ResourceSpec actorSpec = ResourceSpec.with( actor );
        String taskName = part.getTask();
        String desc = part.getDescription();

        sortFlows( part, actorSpec, getQueryService() );

        add(
            new Label( "title", actor.getName() + " - " + taskName ),
            new Label( "header", taskName ),
            new Label( "role", getRoleString( part, actor ) ),
            new Label( "desc", desc ).setVisible( !desc.isEmpty() ),

            createRepeat( part.getRepeatsEvery() ).setVisible( part.isRepeating() ),
            createCompletion( part.getCompletionTime() ).setVisible( part.isSelfTerminating() ),
            createRisks( part.getMitigations() ),

            new AttachmentListPanel( "attachments", part.getAttachments() ),
            createTaskList( actor, part, tasks ),

            new WebMarkupContainer( "flows" )
                    .add(
                        new ListView<ResourceSpec>( "actorSpec", specList ) {
                            @Override
                            protected void populateItem( ListItem<ResourceSpec> item ) {
                                ResourceSpec resourceSpec = item.getModelObject();
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

    private void sortFlows( Part part, ResourceSpec actorSpec, QueryService service ) {

        List<Flow> inputFlows = new ArrayList<Flow>();
        List<Flow> outputFlows = new ArrayList<Flow>();
        List<Flow> otherOutputFlows = new ArrayList<Flow>();
        Set<ResourceSpec> actorSpecs = new HashSet<ResourceSpec>();

        for ( Flow flow : getFlows( part, part.getScenario() ) )
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
                    .add( new Label( "event", getEventName() ) )
                    .setVisible( part.isTerminatesEvent() ) )

            .setVisible( !parts.isEmpty() || part.isTerminatesEvent() );
    }

    private static List<Part> getSubtasks( List<Flow> flows ) {
        Set<Part> parts = new HashSet<Part>();
        for ( Flow flow : flows )
            parts.add( (Part) flow.getTarget() );

        List<Part> tasks = new ArrayList<Part>( parts );
        Collections.sort( tasks );
        return tasks;
    }

    private static List<Flow> getFlows( Part part, Scenario scenario ) {
        List<Flow> result = new ArrayList<Flow>();

        for ( Iterator<Flow> iterator = scenario.flows(); iterator.hasNext() ; ) {
            Flow flow = iterator.next();
            addSources( result, part, flow );
            addTargets( result, part, flow );
        }

        return result;
    }

    private static void addTargets( List<Flow> result, Part part, Flow flow ) {
        Node target = flow.getTarget();
        if ( part.equals( target ) )
            result.add( flow );
        else if ( target instanceof Connector ) {
            Iterator<ExternalFlow> exts = ( (Connector) target ).externalFlows();
            while ( exts.hasNext() ) {
                ExternalFlow externalFlow = exts.next();
                if ( part.equals( externalFlow.getTarget() ) )
                    result.add( externalFlow );
            }
        }
    }

    private static void addSources( List<Flow> result, Part part, Flow flow ) {
        Node source = flow.getSource();
        if ( part.equals( source ) )
            result.add( flow );
        else if ( source instanceof Connector ) {
            Iterator<ExternalFlow> exts = ( (Connector) source ).externalFlows();
            while ( exts.hasNext() ) {
                ExternalFlow externalFlow = exts.next();
                if ( part.equals( externalFlow.getSource() ) )
                    result.add( externalFlow );
            }
        }
    }

    private static Component createRisks( List<Risk> risks ) {
        final int size = risks.size();
        return new WebMarkupContainer( "risk-note" )
            .add( new ListView<Risk>( "risks", risks ) {
                    @Override
                    protected void populateItem( ListItem<Risk> item ) {
                        Risk risk = item.getModelObject();
                        int index = item.getIndex();
                        String riskString = new StringBuilder( risk.getLabel() )
                                .append( index == size - 1 ? "."
                                       : index >= 0 && index == size - 2 ? " and "
                                       : ", " )
                                .toString();

                        item.add( new Label( "risk", riskString ).setRenderBodyOnly( true ) )
                            .setRenderBodyOnly( true );
                    }
                } )
            .setRenderBodyOnly( true )
            .setVisible( !risks.isEmpty() );
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
        ResourceSpec resourceSpec = new ResourceSpec( part.resourceSpec() );
        resourceSpec.setActor( actor );
        return resourceSpec.toString();
    }
}
