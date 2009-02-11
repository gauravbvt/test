package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.CascadeType;
import java.util.Iterator;
import java.util.List;

/**
 * A flow from one Part in this scenario to/from a connector in another scenario.
 * Direction of flow matches other connector's only flow.
 */
@Entity
public class ExternalFlow extends Flow {

    /** The connector. */
    private Connector connector;

    /** True if an input flow, ie an output from a scenario. */
    private boolean input;

    /** The part. */
    private Part part;

    public ExternalFlow() {
    }

    public ExternalFlow( Node source, Node target, String name ) {
        setName( name );
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
    @Override @Transient
    public Node getSource() {
        if ( connector == null )
            return null;
        else {
            Iterator<Flow> iterator = connector.requirements();
            return iterator.hasNext() ? iterator.next().getSource() : part;
        }
    }

    /**
     * Return the target of this flow. Depends on the connector.
     * @return the target of the first outcome of the connector, or the part if none
     *
     * @see Flow#getTarget()
     */
    @Override @Transient
    public Node getTarget() {
        if ( connector == null )
            return null;
        else {
            Iterator<Flow> iterator = connector.outcomes();
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
        Part p = part;
        p.removeOutcome( this );
        p.removeRequirement( this );
        part = null;

        Connector c = connector;
        c.removeExternalFlow( this );
        connector = null;
    }

    /** {@inheritDoc} */
    @Override @Transient
    public boolean isInternal() {
        return false;
    }

    @ManyToOne( cascade = CascadeType.ALL )
    public Part getPart() {
        return part;
    }

    public void setPart( Part part ) {
        this.part = part;
    }

    @ManyToOne( cascade = CascadeType.ALL )
    public Connector getConnector() {
        return connector;
    }

    public void setConnector( Connector connector ) {
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

    @Transient
    private Flow getConnectorFlow() {
        return isInput() ? getConnector().requirements().next()
                         : getConnector().outcomes().next();
    }

    /**
     * @return the name of the flow
     */
    @Override @Transient
    public String getName() {
        return getConnector() == null ? super.getName() : getConnectorFlow().getName();
    }

    public boolean isInput() {
        return input;
    }

    public void setInput( boolean input ) {
        this.input = input;
    }

    @Override @Transient
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

    @Override @Transient
    public List<Channel> getEffectiveChannels() {
        return isConnectorBased() ? getConnectorFlow().getChannels() : super.getChannels();
    }

    /** {@inheritDoc} */
    @Override
    public void setEffectiveChannels( List<Channel> channels ) {
        if ( isConnectorBased() )
            getConnectorFlow().setChannels( channels );
        else
            super.setChannels( channels );
    }

    @Transient
    private boolean isConnectorBased() {
        Connector c = getConnector();
        return c != null
            && ( c.isInput() || getConnectorFlow().isAskedFor() );
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConnectedTo( boolean outcome, Node node ) {
        return super.isConnectedTo( outcome, node ) || node.equals( getConnector() );
    }

    @Override
    public void setCritical( boolean critical ) {
        Connector c = getConnector();
        boolean connectorBased = c != null
                              && c.isInput()
                              && !getConnectorFlow().isAskedFor();
        if ( connectorBased )
            getConnectorFlow().setCritical( critical );
        else
            super.setCritical( critical );
    }

    /** {@inheritDoc} */
    @Override  @Transient
    public boolean isCritical() {
        Connector c = getConnector();
        boolean connectorBased = c != null
                                       && c.isInput()
                                       && !getConnectorFlow().isAskedFor();
        return connectorBased ? getConnectorFlow().isCritical() : super.isCritical();
    }

    /** {@inheritDoc} */
    @Override
    public void initFrom( Flow flow ) {
    }
}
