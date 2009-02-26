package com.mindalliance.channels;

import com.mindalliance.channels.pages.Project;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.CascadeType;
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

    /**
     * {@inheritDoc}
     */
    public void breakup() {
        if ( !source.isConnector() && !target.isConnector() ) {
            Service service = Project.service();
            if ( !source.hasMultipleOutcomes( getName() ) ) {
                Flow flow = service.connect( source, service.createConnector( source.getScenario() ), getName() );
                flow.initFrom( this );
            }
            if ( !target.hasMultipleRequirements( getName() ) ) {
                Flow flow = service.connect( service.createConnector( target.getScenario() ), target, getName() );
                flow.initFrom( this );
            }
        }
        disconnect();
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
    }

    private boolean hasConnector() {
        return source.isConnector() || target.isConnector();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetNameAndDescription() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetMaxDelay() {
        return !( source.isConnector() || target.isConnector() );
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
    public boolean canSetAskedFor() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetMaxDelay() {
        return !( source.isConnector() || target.isConnector() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetChannels() {
        return !( target.isConnector() && isNotification() )
                && !( source.isConnector() && isAskedFor() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetAll() {
        return isNotification() && getSource().isPart() && getTarget().isPart() && ( (Part) getTarget() ).isOnlyRole();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetAll() {
        return canSetAll();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetSignificanceToTarget() {
        return !target.isConnector();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetSignificanceToTarget() {
        return !target.isConnector();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetSignificanceToSource() {
        return !source.isConnector();
    }

    /**
     * Flow could trigger the part
     *
     * @return a boolean
     */
    public boolean canGetTriggersSource() {
        return !source.isConnector() && isAskedFor();
    }

    /**
     * Flow could terminate the part
     *
     * @return a boolean
     */
    public boolean canGetTerminatesSource() {
        return !source.isConnector();
    }


    /**
     * {@inheritDoc}
     */
    public boolean canSetTriggersSource() {
        return source.isPart() && isAskedFor();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetTerminatesSource() {
        return !source.isConnector();
    }
}
