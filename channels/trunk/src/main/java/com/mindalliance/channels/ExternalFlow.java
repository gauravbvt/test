package com.mindalliance.channels;

import java.util.Iterator;

/**
 * A flow from one Part in this scenario to/from a connector in another scenario.
 * Direction of flow matches other connector's only flow.
 */
public class ExternalFlow extends Flow {

    /** The connector. */
    private Connector connector;

    /** True if an input flow, ie an output from a scenario. */
    private boolean input;

    /** The part. */
    private Part part;

    public ExternalFlow() {
    }

    public ExternalFlow( Node source, Node target ) {
        if ( source.isConnector() && target.isPart() ) {
            setConnector( (Connector) source );
            setPart( (Part) target );
            setInput( true );
        } else if ( target.isConnector() && source.isPart() ) {
            setConnector( (Connector) target );
            setPart( (Part) source );
        } else
            throw new IllegalArgumentException();

        if ( getPart().getScenario().equals( getConnector().getScenario() ) ) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Return the source of this flow. Depends on the connector.
     * @return the source of the first requirement of the connector, or the part if none
     *
     * @see Flow#getSource()
     */
    @Override
    public Node getSource() {
        if ( connector == null )
            return null;
        else {
            final Iterator<Flow> iterator = connector.requirements();
            return iterator.hasNext() ? iterator.next().getSource() : part;
        }
    }

    /**
     * Return the target of this flow. Depends on the connector.
     * @return the target of the first outcome of the connector, or the part if none
     *
     * @see Flow#getTarget()
     */
    @Override
    public Node getTarget() {
        if ( connector == null )
            return null;
        else {
            final Iterator<Flow> iterator = connector.outcomes();
            return iterator.hasNext() ? iterator.next().getTarget() : part ;
        }
    }

    /** {@inheritDoc} */
    @Override
    void setSource( Node source ) {
        setPart( (Part) source );
    }

    /** {@inheritDoc} */
    @Override
    void setTarget( Node target ) {
        setPart( (Part) target );
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        final Part p = part;
        p.removeOutcome( this );
        p.removeRequirement( this );
        part = null;

        final Connector c = connector;
        c.removeExternalFlow( this );
        connector = null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInternal() {
        return false;
    }

    public final Part getPart() {
        return part;
    }

    public final void setPart( Part part ) {
        this.part = part;
    }

    public final Connector getConnector() {
        return connector;
    }

    public final void setConnector( Connector connector ) {
        this.connector = connector;
    }

    /** {@inheritDoc} */
    @Override
    public void setName( String name ) {
        if ( getConnector() == null )
            super.setName( name );
        else
            getConnectorFlow().setName( name );
    }

    private Flow getConnectorFlow() {
        return isInput() ? getConnector().requirements().next()
                         : getConnector().outcomes().next();
    }

    /**
     * @return the name of the flow
     */
    @Override
    public String getName() {
        return getConnector() == null ? super.getName() : getConnectorFlow().getName();
    }

    public boolean isInput() {
        return input;
    }

    public final void setInput( boolean input ) {
        this.input = input;
    }

    @Override
    public boolean isAskedFor() {
        return getConnector() == null ? super.isAskedFor() : getConnectorFlow().isAskedFor();
    }

    /**
     * Delegate to connector flow.
     * @param askedFor the new value
     * @see Flow#setAskedFor(boolean)
     */
    @Override
    public void setAskedFor( boolean askedFor ) {
        if ( getConnector() == null )
            super.setAskedFor( askedFor );
        else
            getConnectorFlow().setAskedFor( askedFor );
    }

    @Override
    public String getChannel() {
        final Connector c = getConnector();
        final boolean connectorBased = c != null
                                       && ( c.isInput()
                                            || getConnectorFlow().isAskedFor() );
        return connectorBased ? getConnectorFlow().getChannel() : super.getChannel();
    }

    @Override
    public void setChannel( String channel ) {
        final Connector c = getConnector();
        final boolean connectorBased = c != null
                                       && ( c.isInput()
                                            || getConnectorFlow().isAskedFor() );
        if ( connectorBased )
            getConnectorFlow().setChannel( channel );
        else
            super.setChannel( channel );
    }

    @Override
    public void setCritical( boolean critical ) {
        final Connector c = getConnector();
        final boolean connectorBased = c != null
                                       && c.isInput()
                                       && !getConnectorFlow().isAskedFor();
        if ( connectorBased )
            getConnectorFlow().setCritical( critical );
        else
            super.setCritical( critical );
    }

    @Override
    public boolean isCritical() {
        final Connector c = getConnector();
        final boolean connectorBased = c != null
                                       && c.isInput()
                                       && !getConnectorFlow().isAskedFor();
        return connectorBased ? getConnectorFlow().isCritical() : super.isCritical();
    }
}
