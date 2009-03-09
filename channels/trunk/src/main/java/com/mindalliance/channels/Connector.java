package com.mindalliance.channels;

import org.hibernate.annotations.Proxy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A connector to unspecified node(s) outside of the scenario.
 */
@Entity @Proxy
public class Connector extends Node {

    /** The connections from external scenarios. */
    private Set<ExternalFlow> externalFlows;

    public Connector() {
        setExternalFlows( new HashSet<ExternalFlow>() );
    }

    @Override @Transient
    public boolean isConnector() {
        return true;
    }

    /** {@inheritDoc} */
    @Override @Transient
    public String getTitle() {
        boolean isInput = isSource();
        if ( hasInnerFlow() ) {
            Flow inner = getInnerFlow();
            Part part  = (Part) ( isInput ? inner.getTarget() :  inner.getSource() );
            return MessageFormat.format( isInput ? "{0} to {1} (in {2})" : "{0} from {1} (in {2})",
                                         inner.getName(),
                                         part.getName(),
                                         part.getScenario() );
        } else
            return "(Not connected)";
    }

    /**
     * Is the connector a source (true) or target (false)?
     * @return -- whether a source
     */
    @Transient
    public boolean isSource() {
        return !getOutcomes().isEmpty();
    }

    /**
     * Is the connector a target (true) or source (false)?
     * @return -- whether a target
     */
    @Transient
    public boolean isTarget() {
        return !getRequirements().isEmpty();
    }

    /**
     * Gets the inner flow between part and connector
     * @return the connector's inner flow or null if none
     */
    @Transient
    public Flow getInnerFlow() {
        return isSource() ? getOutcomes().values().iterator().next()
                          : getRequirements().values().iterator().next();
    }

    /** @return true if connector has an inner flow. */
    public boolean hasInnerFlow() {
        return !getOutcomes().isEmpty() || !getRequirements().isEmpty();
    }

    /**
     * Are there external flows to or from this connector?
     * @return -- boolean
     */
    @Transient
    public boolean isConnected() {
        return !getExternalFlows().isEmpty();
    }

    @OneToMany( cascade = CascadeType.ALL )
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

    /**
     * Remove all external connectors.
     */
    public void disconnect() {
        Collection<ExternalFlow> flows = new HashSet<ExternalFlow>( getExternalFlows() );
        for ( ExternalFlow flow : flows )
            flow.disconnect();
    }
}
