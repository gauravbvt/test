package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A set of flows with the same label.
 */
final class SynonymFlowSet implements Comparable<SynonymFlowSet>, Serializable {

    /** The flows contributing to this spec. */
    private final Set<Flow> flows = new HashSet<Flow>();

    /** First added flow, for extracting common properties. */
    private final Flow firstFlow;

    /** If flows are incoming or outgoing. */
    private final boolean incoming;

    //---------------------------------------
    SynonymFlowSet( Flow flow, boolean incoming ) {
        this.incoming = incoming;
        add( flow );
        firstFlow = flow;
    }

    /**
     * Add and analyze a flow.
     * @param flow the flow
     */
    public void add( Flow flow ) {
        flows.add( flow );
    }

    /**
     * @return a label to describe all the flows
     */
    public String getLabel() {
        String label = firstFlow.getName();
        return firstFlow.isAskedFor() ? label + "?" : label;
    }

    /**
     * @return a string describing the set of causes for all the flows.
     */
    public String getCauseString() {
        StringBuilder result = new StringBuilder();
        result.append( "because of " );

        Iterator<Event> it = getCauses().iterator();
        do {
            result.append( it.next().getName().toLowerCase() );
            if ( it.hasNext() )
                result.append( " or " );
        } while ( it.hasNext() );

        return result.toString();
    }

    private Set<Event> getCauses() {
        Set<Event> eventCauses = new HashSet<Event>();
        for ( Flow flow : flows )
            eventCauses.add( getSourcePart( flow ).getScenario().getEvent() );

        return eventCauses;
    }

    /**
     * Get the actual parts associated with this spec.
     * @return a list of parts
     */
    public List<Part> getParts() {
        Set<Part> parts = new HashSet<Part>();
        for ( Flow flow : flows )
            parts.add( getTargetPart( flow ) );

        List<Part> result = new ArrayList<Part>( parts );
        Collections.sort( result );
        return result;
    }

    private Part getTargetPart( Flow flow ) {
        return (Part) ( incoming ? flow.getTarget() : flow.getSource() );
    }

    private Part getSourcePart( Flow flow ) {
        return (Part) ( incoming ? flow.getSource() : flow.getTarget() );
    }

    /**
     * @return if this set of flows touches more than one part.
     */
    public boolean isMultipart() {
        Set<Node> parts = new HashSet<Node>();
        for ( Flow flow : flows )
            parts.add( getTargetPart( flow ) );
        return parts.size() > 1;
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
    public int compareTo( SynonymFlowSet o ) {
        return getLabel().compareTo( o.getLabel() );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the other object
     * @return true if the objects are equal
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;

        if ( obj != null && getClass() == obj.getClass() && obj instanceof SynonymFlowSet )
            return getLabel().equals( ( (SynonymFlowSet) obj ).getLabel() );

        return false;
    }

    /**
     * Allow indexing of flowspecs.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return getLabel().hashCode();
    }

    public long getTargetId() {
        return getTargetPart( firstFlow ).getId();
    }

    /**
     * Project the flow with their actual destinations.
     * @param service the service for resolution to actual actors
     * @param exception spec to ignore in the indexing, if not null
     * @return flows, indexed by source or target specs
     */
    public Map<ResourceSpec,Flow> getProjection( QueryService service, ResourceSpec exception ) {
        Map<ResourceSpec, Flow> map = new HashMap<ResourceSpec, Flow>();
        for ( Flow flow : flows ) {
            Node node = incoming ? flow.getSource() : flow.getTarget();
            List<Flow> narrowedFlows = narrowFlow( flow, node );
            for ( ResourceSpec otherSpec : PlaybookPage.expandSpecs( service, node ) )
                if ( exception == null || !otherSpec.equals( exception ) )
                    for ( Flow narrowedFlow : narrowedFlows )
                        map.put( otherSpec, narrowedFlow );
        }

        return map;
    }

    private static List<Flow> narrowFlow( Flow flow, Node node ) {
        List<Flow> narrowedFlows = new ArrayList<Flow>();
        if ( node.isConnector() ) {
            Iterator<ExternalFlow> iterator = ( (Connector) node ).externalFlows();
            while ( iterator.hasNext() )
                narrowedFlows.add( iterator.next() );
        } else
            narrowedFlows.add( flow );
        return narrowedFlows;
    }

    /**
     * @return consolidated attachments for all flows in this set.
     */
    public List<Attachment> getAttachments() {
        Set<Attachment> attachments = new HashSet<Attachment>();
        for ( Flow flow : flows )
            attachments.addAll( flow.getAttachments() );

        List<Attachment> result = new ArrayList<Attachment>( attachments );
        Collections.sort( result );
        return result;
    }

    /**
     * Get the first flow associated with a resource spec.
     * @param service service for resolution of roles
     * @param spec the spec
     * @return a flow, or null if none
     */
    public Flow getFlow( QueryService service, ResourceSpec spec ) {
        return getProjection( service, null ).get( spec );
    }

    public boolean isIncoming() {
        return incoming;
    }
}
