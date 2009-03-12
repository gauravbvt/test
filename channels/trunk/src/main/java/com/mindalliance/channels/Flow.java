package com.mindalliance.channels;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An arrow between two nodes in the information flow graph.
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
public abstract class Flow extends ModelObject implements Channelable, ScenarioObject {

    /**
     * A list of alternate communication channels for the flow.
     */
    private List<Channel> channels = new ArrayList<Channel>();
    /**
     * The flow's significance to the source (none, triggers it, or terminates it)
     */
    private Significance significanceToSource = Significance.None;
    /**
     * The flow's significance to the target (useful, critical, triggers it, or terminates it)
     */
    private Significance significanceToTarget = Significance.Useful;

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
     * Whether the flow is a notification
     *
     * @return a boolean
     */
    @Transient
    public boolean isNotification() {
        return !isAskedFor();
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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

    @Enumerated( EnumType.ORDINAL )
    public Significance getSignificanceToSource() {
        return significanceToSource;
    }

    public void setSignificanceToSource( Significance significance ) {
        significanceToSource = significance;
    }

    @Enumerated( EnumType.ORDINAL )
    public Significance getSignificanceToTarget() {
        return significanceToTarget;
    }

    public void setSignificanceToTarget( Significance significanceToTarget ) {
        this.significanceToTarget = significanceToTarget;
    }

    private String getShortName( Node node, boolean qualified ) {
        String result = "somebody";

        if ( node != null ) {
            String sourceName = node.getName();
            if ( sourceName != null && !sourceName.trim().isEmpty() ) {
                result = sourceName;
            }

            if ( qualified && node.isPart() && ( (Part) node ).isOnlyRole() )
                result = MessageFormat.format( isAll() ? "every {0}" : "any {0}", result );
        }

        return result;
    }

    /**
     * Provide a out-of-context description of the flow.
     *
     * @return the description
     */
    @Transient
    public String getTitle() {
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";

        return MessageFormat.format(
                isAskedFor() ? "{2} ask {1} about {0}"
                        : isTriggeringToTarget() ? "{1} telling {2} to {0}"
                        : "{1} notify {2} of {0}",

                message, getShortName( getSource(), false ), getShortName( getTarget(), false ) );
    }

    /**
     * Provide a description of the flow, when viewed as a requirement.
     *
     * @return the description
     */
    @Transient
    public String getRequirementTitle() {
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = !isAskedFor() && isTriggeringToTarget() ? "do something" : "something";

        if ( getSource().isConnector() ) {
            return MessageFormat.format(
                    isAskedFor() ? "Needs to ask for {0}"
                            : isTriggeringToTarget() ? "Needs to be told to {0}"
                            : "Needs to be notified of {0}",
                    message.toLowerCase() );

        } else {
            return MessageFormat.format(
                    isAskedFor() ? "Ask {1} for {0}"
                            : isTriggeringToTarget() ? "Told to {0} by {1}"
                            : "Notified of {0} by {1}",
                    message.toLowerCase(), getShortName( getSource(), false ) );
        }
    }

    /**
     * Provide a description of the flow, when viewed as an outcome.
     *
     * @return the description
     */
    @Transient
    public String getOutcomeTitle() {
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";

        Node node = getTarget();
        if ( node.isConnector() ) {
            String format = isAskedFor() ? "Can answer with {0}"
                    : isTriggeringToTarget() ? "Can tell to {0}"
                    : "Can notify of {0}";

            return MessageFormat.format( format, message.toLowerCase() );

        } else {
            String format = isAskedFor() ? "Answer {1} with {0}"
                    : isTriggeringToTarget() ? "Tell {1} to {0}"
                    : "Notify {1} of {0}";

            return MessageFormat.format(
                    format, message.toLowerCase(), getShortName( node, true ) );
        }
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
        return getSignificanceToTarget() == Significance.Critical;
    }

    /**
     * Change significance to critical
     */
    public void becomeCritical() {
        setSignificanceToTarget( Significance.Critical );
    }

    /**
     * Whether flow triggers its source
     *
     * @return a boolean
     */
    @Transient
    public boolean isTriggeringToSource() {
        return getSignificanceToSource() == Significance.Triggers;
    }

    /**
     * Change significance to triggers source
     */
    @Transient
    public void becomeTriggeringToSource() {
        setSignificanceToSource( Significance.Triggers );
    }

    /**
     * Whether flow triggers its target
     *
     * @return a boolean
     */
    @Transient
    public boolean isTriggeringToTarget() {
        return getSignificanceToTarget() == Significance.Triggers;
    }

    /**
     * Change significance to triggers target
     */
    public void becomeTriggeringToTarget() {
        setSignificanceToTarget( Significance.Triggers );
    }

    /**
     * Whether flow terminates its source
     *
     * @return a boolean
     */
    @Transient
    public boolean isTerminatingToSource() {
        return getSignificanceToSource() == Significance.Terminates;
    }

    /**
     * Change significance to terminates source
     */
    public void becomeTerminatingToSource() {
        setSignificanceToSource( Significance.Terminates );
    }

    /**
     * Whether flow terminates its target
     *
     * @return a boolean
     */
    @Transient
    public boolean isTerminatingToTarget() {
        return getSignificanceToTarget() == Significance.Terminates;
    }

    /**
     * Change significance to terminates source
     */
    public void becomeTerminatingToTarget() {
        setSignificanceToTarget( Significance.Terminates );
    }

    /**
     * Whether a flow is required (critical, triggering or terminating).
     *
     * @return a boolean
     */
    @Transient
    public boolean isRequired() {
        return isCritical() || isTriggeringToTarget() || isTerminatingToTarget();
    }

    /**
     * {@inheritDoc}
     */
    public void moveToFirst( Channel channel ) {
        channels.remove( channel );
        channels.add( 0, channel );
    }

    // Abstract methods

    /**
     * Whether the flow's name and description can be set.
     *
     * @return a boolean
     */
    public abstract boolean canSetNameAndDescription();

    /**
     * Whether the flow's max delay can be set.
     *
     * @return a boolean
     */
    public abstract boolean canSetMaxDelay();

    /**
     * Whether the flow's max delay property applies.
     *
     * @return a boolean
     */
    public abstract boolean canGetMaxDelay();

    /**
     * Whether the flow's channels can be set.
     *
     * @return a boolean
     */
    public abstract boolean canSetChannels();

    /**
     * Whether the flow's channels property applies.
     *
     * @return a boolean
     */
    public abstract boolean canGetChannels();

    /**
     * Whether the flow's notify vs. reply property can be set.
     *
     * @return a boolean
     */
    public abstract boolean canSetAskedFor();

    /**
     * Whether the flow's all (true or false) can be set.
     *
     * @return a boolean
     */
    public abstract boolean canSetAll();

    /**
     * Whether the flow's all (true or false) properties applies.
     *
     * @return a boolean
     */
    public abstract boolean canGetAll();

    /**
     * Whether the significance to target applies.
     *
     * @return a boolean
     */
    public abstract boolean canGetSignificanceToTarget();

    /**
     * Whether the significance to target can be set.
     *
     * @return a boolean
     */
    public abstract boolean canSetSignificanceToTarget();

    /**
     * Whether the significance to source applies.
     *
     * @return a boolean
     */
    public abstract boolean canGetSignificanceToSource();

    /**
     * Whether the significance to source can take value Triggers.
     *
     * @return a boolean
     */
    public abstract boolean canSetTriggersSource();

    /**
     * Whether the significance to source can take value Terminates.
     *
     * @return a boolean
     */
    public abstract boolean canSetTerminatesSource();

    /**
     * Flow could trigger the part
     *
     * @return a boolean
     */
    public abstract boolean canGetTriggersSource();

    /**
     * Flow could terminate the part
     *
     * @return a boolean
     */
    public abstract boolean canGetTerminatesSource();

    /**
     * Breakup a flow, possibly creating connector-based flows to preserve requirement and outcome.
     * TODO - Move to BreakupFlow command
     */
    public abstract void breakup();

    /**
     * Make a replicate of the flow
     *
     * @param isOutcome whether to replicate as outcome or requirement
     * @return a created flow
     */
    //TODO remove
    public Flow replicate( boolean isOutcome ) {
        Flow flow;
        if ( isOutcome ) {
            Node source = getSource();
            Scenario scenario = getSource().getScenario();
            Service service = scenario.getService();
            flow = service.connect( source, service.createConnector( scenario ), getName() );
        } else {
            Node target = getTarget();
            Scenario scenario = target.getScenario();
            Service service = scenario.getService();
            flow = service.connect( service.createConnector( scenario ), target, getName() );
        }
        flow.initFrom( this );
        return flow;
    }

    /**
     * Whether the flow has a connector as source or target
     *
     * @return a boolean
     */
    public abstract boolean hasConnector();

    /**
     * Get a copy of the list of channels
     *
     * @return copied list of channels
     */
    @Transient
    public List<Channel> getChannelsCopy() {
        List<Channel> channelsCopy = new ArrayList<Channel>();
        for ( Channel channel : getChannels() ) {
            channelsCopy.add( new Channel( channel ) );
        }
        return channelsCopy;
    }

    /**
     * The significance of a flow.
     */
    public enum Significance {
        Triggers( "triggers" ),
        Critical( "is critical to" ),
        Useful( "is useful to" ),
        None( "none" ),
        Terminates( "terminates" );

        private String label;

        Significance( String label ) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }


        /**
         * Instantiate a Significance from its label.
         *
         * @param label a String
         * @return a Significance
         */
        public static Significance fromLabel( String label ) {
            for ( Significance s : values() ) {
                if ( s.getLabel().equals( label ) ) {
                    return s;
                }
            }
            throw new IllegalArgumentException( "Unknown Significance label: " + label );
        }


        /**
         * Get list choice of Significances
         *
         * @return a list of Significances
         */
        @Transient
        public List<Significance> getChoices() {
            return Arrays.asList( values() );
        }

        /**
         * {@inheritDoc}
         */
        public String toString() {
            return name();
        }
    }
}
