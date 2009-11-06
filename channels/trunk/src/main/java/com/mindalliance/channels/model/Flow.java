package com.mindalliance.channels.model;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.util.Matcher;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    /**
     * Elements of information.
     */
    private List<ElementOfInformation> eois = new ArrayList<ElementOfInformation>();

    protected Flow() {
    }

    // todo - remove when cut-over is completed
    public void setDescription( String val ) {
        if ( !val.isEmpty() ) setEOIsFrom( val );
    }

    // todo - remove when cut-over is completed
    private void setEOIsFrom( String val ) {
        List<String> contents = Matcher.extractEOIs( val );
        for ( String content : contents ) {
            ElementOfInformation eoi = new ElementOfInformation();
            eoi.setContent( content );
            addEOI( eoi );
        }
    }

    /**
     * Get EOIs as a string.
     * @return a string
     */
    @Transient
    public String getEOIsDescription() {
        return descriptionFromEOIs();
    }

    private String descriptionFromEOIs() {
        StringBuilder sb = new StringBuilder();
        Iterator<ElementOfInformation> iter = getEois().iterator();
        while ( iter.hasNext() ) {
            ElementOfInformation eoi = iter.next();
            sb.append( eoi.toString() );
            if ( iter.hasNext() ) sb.append( '\n' );
        }
        return sb.toString();
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

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels( List<Channel> channels ) {
        this.channels = channels;
    }

    /**
     * Set the channels that are in effect.
     *
     * @param channels the channels
     */
    @Transient
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

    public List<ElementOfInformation> getEois() {
        return eois;
    }

    public void setEois( List<ElementOfInformation> eois ) {
        this.eois = eois;
    }

    /**
     * Add element of information.
     *
     * @param eoi an element of information
     */
    public void addEoi( ElementOfInformation eoi ) {
        eois.add( eoi );
    }

    /**
     * Add element of information.
     *
     * @param eoi an element of information
     */
    public void addEOI( ElementOfInformation eoi ) {
        eois.add( eoi );
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
                isAskedFor() ? "{2} ask {1} about \"{0}\""
                        //    : isTriggeringToTarget() ? "{1} telling {2} to {0}"
                        : "{1} notify {2} of \"{0}\"",

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
            message = /*!isAskedFor() && isTriggeringToTarget() ? "do something" :*/ "something";
        Node source = getSource();
        if ( source.isConnector() ) {
            return MessageFormat.format(
                    isAskedFor() ? "Needs to ask for \"{0}\""
                            //  : isTriggeringToTarget() ? "Needs to be told to {0}"
                            : "Needs to be notified of \"{0}\"",
                    message.toLowerCase() );

        } else {
            Part part = (Part) source;
            return MessageFormat.format(
                    isAskedFor() ? "Ask {1}{2}{3} for \"{0}\""
                            //   : isTriggeringToTarget() ? "Told to {0} by {1}{2}{3}"
                            : "Notified of \"{0}\" by {1}{2}{3}",
                    message.toLowerCase(),
                    getShortName( part, false ),
                    getOrganizationString( part ),
                    getJurisdictionString( part ) );
        }
    }

    @Transient
    private static String getOrganizationString( Part part ) {
        Organization organization = part.getOrganization();
        return organization == null || part.getRole() == null && part.getActor() == null ? ""
                : MessageFormat.format( " in {0}", organization.getLabel() );
    }

    @Transient
    private static String getJurisdictionString( Part part ) {
        Place place = part.getJurisdiction();
        return place == null ? ""
                : MessageFormat.format( " for {0}", place );
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
            String format = isAskedFor() ? "Can answer with \"{0}\""
                    //   : isTriggeringToTarget() ? "Can tell to {0}"
                    : "Can notify of \"{0}\"";

            return MessageFormat.format( format, message.toLowerCase() );

        } else {
            Part part = (Part) node;
            String format = isAskedFor() ? "Answer {1}{2}{3} with \"{0}\""
                    //  : isTriggeringToTarget() ? "Tell {1}{2}{3} to {0}"
                    : "Notify {1}{2}{3} of \"{0}\"";

            return MessageFormat.format(
                    format, message.toLowerCase(),
                    getShortName( node, true ),
                    getOrganizationString( part ),
                    getJurisdictionString( part ) );
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
     * @see com.mindalliance.channels.QueryService#connect(Node, Node, String)
     */
    abstract void setSource( Node source );

    /**
     * Set the target of this flow.
     * Note: this method should not be called directly.
     *
     * @param target the target node.
     * @see com.mindalliance.channels.QueryService#connect(Node, Node, String)
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
            QueryService queryService = scenario.getQueryService();
            flow = queryService.connect( source, queryService.createConnector( scenario ), getName() );
        } else {
            Node target = getTarget();
            Scenario scenario = target.getScenario();
            QueryService queryService = scenario.getQueryService();
            flow = queryService.connect( queryService.createConnector( scenario ), target, getName() );
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
     * {@inheritDoc}
     */
    public boolean canBeUnicast() {
        Node node = isAskedFor() ? getSource() : getTarget();
        if ( node.isPart() ) {
            Part part = (Part) node;
            ResourceSpec resourceSpec = part.resourceSpec();
            return resourceSpec.isActor() || resourceSpec.isOrganization();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc }
     */
    public String validate( Channel channel ) {
        String problem = null;
        if ( !canBeUnicast() && channel.isUnicast() ) {
            if ( !channel.getAddress().isEmpty() ) {
                problem = "Can't specify a unicast address when not communicating with an actor or organization.";
            }
        } else if ( !channel.isValid() ) {
            problem = "Invalid address";
        }
        return problem;
    }

    /**
     * Get part being contacted if any.
     *
     * @return a part or null if contacting a connector
     */
    @Transient
    public Part getContactedPart() {
        Node node = isAskedFor() ? getSource() : getTarget();
        return node.isPart() ? (Part) node : null;
    }

    /**
     * Get node being contacted if any.
     *
     * @return a part or a connector
     */
    @Transient
    public Node getContactedNode() {
        return isAskedFor() ? getSource() : getTarget();
    }

    /**
     * Get scenario-local part
     *
     * @return a part or null
     */
    @Transient
    public Part getLocalPart() {
        Node source = getSource();
        if ( source.isPart() && source.getScenario() == getScenario() ) {
            return (Part) source;
        } else {
            Node target = getTarget();
            if ( target.isPart() && target.getScenario() == getScenario() ) {
                return (Part) target;
            } else {
                // Should never happen?
                return null;
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isUndefined() {
        return super.isUndefined()
                && channels.isEmpty()
                && significanceToSource == Significance.None
                && significanceToTarget == Significance.Useful
                && maxDelay.equals( new Delay() );
    }

    /**
     * Get a string description of the kind of communication
     * including max delay if applicable
     *
     * @return a string description of the communication
     */
    @Transient
    public String getKind() {
        return isAskedFor() ? "answer" : "notify";
    }

    /**
     * Get the broadcast channels associated with this flow.
     *
     * @return a collection of channels
     */
    @Transient
    public Collection<Channel> getBroadcasts() {
        Set<Channel> broadcasts = new HashSet<Channel>();
        for ( Channel c : getEffectiveChannels() )
            if ( c.isBroadcast() )
                broadcasts.add( c );
        return broadcasts;
    }

    /**
     * Get the unicast media used by this flow.
     *
     * @return a set of media
     */
    @Transient
    public Set<Medium> getUnicasts() {
        Set<Medium> result = EnumSet.noneOf( Medium.class );

        for ( Channel c : getEffectiveChannels() ) {
            Medium medium = c.getMedium();
            if ( medium.isUnicast() )
                result.add( medium );
        }
        return result;
    }

    /**
     * Whether this flow represents an information sharing.
     *
     * @return a boolean
     */
    @Transient
    public boolean isSharing() {
        return getSource().isPart() && getTarget().isPart();
    }

    /**
     * Flow is an information need.
     *
     * @return a boolean
     */
    @Transient
    public boolean isNeed() {
        return getSource().isConnector();
    }

    /**
     * Flow is an information capability.
     *
     * @return a boolean
     */
    @Transient
    public boolean isCapability() {
        return getTarget().isConnector();
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public List<Attachment.Type> getAttachmentTypes() {
        List<Attachment.Type> types = super.getAttachmentTypes();
        if ( !hasImage() )
            types.add( Attachment.Type.Image );
        types.add( Attachment.Type.PolicyMust );
        types.add( Attachment.Type.PolicyCant );
        return types;
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        return "flow";
    }

    /**
     * Get all distinct classifications of the flow's elements of information.
     *
     * @return a list of classifications
     */
    @Transient
    public List<Classification> getClassifications() {
        Set<Classification> classifications = new HashSet<Classification>();
        for ( ElementOfInformation eoi : eois ) {
            classifications.addAll( eoi.getClassifications() );
        }
        return new ArrayList<Classification>( classifications );
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
