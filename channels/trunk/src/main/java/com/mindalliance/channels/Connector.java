package com.mindalliance.channels;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.text.MessageFormat;

/**
 * A connector to unspecified node(s) outside of the scenario.
 */
public class Connector extends Node {

    /** The connections from external scenarios. */
    private Set<ExternalFlow> externalFlows;

    public Connector() {
        setExternalFlows( new HashSet<ExternalFlow>() );
    }

    @Override
    public boolean isConnector() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        final boolean isInput = isInput();
        final Flow inner = getInnerFlow();
        final Part part  = (Part) ( isInput ? inner.getTarget() :  inner.getSource() );
        return MessageFormat.format( isInput ? "{0} to {1} (in {2})" : "{0} from {1} (in {2})",
                                     inner.getName(),
                                     part.getName(),
                                     part.getScenario() );
    }

    /**
     * Is the connector a source (true) or target (false)?
     * @return -- whether source or target
     */
    public boolean isInput() {
        Iterator<Flow> outs = outcomes();
        return outs.hasNext();
     }

    /**
     * Gets the inner flow between part and connector
     * @return -- the connector's inner flow
     */
    public Flow getInnerFlow() {
        return isInput() ? outcomes().next() : requirements().next();
    }

    /**
     * Are there external flows to or from this connector?
     * @return -- boolean
     */
    public boolean isConnected() {
        return !getExternalFlows().isEmpty();
    }

    private Set<ExternalFlow> getExternalFlows() {
        return externalFlows;
    }

    private void setExternalFlows( Set<ExternalFlow> externalConnections ) {
        externalFlows = externalConnections;
    }

    /**
     * @return an iterator on external flows.
     */
    public Iterator<ExternalFlow> externalFlows() {
        return getExternalFlows().iterator();
    }

    /**
     * Connect to an external flow.
     * @param externalFlow the flow
     */
    public void addExternalFlow( ExternalFlow externalFlow ) {
        getExternalFlows().add( externalFlow );
    }

    /**
     * Disconnect an external flow.
     * @param externalFlow the flow
     */
    public void removeExternalFlow( ExternalFlow externalFlow ) {
        getExternalFlows().remove( externalFlow );
    }
}
