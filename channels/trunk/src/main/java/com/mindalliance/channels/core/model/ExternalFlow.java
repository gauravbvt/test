package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.AssetConnections;
import com.mindalliance.channels.core.model.time.Cycle;
import com.mindalliance.channels.core.model.time.Delay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A flow from one Part in this segment to/from a connector in another segment.
 * Direction of flow matches other connector's only flow.
 */
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

        if ( getPart().getSegment().equals( getConnector().getSegment() ) ) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Return the source of this flow. Depends on the connector.
     *
     * @return the source of the first receive of the connector, or the part if none
     * @see Flow#getSource()
     */
    @Override
    public Node getSource() {
        return connector == null ? null
                : connector.isSource() ? part
                : getConnectorFlow().getSource();
    }

    /**
     * Return the target of this flow. Depends on the connector.
     *
     * @return the target of the first send of the connector, or the part if none
     * @see Flow#getTarget()
     */
    @Override
    public Node getTarget() {
        return connector == null ? null
                : connector.isTarget() ? part
                : getConnectorFlow().getTarget();
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
    public boolean isInternal() {
        return false;
    }

    public Part getPart() {
        return part;
    }

    public void setPart( Part part ) {
        this.part = part;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector( Connector connector ) {
        this.connector = connector;
    }

    private Flow getConnectorFlow() {
        Connector conn = getConnector();
        return conn == null || !conn.hasInnerFlow() ? null : conn.getInnerFlow();
    }

    /**
     * @return the name of the flow
     */
    @Override
    public String getName() {
        Flow connectorFlow = getConnectorFlow();
        return connectorFlow == null ? super.getName() : connectorFlow.getName();
    }

    /**
     * Is a part the ultimate target in this external flow?
     *
     * @return a boolean
     */
    public boolean isPartTargeted() {
        // if connector is in a capability, i.e. itself the target of an internal flow
        return connector.isTarget();
    }

    /**
     * Get a string description of the kind of communication
     * including max delay if applicable
     *
     * @return a string description of the communication
     */
    public String getKind() {
        if ( !isPartTargeted() ) {
            return isAskedFor() ? "answer" : "notify";
        } else {
            return isAskedFor() ? "ask" : "receive";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPart( Part part ) {
        return this.part.equals( part );
    }


    /**
     * Get external part.
     *
     * @return a part
     */
    public Part getExternalPart() {
        return (Part) ( isPartTargeted() ? getSource() : getTarget() );
    }

    @Override
    public boolean isAskedFor() {
        Flow flow = getConnectorFlow();
        return flow == null ? super.isAskedFor() : flow.isAskedFor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName( String name ) {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            super.setName( name );
        else
            flow.setName( name );
    }

    public List<Tag> getVisibleTags() {
        Flow flow = getConnectorFlow();
        if ( flow == null ) {
            return super.getVisibleTags();
        } else {
            Set<Tag> allVisibleTags = new HashSet<Tag>( flow.getVisibleTags() );
            allVisibleTags.addAll( super.getVisibleTags() );
            return new ArrayList<Tag>( allVisibleTags );
        }
    }

    @Override
    public Intent getIntent() {
        Flow flow = getConnectorFlow();
        return flow == null ? super.getIntent() : flow.getIntent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription( String description ) {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            super.setDescription( description );
        else
            flow.setDescription( description );
    }

    /**
     * {@inheritDoc
     */
    @Override
    public String getDescription() {
        Flow flow = getConnectorFlow();
        return flow == null ? super.getDescription() : flow.getDescription();
    }

    public List<ElementOfInformation> getEois() {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            return super.getEois();
        else
            return flow.getEois();
    }

    public List<ElementOfInformation> getEffectiveEois() {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            return super.getEffectiveEois();
        else
            return flow.getEffectiveEois();
    }


    public List<ElementOfInformation> getLocalEois() {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            return super.getLocalEois();
        else
            return flow.getLocalEois();
    }


    public void setEois( List<ElementOfInformation> elements ) {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            super.setEois( elements );
        else
            flow.setEois( elements );
    }

    public void setLocalEois( List<ElementOfInformation> elements ) {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            super.setLocalEois( elements );
        else
            flow.setLocalEois( elements );
    }


    /**
     * Delegate to connector flow.
     *
     * @param askedFor the new value
     * @see Flow#setAskedFor(boolean)
     */
    @Override
    public void setAskedFor( boolean askedFor ) {
        if ( getConnectorFlow() == null )
            super.setAskedFor( askedFor );
        else
            getConnectorFlow().setAskedFor( askedFor );
    }

    public void setAssetConnections( AssetConnections assetConnections ) {
        if ( getConnectorFlow() == null ) {
            super.setAssetConnections( assetConnections );
        } else {
            getConnectorFlow().setAssetConnections( assetConnections );
        }
    }

    public AssetConnections getAssetConnections() {
        Flow flow = getConnectorFlow();
        if ( flow == null )
            return super.getAssetConnections();
        else
            return flow.getAssetConnections();
    }

    public void addAssetConnection( AssetConnection assetConnection ) {
        if ( getConnectorFlow() == null ) {
            super.addAssetConnection( assetConnection );
        } else {
            getConnectorFlow().addAssetConnection( assetConnection );
        }

    }

    @Override
    public List<Channel> getEffectiveChannels() {
        return channelsAreInConnectorFlow() ?
                getConnectorFlow().getChannels() : super.getChannels();
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

    private boolean channelsAreInConnectorFlow() {
        Connector c = getConnector();
        return c != null &&
                ( c.isSource() && getConnectorFlow().isNotification()
                        || c.isTarget() && getConnectorFlow().isAskedFor() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnectedTo( boolean isSend, Node node ) {
        return super.isConnectedTo( isSend, node ) || node.equals( getConnector() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Significance getSignificanceToSource() {
        Flow flow = getConnectorFlow();
        return flow == null
                ? Significance.None
                : isPartTargeted()
                ? flow.getSignificanceToSource()
                : super.getSignificanceToSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Significance getSignificanceToTarget() {
        Flow flow = getConnectorFlow();
        return flow == null
                ? Flow.Significance.None
                : isPartTargeted()
                ? super.getSignificanceToTarget()
                : flow.getSignificanceToTarget();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCritical() {
        return getSignificanceToTarget() == Flow.Significance.Critical;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRequired() {
        Flow flow = getConnectorFlow();
        return flow != null && ( isPartTargeted() ? super.isRequired() : flow.isRequired() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canSetNameAndElements() {
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
        return true;
        /*return !channelsAreInConnectorFlow();*/
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
        return isNotification()
                && !isPartTargeted()
                && ( (Part) getTarget() ).hasNonActualActorResource();
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
    public boolean hasConnector() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initFrom( Flow flow ) {
        if ( !hasConnector() ) {
            setMaxDelay( new Delay( flow.getMaxDelay() ) );
            setRepeatsEvery( flow.getRepeatsEvery() == null ? null : new Cycle( flow.getRepeatsEvery() ) );
        }
        setChannels( flow.copyChannels() );
        if ( !isPartTargeted() ) setSignificanceToSource( flow.getSignificanceToSource() );
        if ( isPartTargeted() ) setSignificanceToTarget( flow.getSignificanceToTarget() );
        setPublished( flow.isPublished() );
        setAssetConnections( flow.getAssetConnections().copy() );
    }

    /**
     * {@inheritDoc}
     */
    public Segment getSegment() {
        return part.getSegment();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUndefined() {
        return super.isUndefined() && part.isUndefined();
    }


}
