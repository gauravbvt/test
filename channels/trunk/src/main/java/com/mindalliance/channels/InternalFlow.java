package com.mindalliance.channels;

/**
 * A flow between nodes within one scenario.
 */
public class InternalFlow extends Flow {

    /** The source of the flow. */
    private Node source;
    /** The target of the flow. */
    private Node target;

    public InternalFlow() {
    }

    public InternalFlow( Node source, Node target ) {
        this.source = source;
        this.target = target;
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
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
        final Node s = source;
        s.removeOutcome( this );
        if ( s.isConnector() )
            s.getScenario().removeNode( s );
        source = null;
        
        final Node t = target;
        t.removeRequirement( this );
        if ( t.isConnector() ) {
            t.getScenario().removeNode( t );
        }
        target = null;
    }

    @Override
    public boolean isInternal() {
        return true;
    }
}
