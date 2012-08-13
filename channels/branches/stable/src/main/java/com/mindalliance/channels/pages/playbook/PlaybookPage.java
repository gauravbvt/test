package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A generic playbook page.
 */
public abstract class PlaybookPage extends AbstractChannelsWebPage {

    /** The name of the agent id parameter. */
    public static final String ACTOR_PARM = "0";

    /** The name of the part id parameter. */
    public static final String PART_PARM = "1";


    /** The logged-in user. */
    @SpringBean
    private ChannelsUser user;


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

        actor = getActualActor();
        part = getParm( PART_PARM, Part.class );

        if ( actor == null ) {
            if ( parameters.getNamedKeys().contains( ACTOR_PARM ) ) {
                throw new RestartResponseException( getClass() );
            } else {
                // No actor specified. Just show top page.
                throw new RestartResponseException( MainPage.class );
            }
        }
    }

    /**
     * Get actor for current user, if no actor was explicitely specify.
     * @return an actor or null, if nothing relevant was found.
     */
    private Actor getActualActor() {
        Actor actualActor = getParm( ACTOR_PARM, Actor.class );
       /* if ( actualActor == null ) {
            Participation participation = getQueryService().findOrCreate( Participation.class, user.getUsername() );
            actualActor = participation.getActor();
        }*/
        return actualActor;
    }

    private <T extends ModelObject> T getParm( String parm, Class<T> parmClass ) {
        T result = null;
        PageParameters parms = getPageParameters();
        if ( parms.getNamedKeys().contains( parm ) )
            try {
                result = getQueryService().find( parmClass, Long.valueOf( parms.get( parm ).toString() ) );

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
            for ( ResourceSpec spec : expandSpecs( getQueryService(), flow.getSource() ) )
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
     * Find actual actor or irreducible spec associated with a node.
     * @param service used for resolution
     * @param node the initial node
     * @return resource specification
     */
    static Set<ResourceSpec> expandSpecs( QueryService service, Node node ) {
        return expandSpecs( service, node, new HashSet<ResourceSpec>() );
    }

    /**
     * Add actor specifications for a given node (either a simple actor or a role specification,
     * if no actors are assigned).
     * @param service the service to use
     * @param node the node
     * @param result collection to add to
     * @return the result, for convenience
     */
    static <T extends Collection<ResourceSpec>> T expandSpecs(
            QueryService service, Node node, T result ) {

        Set<ResourceSpec> rawSpecs = new HashSet<ResourceSpec>();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> flows = ( (Connector) node ).externalFlows();
            while ( flows.hasNext() )
                rawSpecs.add( flows.next().getPart().resourceSpec() );
        } else
            rawSpecs.add( ( (Part) node ).resourceSpec() );

        for ( ResourceSpec spec : rawSpecs ) {
            List<Actor> actors = service.findAllActualActors( spec );
            if ( actors.isEmpty() )
                result.add( spec );
            else
                for ( Actor a : actors )
                    result.add( new ResourceSpec( a, spec.getRole(), spec.getOrganization(),
                                                  spec.getJurisdiction() ) );
        }

        return result;
    }

    String getEventPhaseName() {
        return part.getSegment().getPhaseEventTitle();
    }
}
