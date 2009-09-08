package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Flows from the same source or target.
 */
class FlowSet implements Comparable<FlowSet> {

    /** The source or target specification. */
    private final ResourceSpec spec;

    /** Initiating flows from the source, indexed by flow name. */
    private final Map<String, SynonymFlowSet> synonymSets = new HashMap<String, SynonymFlowSet>();

    /** True if specification is the target of the flows. */
    private final boolean incoming;

    FlowSet( ResourceSpec spec, boolean incoming ) {
        this.spec = spec;
        this.incoming = incoming;
    }

    FlowSet( ResourceSpec spec, boolean incoming, Collection<Flow> flows ) {
        this( spec, incoming );
        addAll( flows );
    }

    private ResourceSpec getSpec() {
        return spec;
    }

    /**
     * @return the specification string
     */
    @Override
    public String toString() {
        return spec.toString();
    }

    /**
     * Test if this set is the same as another object.
     * @param obj the other object
     * @return true if equal
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;

        return obj != null && getClass() == obj.getClass()
               && spec.equals( ( (FlowSet) obj ).getSpec() );
    }

    /**
     * Allow indexing of flow sets.
     * @return a hash code
     */
    @Override
    public int hashCode() {
        return spec.hashCode();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param  o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    public int compareTo( FlowSet o ) {
        return spec.compareTo( o.getSpec() );
    }

    /**
     * Add and classify a flow.
     * @param flow the flow
     */
    public void add( Flow flow ) {
        String key = getKey( flow );
        SynonymFlowSet set = synonymSets.get( key );
        if ( set == null )
            synonymSets.put( key, new SynonymFlowSet( flow, incoming ) );
        else
            set.add( flow );
    }

    private static String getKey( Flow flow ) {
        String key = flow.getName();
        return flow.isAskedFor() ? key + "?" : key;
    }

    /**
     * Classify a bunch of flows.
     * @param flows the flows
     */
    private void addAll( Collection<Flow> flows ) {
        for ( Flow flow : flows )
            add( flow );
    }

    /**
     * @return the flow specification attached to this category.
\     */
    public List<SynonymFlowSet> getSynonymSets() {
        List<SynonymFlowSet> result = new ArrayList<SynonymFlowSet>( synonymSets.values() );
        Collections.sort( result );
        return result;
    }

    /**
     * Find all flows associated with an actor specification.
     * @param service the service to use for role resolution
     * @param actorSpec the actor spec
     * @return a sorted list of flows
     */
    public List<Flow> getFlows( QueryService service, ResourceSpec actorSpec ) {
        List<Flow> list = new ArrayList<Flow>();
        for ( SynonymFlowSet set : synonymSets.values() ) {
            Flow flow = set.getFlow( service, actorSpec );
            if ( flow != null )
                list.add( flow );
        }
        return list;
    }

    public String getSourceString() {
        return spec.toString();
    }

    public boolean isNotEmpty() {
        return !synonymSets.isEmpty();
    }

    public Actor getActor() {
        return spec.getActor();
    }
}
