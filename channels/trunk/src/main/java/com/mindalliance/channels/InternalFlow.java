package com.mindalliance.channels;

import org.hibernate.annotations.Entity;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * A flow between nodes within one scenario.
 */
@Entity
public class InternalFlow extends Flow {

    /** The source of the flow. */
    private Node source;

    /** The target of the flow. */
    private Node target;

    public InternalFlow() {
    }

    public InternalFlow( Node source, Node target, String name ) {
        setName( name );
        this.source = source;
        this.target = target;
    }

    @Override
    @ManyToOne
    public Node getSource() {
        return source;
    }

    @Override
    @ManyToOne
    public Node getTarget() {
        return target;
    }

    @Override
    public void setSource( Node source ) {
        this.source = source;
    }

    @Override
    public void setTarget( Node target ) {
        this.target = target;
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        Node s = source;
        s.removeOutcome( this );
        if ( s.isConnector() )
            s.getScenario().removeNode( s );
        source = null;

        Node t = target;
        t.removeRequirement( this );
        if ( t.isConnector() ) {
            t.getScenario().removeNode( t );
        }
        target = null;
    }

    @Override @Transient
    public boolean isInternal() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void initFrom( Flow flow ) {
        setName( flow.getName() );
        setDescription( flow.getDescription() );
        setMaxDelay( flow.getMaxDelay() );
        setAskedFor( flow.isAskedFor() );
        setChannels( flow.getChannels() );
        setCritical( flow.isCritical() );
    }
}
