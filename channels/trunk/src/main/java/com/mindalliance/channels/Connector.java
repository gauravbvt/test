package com.mindalliance.channels;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
        return "Connector";
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
