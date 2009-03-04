package com.mindalliance.channels;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.CascadeType;
import java.util.List;

/**
 * A flow from one Part in this scenario to/from a connector in another scenario.
 * Direction of flow matches other connector's only flow.
 */
@Entity
public class ExternalFlow extends Flow {

    /**
     * The connector.
     */
    private Connector connector;

    /**
     * The part.
     */
    private Part part;

    public ExternalFlow() {
    }

    public ExternalFlow( Node source, Node target, String name ) {
        // Ignore name since it takes the name of the internal flow involving the connector
        if ( source.isConnector() && target.isPart() ) {
            setConnector( (Connector) source );
            setPart( (Part) target );
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
     *
     * @return the source of the first requirement of the connector, or the part if none
     * @see Flow#getSource()
     */
    @Override
    @Transient
    public Node getSource() {
        if ( connector == null )  // TODO -- How can the connector ever be null?
            return null;
        else {
/*
            Iterator<Flow> iterator = connector.requirements();
            return iterator.hasNext() ? iterator.next().getSource() : part;
*/
            if ( connector.isSource() ) {
                return part;
            } else {
                return getConnectorFlow().getSource();
            }
        }
    }

    /**
     * Return the target of this flow. Depends on the connector.
     *
     * @return the target of the first outcome of the connector, or the part if none
     * @see Flow#getTarget()
     */
    @Override
    @Transient
    public Node getTarget() {
        if ( connector == null )
            return null;
        else {
/*
            Iterator<Flow> iterator = connector.outcomes();
            return iterator.hasNext() ? iterator.next().getTarget() : part;
*/
            if ( connector.isTarget() ) {
                return part;
            } else {
                return getConnectorFlow().getTarget();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setSource( Node source ) {
        setPart( (Part) source );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setTarget( Node target ) {
        setPart( (Part) target );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
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

    @Transient
    private Flow getConnectorFlow() {
        return getConnector().getInnerFlow();
    }

    /**
     * @return the name of the flow
     */
    @Override
    @Transient
    public String getName() {
        return getConnector() == null ? super.getName() : getConnectorFlow().getName();
    }

    /**
     * Is the part the target in this flow?
     *
     * @return a boolean
     */
    @Transient
    public boolean isPartTargeted() {
        return connector.isTarget();
    }

    @Override
    @Transient
    public boolean isAskedFor() {
        return getConnector() == null ? super.isAskedFor() : getConnectorFlow().isAskedFor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName( String name ) {
        if ( getConnector() == null )
            super.setName( name );
        else
            getConnectorFlow().setName( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription( String description ) {
        if ( getConnector() == null )
            super.setDescription( description );
        else
            getConnectorFlow().setDescription( description );
    }

    /**
      * @return the description of the flow
      */
     @Override
     @Transient
     public String getDescription() {
         return getConnector() == null ? super.getDescription() : getConnectorFlow().getDescription();
     }

    /**
     * Delegate to connector flow.
     *
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
    @Transient
    public List<Channel> getEffectiveChannels() {
        return channelsAreInConnectorFlow() ? getConnectorFlow().getChannels() : super.getChannels();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEffectiveChannels( List<Channel> channels ) {
        if ( channelsAreInConnectorFlow() )
            getConnectorFlow().setChannels( channels );
        else
            super.setChannels( channels );
    }

    @Transient
    private boolean channelsAreInConnectorFlow() {
        Connector c = getConnector();
        return c != null &&
                (
                        ( c.isSource() && getConnectorFlow().isNotification() )
                ||
                        ( c.isTarget() && getConnectorFlow().isAskedFor() )
                );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnectedTo( boolean outcome, Node node ) {
        return super.isConnectedTo( outcome, node ) || node.equals( getConnector() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Significance getSignificanceToSource() {
        Connector c = getConnector();
        if (c == null) return Flow.Significance.None;
        if (isPartTargeted()) {
            return getConnectorFlow().getSignificanceToSource();
        }
        else {
            return super.getSignificanceToSource();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Significance getSignificanceToTarget() {
        Connector c = getConnector();
        if (c == null) return Flow.Significance.None;
        if (isPartTargeted()) {
            return super.getSignificanceToTarget();
        }
        else {
            return getConnectorFlow().getSignificanceToTarget();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isCritical() {
        return getSignificanceToTarget() == Flow.Significance.Critical;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isRequired() {
        Connector c = getConnector();
        if (c == null) return false;
        if (isPartTargeted()) {
            return super.isRequired();
        }
        else {
            return getConnectorFlow().isRequired();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetNameAndDescription() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetMaxDelay() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetMaxDelay() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
     public boolean canSetChannels() {
        return !channelsAreInConnectorFlow();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetAskedFor() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetChannels() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetAll() {
        // The role-based part in the connector flow is targeted by a notification
        boolean canSetAll = isNotification()
                && !isPartTargeted()
                && ( (Part) getTarget() ).isOnlyRole();
        return canSetAll;
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
        return isPartTargeted() || getConnectorFlow().canGetSignificanceToTarget();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetSignificanceToTarget() {
        return isPartTargeted();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetSignificanceToSource() {
        return !isPartTargeted() || getConnectorFlow().canGetSignificanceToSource();
    }

    /**
     * {@inheritDoc}
     */
     public boolean canSetTriggersSource() {
        return !isPartTargeted() && isAskedFor();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetTerminatesSource() {
        return !isPartTargeted();
    }

    /**
     * {@inheritDoc}
     */
    public boolean canGetTriggersSource() {
        return canGetSignificanceToSource() && isAskedFor();
    }

    /**
     * {@inheritDoc}
     */
     public boolean canGetTerminatesSource() {
        return canGetSignificanceToSource();
    }

    /**
     * {@inheritDoc}
     */
    public void breakup() {
        disconnect();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasConnector() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initFrom( Flow flow ) {
        if ( !hasConnector() ) setMaxDelay( flow.getMaxDelay() );
        setChannels( flow.getChannels() );
        if ( !isPartTargeted() ) setSignificanceToSource( flow.getSignificanceToSource() );
        if ( isPartTargeted() ) setSignificanceToTarget( flow.getSignificanceToTarget() );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario getScenario() {
        return part.getScenario();
    }
}
