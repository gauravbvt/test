package com.mindalliance.channels;

import com.mindalliance.channels.pages.components.Channelable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * An arrow between two nodes in the information flow graph.
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
public abstract class Flow extends ModelObject implements Channelable {

    // TODO Should we annotate a flow as primary vs seconday (when multiple flows are mutually redundant)?
    /**
     * A list of alternate communication channels for the flow
     */
    private Set<Channel> channels = new HashSet<Channel>();

    /**
     * If this flow is critical to either source or target.
     */
    private boolean critical;

    /**
     * If this flow only happens on request from either the source or target.
     */
    private boolean askedFor;

    /**
     * How much lag time is expected for this flow.
     */
    private Delay maxDelay = new Delay();

    /**
     * If flow applies to all potential sources/targets.
     */
    private boolean all;

    protected Flow() {
    }

    public boolean isAskedFor() {
        return askedFor;
    }

    /**
     * Set if this flow is notified or asked for.
     *
     * @param askedFor true is information is asked for
     */
    public void setAskedFor( boolean askedFor ) {
        this.askedFor = askedFor;
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    public void setChannels( Set<Channel> channels ) {
        this.channels = channels;
    }

    public void addChannel( Channel channel ) {
        channels.add( channel );
    }

    public void addChannel( Medium medium, String address ) {
        channels.add( new Channel(medium, address) );
    }

    public void removeChannel( Channel channel ) {
        channels.remove( channel );
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
     *
     * @param s a string parseable to a Delay
     */
    public void setMaxDelay( String s ) {
        maxDelay = Delay.parse( s );
    }

    /**
     * Provide a out-of-context description of the flow.
     *
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
            final String sourceName = node.getName();
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
     *
     * @return the description
     */
    @Transient
    public String getRequirementTitle() {
        final boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                noName ? isAskedFor() ? "Ask {1} for something"
                        : "Notified of something by {1}"
                        : isAskedFor() ? "Ask {1} for {0}"
                        : "Notified of {0} by {1}",
                getName(), getShortName( getSource() ) );
    }

    /**
     * Provide a description of the flow, when viewed as an outcome.
     *
     * @return the description
     */
    @Transient
    public String getOutcomeTitle() {
        final boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                noName ? isAskedFor() ? "Answer {1} with something"
                        : "Notify {1} of something"
                        : isAskedFor() ? "Answer {1} with {0}"
                        : "Notify {1} of {0}",
                getName(), getShortName( getTarget() ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * @return Get the source of this flow.
     */
    @Transient
    public abstract Node getSource();

    /**
     * @return the target of this flow.
     */
    @Transient
    public abstract Node getTarget();

    /**
     * Set the source of this flow.
     * Note: this method should not be called directly.
     *
     * @param source the source node.
     * @see Scenario#connect(Node, Node)
     */
    abstract void setSource( Node source );

    /**
     * Set the target of this flow.
     * Note: this method should not be called directly.
     *
     * @param target the target node.
     * @see Scenario#connect(Node, Node)
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
     *
     * @param flow the other flow
     */
    public abstract void initFrom( Flow flow );
}
