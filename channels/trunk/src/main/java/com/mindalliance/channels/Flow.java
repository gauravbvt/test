package com.mindalliance.channels;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Embeddable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * An arrow between two nodes in the information flow graph.
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
public abstract class Flow extends ModelObject implements Channelable {

    // TODO Should we annotate a flow as primary vs seconday
    // (when multiple flows are mutually redundant)?

    /**
     * A list of alternate communication channels for the flow.
     */
    private List<Channel> channels = new ArrayList<Channel>();
    /**
     * The flow's significance (critical, triggers a part, terminates a part, or just useful)
     */
    private Significance significance = Significance.Useful;

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

    @OneToMany( cascade = CascadeType.ALL )
    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    /**
     * Get the channels that are in effect.
     *
     * @return the effective channels
     */
    @Transient
    public abstract List<Channel> getEffectiveChannels();

    /**
     * Set the channels that are in effect.
     *
     * @param channels the channels
     */
    public abstract void setEffectiveChannels( List<Channel> channels );

    /**
     * Add an alternate channel for the flow
     *
     * @param channel a Channel
     */
    public void addChannel( Channel channel ) {
        addChannelIfUnique( channel );
    }

    /**
     * Add an alternate channel for the flow
     *
     * @param medium  a communication medium
     * @param address an address on the medium
     */
    public void addChannel( Medium medium, String address ) {
        addChannelIfUnique( new Channel( medium, address ) );
    }

    private void addChannelIfUnique( Channel channel ) {
        if ( !getEffectiveChannels().contains( channel ) ) getEffectiveChannels().add( channel );
    }

    /**
     * {@inheritDoc}
     */
    public void removeChannel( Channel channel ) {
        getEffectiveChannels().remove( channel );
    }

    /**
     * {@inheritDoc
     */
    @Transient
    public String getChannelsString() {
        return Channel.toString( getEffectiveChannels() );
    }

    @Embedded
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

    public Significance getSignificance() {
        return significance;
    }

    public void setSignificance( Significance significance ) {
        this.significance = significance;
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
     *
     * @return the description
     */
    @Transient
    public String getRequirementTitle() {
        boolean noName = getName() == null || getName().trim().isEmpty();
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
        boolean noName = getName() == null || getName().trim().isEmpty();
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
     * @see Service#connect(Node, Node, String)
     */
    abstract void setSource( Node source );

    /**
     * Set the target of this flow.
     * Note: this method should not be called directly.
     *
     * @param target the target node.
     * @see Service#connect(Node, Node, String)
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

    @Column( name = "isAll" )
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

    /**
     * Test if a node is at either end of this flow.
     *
     * @param outcome true for checking target, false for source
     * @param node    the node
     * @return true if node is included in this flow
     */
    public boolean isConnectedTo( boolean outcome, Node node ) {
        return outcome && getTarget().equals( node )
                || !outcome && getSource().equals( node );
    }

    /**
     * {@inheritDoc }
     */
    public List<Channel> allChannels() {
        return getEffectiveChannels();
    }

    @Transient
    public boolean isCritical() {
        return significance == Significance.Critical;
    }

    /**
     * Change significance to critical
     */
    public void becomeCritical() {
        significance = Significance.Critical;
    }

    /**
     * Whether flow triggers its target
     * @return a boolean
     */
    @Transient
    public boolean isTriggering() {
        return significance == Significance.Triggers;
    }

    /**
     * Change significance to triggers
     */
    public void becomeTriggering() {
        significance = Significance.Triggers;
    }

    /**
     * Whether flow terminates its target
     * @return a boolean
     */
    @Transient
    public boolean isTerminating() {
        return significance == Significance.Terminates;
    }

    /**
     * Change significance to terminates
     */
    public void becomeTerminating() {
        significance = Significance.Terminates;
    }

    /**
     * Whether flow terminates its source
     * @return a boolean
     */
    @Transient
    public boolean isSelfTerminating() {
        return significance == Significance.SelfTerminates;
    }

    /**
     * Change significance to self-terminates
     */
    public void becomeSelfTerminating() {
        significance = Significance.SelfTerminates;
    }

    /**
     * Whether a flow is required (critical, triggering or terminating).
     * @return a boolean
     */
    @Transient
    public boolean isRequired() {
        return isCritical() || isTriggering() || isTerminating();
    }

    /**
     * The significance of a flow
     */
    @Embeddable
    public enum Significance {
        Useful( "is useful", "is useful" ),
        Critical( "is critical", "is critical" ),
        Triggers( "triggers recipient's task", "triggers this task" ),
        SelfTerminates( "terminates this task", "terminates sender's task" ),
        Terminates( "terminates recipient's task", "terminates this task" );

        private String senderName;
        private String receiverName;

        Significance( String senderName, String receiverName ) {
            this.senderName = senderName;
            this.receiverName = receiverName;
        }

        public String getSenderName() {
            return senderName;
        }

        public String getReceiverName() {
            return receiverName;
        }

        /**
         * Get name from point of view of sender or receiver of flow.
         * @param isSender a boolean
         * @return a string
         */
        public String getName( boolean isSender ) {
            return isSender ? getSenderName() : getReceiverName();
        }

        /**
         * Instantiate a Significance from its name, relative to sender or receiver of a flow.
         *
         * @param name       a String
         * @param fromSender a boolean
         * @return a Significance
         */
        public static Significance fromName( String name, boolean fromSender ) {
            for ( Significance s : values() ) {
                if ( fromSender ) {
                    if ( s.senderName.equals( name ) ) {
                        return s;
                    }
                } else {
                    if ( s.receiverName.equals( name ) ) {
                        return s;
                    }
                }

            }
            throw new IllegalArgumentException( "Unknown Significance name: " + name );
        }

/*        *//**
         * Produce the list of names for all significances relative to sender or receiver of a flow.
         *
         * @param isSender a boolean
         * @return a list of strings
         *//*
        public static List<String> choices( boolean isSender ) {
            List<String> choices = new ArrayList<String>();
            for ( Significance s : values() ) {
                choices.add( isSender ? s.getSenderName() : s.getReceiverName() );
            }
            return choices;
        }*/

        /**
         * Get list choice of Significances
         * @return a list of Significances
         */
        @Transient
        public List<Significance> getChoices() {
            return Arrays.asList( values() );
        }
        
        /** {@inheritDoc} */
        public String toString() {
            return name();
        }


    }
}
