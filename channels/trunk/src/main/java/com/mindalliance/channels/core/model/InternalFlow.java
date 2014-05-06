package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.time.Cycle;
import com.mindalliance.channels.core.model.time.Delay;

import java.util.ArrayList;
import java.util.List;

/**
 * A flow between nodes within one segment.
 */
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

    @Override
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

    @Override
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
        if ( !hasConnector() )  {
            setMaxDelay( new Delay( flow.getMaxDelay() ) );
            setRepeatsEvery( flow.getRepeatsEvery() == null ? null : new Cycle( flow.getRepeatsEvery() ) );
        }
        setPublished( flow.isPublished() );
        setAskedFor( flow.isAskedFor() );
        setChannels( flow.copyChannels() );
        if ( !source.isConnector() ) setSignificanceToSource( flow.getSignificanceToSource() );
        if ( !target.isConnector() ) setSignificanceToTarget( flow.getSignificanceToTarget() );
        setAll( flow.isAll() );
        setIntent( flow.getIntent() );
        setRestrictions( new ArrayList<Restriction>( flow.getRestrictions() ) );
        setEois( flow.copyEois() );
        setAssetConnections( flow.getAssetConnections().copy() );
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
    public boolean hasPart( Part part ) {
        return
                source.isPart() && source.equals( part )
                || target.isPart() && target.equals( part );
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
        return true;
        /*return !( target.isConnector() && isNotification() )
                && !( source.isConnector() && isAskedFor() );*/
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
        return true;
        /*return !( target.isConnector() && isNotification() )
                && !( source.isConnector() && isAskedFor() );*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSetAll() {
        return isNotification() && getSource().isPart() && getTarget().isPart()
               && ( (Part) getTarget() ).hasNonActualActorResource();
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
    public Segment getSegment() {
        return source == null
                ? null // when detached
                : source.getSegment();
    }
}
