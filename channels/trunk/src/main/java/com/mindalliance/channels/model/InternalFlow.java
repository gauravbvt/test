package com.mindalliance.channels.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.List;

/**
 * A flow between nodes within one scenario.
 */
@Entity
public class InternalFlow extends Flow {

    /**
     * The source of the flow.
     */
    private Node source;

    /**
     * The target of the flow.
     */
    private Node target;

    public InternalFlow() {
    }

    public InternalFlow( Node source, Node target, String name ) {
        setName( name );
        this.source = source;
        this.target = target;
    }

    @Override
    @ManyToOne( cascade = CascadeType.ALL )
    public Node getSource() {
        return source;
    }

    @Override
    @ManyToOne( cascade = CascadeType.ALL )
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

    @Override
    @Transient
    public List<Channel> getEffectiveChannels() {
        return getChannels();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO - SHOULD NEVER BE CALLED - always setChannels directly where it is allowed
    public void setEffectiveChannels( List<Channel> channels ) {
        assert canSetChannels();
        setChannels( channels );
    }

    /**
     * {@inheritDoc}
     */
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

    @Override
    @Transient
    public boolean isInternal() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initFrom( Flow flow ) {
        setName( flow.getName() );
        setDescription( flow.getDescription() );
        if ( !hasConnector() ) setMaxDelay( flow.getMaxDelay() );
        setAskedFor( flow.isAskedFor() );
        setChannels( flow.getChannels() );
        if ( !source.isConnector() ) setSignificanceToSource( flow.getSignificanceToSource() );
        if ( !target.isConnector() ) setSignificanceToTarget( flow.getSignificanceToTarget() );
        setAll( flow.isAll() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasConnector() {
        return source.isConnector() || target.isConnector();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetNameAndElements() {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetMaxDelay() {
        return canGetMaxDelay();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetChannels() {
        return !( target.isConnector() && isNotification() )
                && !( source.isConnector() && isAskedFor() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetAskedFor() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canGetMaxDelay() {
        // return !( source.isConnector() || target.isConnector() );
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canGetChannels() {
        return !( target.isConnector() && isNotification() )
                && !( source.isConnector() && isAskedFor() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetAll() {
        return isNotification() && getSource().isPart() && getTarget().isPart()
               && ( (Part) getTarget() ).hasNonActorResource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canGetAll() {
        return canSetAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canGetSignificanceToTarget() {
        return !target.isConnector();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetSignificanceToTarget() {
        return !target.isConnector();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canGetSignificanceToSource() {
        return !source.isConnector();
    }

    /**
     * Flow could trigger the part
     *
     * @return a boolean
     */
    @Override
    public boolean canGetTriggersSource() {
        return !source.isConnector() && isAskedFor();
    }

    /**
     * Flow could terminate the part
     *
     * @return a boolean
     */
    @Override
    public boolean canGetTerminatesSource() {
        return !source.isConnector();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetTriggersSource() {
        return source.isPart() && isAskedFor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetTerminatesSource() {
        return !source.isConnector();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public Scenario getScenario() {
        return source.getScenario();
    }
}
