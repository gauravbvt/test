package com.mindalliance.channels;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * An arrow between two nodes in the information flow graph.
 */
@Entity
public abstract class Flow extends ModelObject implements Channelable {

    // TODO Should we annotate a flow as primary vs seconday
    // (when multiple flows are mutually redundant)?

    /** A list of alternate communication channels for the flow. */
    private List<Channel> channels = new ArrayList<Channel>();

    /** If this flow is critical to either source or target. */
    private boolean critical;

    /** If this flow only happens on request from either the source or target. */
    private boolean askedFor;

    /** How much lag time is expected for this flow. */
    private Delay maxDelay = new Delay();

    /** If flow applies to all potential sources/targets. */
    private boolean all;

    protected Flow() {
    }

    public boolean isAskedFor() {
        return askedFor;
    }

    /**
     * Set if this flow is notified or asked for.
     * @param askedFor true is information is asked for
     */
    public void setAskedFor( boolean askedFor ) {
        this.askedFor = askedFor;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    /**
     * Add an alternate channel for the flow
     * @param channel a Channel
     */
    public void addChannel( Channel channel ) {
        addChannelIfUnique( channel );
    }

    /**
     * Add an alternate channel for the flow
     * @param medium a communication medium
     * @param address an address on the medium
     */
    public void addChannel( Medium medium, String address ) {
        addChannelIfUnique( new Channel( medium, address ) );
    }

    private void addChannelIfUnique( Channel channel ) {
        boolean found = false;
        for ( Channel c : channels )
            if ( c.sameAs( channel ) ) found = true;
        if ( !found ) channels.add( channel );
    }

    /**
     * Remove a channel from the list of alternate channels
     * @param channel a Channel
     */
    public void removeChannel( Channel channel ) {
        channels.remove( channel );
    }

    /** {@inheritDoc */
    @Transient
    public String getChannelsString() {
        return Channel.toString( channels );
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical( boolean critical ) {
        this.critical = critical;
    }

    public Delay getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay( Delay maxDelay ) {
        this.maxDelay = maxDelay;
    }

    /**
     * Set max delay from string
     * @param s a string parseable to a Delay
     */
    public void setMaxDelay( String s ) {
        maxDelay = Delay.parse( s );
    }

    /**
     * Provide a out-of-context description of the flow.
     * @return the description
     */
    @Transient
    public String getTitle() {
        String content = getName();
        if ( content == null || content.trim().isEmpty() )
            content = "something";
        return MessageFormat.format( isAskedFor() ? "{2} ask {1} about {0}"
                                                  : "{1} notify {2} of {0}",

            content, getShortName( getSource() ), getShortName( getTarget() ) );
    }

    private String getShortName( Node node ) {
        String result = "somebody";

        if ( node != null ) {
            String sourceName = node.getName();
            if ( sourceName != null && !sourceName.trim().isEmpty() ) {
                result = sourceName;
                if ( node.isPart() && ( (Part) node ).isOnlyRole() )
                    result = MessageFormat.format( isAll() ? "every {0}" : "any {0}", result );
            }
        }

        return result;
    }

    /**
     * Provide a description of the flow, when viewed as a requirement.
     * @return the description
     */
    @Transient
    public String getRequirementTitle() {
        boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                 noName       ? isAskedFor() ? "Ask {1} for something"
                                             : "Notified of something by {1}"
               : isAskedFor() ? "Ask {1} for {0}"
                              : "Notified of {0} by {1}",
                getName(), getShortName( getSource() ) );
    }

    /**
     * Provide a description of the flow, when viewed as an outcome.
     * @return the description
     */
    @Transient
    public String getOutcomeTitle() {
        boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                noName       ? isAskedFor() ? "Answer {1} with something"
                                            : "Notify {1} of something"
              : isAskedFor() ? "Answer {1} with {0}"
                             : "Notify {1} of {0}",
                getName(), getShortName( getTarget() ) );
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getTitle();
    }

    /** @return Get the source of this flow. */
    @Transient
    public abstract Node getSource();

    /** @return the target of this flow. */
    @Transient
    public abstract Node getTarget();

    /**
     * Set the source of this flow.
     * Note: this method should not be called directly.
     * @see Service#connect( Node, Node, String )
     * @param source the source node.
     */
    abstract void setSource( Node source );

    /**
     * Set the target of this flow.
     * Note: this method should not be called directly.
     * @see Service#connect( Node, Node, String )
     * @param target the target node.
     */
    abstract void setTarget( Node target );

    /**
     * Disconnect from source and target.
     */
    public abstract void disconnect();

    /**
     * @return true for internal flows; false for external flows.
     */
    @Transient
    public abstract boolean isInternal();

    @Column( name = "ISALL" )
    public boolean isAll() {
        return all;
    }

    public void setAll( boolean all ) {
        this.all = all;
    }

    /**
     * Initialize relevant properties from another flow.
     * @param flow the other flow
     */
    public abstract void initFrom( Flow flow );

    /**
     * Test if a node is at either end of this flow.
     * @param outcome true for checking target, false for source
     * @param node the node
     * @return true if node is included in this flow
     */
    public boolean isConnectedTo( boolean outcome, Node node ) {
        return outcome && getTarget().equals( node )
            || !outcome && getSource().equals( node );
    }
    /** {@inheritDoc } */
    public List<Channel> allChannels() {
        return channels;
    }
}
