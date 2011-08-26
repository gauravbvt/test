package com.mindalliance.channels.core.model;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A connector to unspecified node(s) outside of the segment.
 */
public class Connector extends Node {

    /** The connections from external segments. */
    private Set<ExternalFlow> externalFlows;

    public Connector() {
        setExternalFlows( new HashSet<ExternalFlow>() );
    }

    public boolean isConnector() {
        return true;
    }

    /** {@inheritDoc} */
    public String displayString() {
        return displayString( Integer.MAX_VALUE );
    }

    /** {@inheritDoc} */
    public String displayString( int maxItemLength ) {
        boolean isInput = isSource();
        if ( hasInnerFlow() ) {
            Flow inner = getInnerFlow();
            Part part  = (Part) ( isInput ? inner.getTarget() :  inner.getSource() );
            return MessageFormat.format( "{0} - {1}",
                                         part.displayString( maxItemLength ),
                                         part.getSegment().displayString( maxItemLength ) );
        } else
            return "(Not connected)";
    }

    /** {@inheritDoc} */
    public String fullDisplayString( int maxItemLength ) {
        boolean isInput = isSource();
        if ( hasInnerFlow() ) {
            Flow inner = getInnerFlow();
            Part part  = (Part) ( isInput ? inner.getTarget() :  inner.getSource() );
            return MessageFormat.format( "\"{0}\" - {1} - {2}",
                                         StringUtils.abbreviate( inner.getName(), maxItemLength ),
                                         part.displayString( maxItemLength ),
                                         part.getSegment().displayString( maxItemLength ) );
        } else
            return "(Not connected)";
    }

    /** {@inheritDoc} */
    public String getTitle() {
        boolean isInput = isSource();
        if ( hasInnerFlow() ) {
            Flow inner = getInnerFlow();
            Part part  = (Part) ( isInput ? inner.getTarget() :  inner.getSource() );
            return MessageFormat.format( isInput ? "{0} to {1} in {2}" : "{0} from {1} in {2}",
                                         inner.getName(),
                                         part.displayString(),
                                         part.getSegment().getPhaseEventTitle() );
        } else
            return "(Not connected)";
    }

    /**
     * Is the connector a source (true) or target (false)?
     * @return -- whether a source
     */
    public boolean isSource() {
        return !getSends().isEmpty();
    }

    /**
     * Is the connector a target (true) or source (false)?
     * @return -- whether a target
     */
    public boolean isTarget() {
        return !getReceives().isEmpty();
    }

    /**
     * Gets the inner flow between part and connector
     * @return the connector's inner flow or null if none
     */
    public Flow getInnerFlow() {
        return isSource() ? getSends().values().iterator().next()
                          : getReceives().values().iterator().next();
    }

    /** @return true if connector has an inner flow. */
    public boolean hasInnerFlow() {
        return !getSends().isEmpty() || !getReceives().isEmpty();
    }

    /**
     * Are there external flows to or from this connector?
     * @return -- boolean
     */
    public boolean isConnected() {
        return !getExternalFlows().isEmpty();
    }

    public Set<ExternalFlow> getExternalFlows() {
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
