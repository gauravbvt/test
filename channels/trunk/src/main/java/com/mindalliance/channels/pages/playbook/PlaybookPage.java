package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Connector;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A generic playbook page.
 */
public abstract class PlaybookPage extends WebPage {

    /** The name of the agent id parameter. */
    static final String ACTOR_PARM = "0";

    /** The name of the part id parameter. */
    static final String PART_PARM = "1";

    /** The current plan. */
    @SpringBean
    private Plan plan;

    /** The logged-in user. */
    @SpringBean
    private User user;

    /** Data access. */
    @SpringBean
    private QueryService queryService;

    /** The actor of this page. */
    private final Actor actor;

    /** The part, if available. */
    private final Part part;

    //----------------------------------------
    protected PlaybookPage() {
        setStatelessHint( true );
        actor = null;
        part = null;
    }

    protected PlaybookPage( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        actor = getParm( ACTOR_PARM, Actor.class );
        part = getParm( PART_PARM, Part.class );

        if ( actor == null ) {
            if ( parameters.containsKey( ACTOR_PARM ) ) {
                // Invalid actor parameter. Forget other parameters and redirect
                setRedirect( true );
                throw new RestartResponseException( getClass() );
            } else {
                // No actor specified. Just show top page.
                throw new RestartResponseException( MainPage.class );
            }
        }
    }

    final Plan getPlan() {
        return plan;
    }

    final QueryService getQueryService() {
        return queryService;
    }

    final User getUser() {
        return user;
    }

    private <T extends ModelObject> T getParm( String parm, Class<T> parmClass ) {
        T result = null;
        PageParameters parms = getPageParameters();
        if ( parms.containsKey( parm ) )
            try {
                result = queryService.find( parmClass, Long.valueOf( parms.getString( parm ) ) );

            } catch ( NumberFormatException ignored ) {
                result = null;

            } catch ( NotFoundException ignored ) {
                result = null;
            }

        return result;
    }

    Actor getActor() {
        return actor;
    }

    Part getPart() {
        return part;
    }

    /**
     * Classify flows that start tasks according to sources.
     * Resolve to single actors if appropriate.
     * @param flows the flows to sort
     * @return flows, sorted by resource specs.
     */
    List<FlowSet> sortFlows( Set<Flow> flows ) {
        Map<ResourceSpec, FlowSet> targets = new HashMap<ResourceSpec, FlowSet>();
        for ( Flow flow : flows ) {
            for ( ResourceSpec spec : addActorSpecs( getQueryService(),
                                                     new HashSet<ResourceSpec>(),
                                                     flow.getSource() ) )
            {
                if ( !spec.isActor() || !actor.equals( spec.getActor() ) ) {
                    FlowSet flowSet = targets.get( spec );
                    if ( flowSet == null ) {
                        flowSet = new FlowSet( spec, true );
                        targets.put( spec, flowSet );
                    }
                    flowSet.add( flow );
                }
            }
        }

        List<FlowSet> result = new ArrayList<FlowSet>( targets.values() );
        Collections.sort( result );
        return result;
    }

    /**
     * Add actor specifications for a given node (either a simple actor or a role specification,
     * if no actors are assigned).
     * @param <T> a subclass of a collection of resource specs
     * @param service the service to use
     * @param result collection to add to
     * @param node the node
     * @return the result, for convenience
     */
    static <T extends Collection<ResourceSpec>> T addActorSpecs(
            QueryService service, T result, Node node ) {

        Set<ResourceSpec> rawSpecs = new HashSet<ResourceSpec>();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> flows = ( (Connector) node ).externalFlows();
            while ( flows.hasNext() )
                rawSpecs.add( flows.next().getPart().resourceSpec() );
        } else
            rawSpecs.add( ( (Part) node ).resourceSpec() );

        for ( ResourceSpec spec : rawSpecs ) {
            List<Actor> actors = service.findAllActors( spec );
            if ( actors.isEmpty() )
                result.add( spec );
            else
                for ( Actor a : actors )
                    result.add( ResourceSpec.with( a ) );
        }

        return result;
    }

    String getEventName() {
        return part.getScenario().getEvent().getName();
    }
}
