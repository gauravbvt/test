package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.AbstractModelObjectDao;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.Transformer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An arrow between two nodes in the information flow graph.
 */
public abstract class Flow extends ModelObject implements Channelable, SegmentObject, Prohibitable, EOIsHolder {

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
    /**
     * Whether eois classifications are expected to "share" the same classifications.
     */
    private boolean classificationsLinked = true;

    /**
     * The intent of a flow.
     */
    private Intent intent;
    /**
     * Restriction on implied sharing commitments. // todo - OBSOLETE
     */
    private Restriction restriction;

    /**
     * restrictions on implied sharing commitments.
     */
    private List<Restriction> restrictions = new ArrayList<Restriction>();
    /**
     * Flow applies only if task fails. (Send only)
     */
    private boolean ifTaskFails;

    /**
     * Whether the flow is prohibited.
     */
    private boolean prohibited = false;
    /**
     * Whether the flow clarifies the event phase in which it happens.
     */
    private boolean referencesEventPhase = true;

    /**
     * If notification, whether the target can be bypassed (set by notifier).
     * If request-reply, whether the source can be bypassed (set by requester).
     */
    private boolean canBypassIntermediate = false;

    private boolean receiptConfirmationRequested = false;

    /**
     * InfoProduct standardizing the information flowing.
     */
    private InfoProduct infoProduct;

    /**
     * Whether the info flowing is standardized as an InfoProduct having the name of the flow.
     */
    private boolean standardized = false;

    /**
     * Whether the need, capability or implied commitments (if sharing flow) can be made visible to third parties.
     */
    private boolean published = false;

    protected Flow() {
    }

    public static String classLabel() {
        return "information flows";
    }

    @Override
    public String getName() {
        if ( infoProduct != null ) {
            return infoProduct.getName();
        } else {
            return super.getName();
        }
    }

    @Override
    public String getClassLabel() {
        return classLabel();
    }

    @Override
    public boolean isSegmentObject() {
        return true;
    }


    @Override
    public String getKindLabel() {
        return "Flow";
    }

    /**
     * Whether a flow connecting source and target would be an internal flow.
     *
     * @param source a node
     * @param target a node
     * @return a boolean
     */
    public static boolean isInternal( Node source, Node target ) {
        Segment segment = source.getSegment();
        return segment != null && segment.equals( target.getSegment() );
    }

    /**
     * Whether a flow connecting source and target would be an external flow.
     *
     * @param source a node
     * @param target a node
     * @return a boolean
     */
    public static boolean isExternal( Node source, Node target ) {
        Segment segment = source.getSegment();
        return segment != null
                && !segment.equals( target.getSegment() )
                && ( target.isConnector() || source.isConnector() );
    }

    public boolean isAskedFor() {
        return askedFor;
    }

    /**
     * Whether the flow is a notification
     *
     * @return a boolean
     */
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
    public abstract void setEffectiveChannels( List<Channel> channels );

    @Override
    public List<Channel> getModifiableChannels() {
        return getEffectiveChannels();
    }

    public void addChannel( Channel channel ) {
        addChannelIfUnique( channel );
    }

    /**
     * Add an alternate channel for the flow
     *
     * @param medium  a communication medium
     * @param address an address on the medium
     */
    public void addChannel( TransmissionMedium medium, String address ) {
        addChannelIfUnique( new Channel( medium, address ) );
    }

    private void addChannelIfUnique( Channel channel ) {
        if ( !getEffectiveChannels().contains( channel ) ) getEffectiveChannels().add( channel );
    }

    public void removeChannel( Channel channel ) {
        getEffectiveChannels().remove( channel );
    }

    public String getChannelsString() {
        return Channel.toString( getEffectiveChannels() );
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

    public List<ElementOfInformation> getEois() {
        return getLocalEois();
    }

    @Override
    public List<ElementOfInformation> getLocalEois() {
        return eois;
    }

    /**
     * Return all EOIS inherited from info product (no redundancies) and all local EOIs not overridden by inherited EOIS.
     * Local EOIS are at the top of the list.
     *
     * @return a list of EOIS
     */
    public List<ElementOfInformation> getEffectiveEois() {
        List<ElementOfInformation> allEois = new ArrayList<ElementOfInformation>();
        List<ElementOfInformation> inheritedEois = getInheritedEois();
        for ( final ElementOfInformation eoi : getLocalEois() ) {
            if ( !isOverridden( eoi, inheritedEois ) ) {
                allEois.add( eoi );
            }
        }
        allEois.addAll( inheritedEois );
        return Collections.unmodifiableList( allEois );
    }

    private List<ElementOfInformation> getInheritedEois() {
        if ( infoProduct != null ) {
            return infoProduct.getEffectiveEois();
        } else {
            return new ArrayList<ElementOfInformation>();
        }
    }

    private boolean isOverridden( final ElementOfInformation eoi, List<ElementOfInformation> inheritedEois ) {
        return CollectionUtils.exists(
                inheritedEois,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return Matcher.same( ( (ElementOfInformation) object ).getContent(), eoi.getContent() );
                    }
                }
        );
    }


    public InfoProduct getInfoProduct() {
        return infoProduct;
    }

    public void setEois( List<ElementOfInformation> elements ) {
        setLocalEois( elements );
    }

    public void setLocalEois( List<ElementOfInformation> elements ) {
        eois = new ArrayList<ElementOfInformation>( elements );
    }

    public boolean isClassificationsLinked() {
        return classificationsLinked;
    }

    public void setClassificationsLinked( boolean classificationsLinked ) {
        this.classificationsLinked = classificationsLinked;
    }

    /**
     * Whether at least one eoi is classified.
     *
     * @return a boolean
     */
    public boolean isClassified() {
        return CollectionUtils.exists(
                getEffectiveEois(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (ElementOfInformation) obj ).isClassified();
                    }
                }
        );
    }


    /**
     * Add element of information.
     *
     * @param eoi an element of information
     */
    @Override
    public void addLocalEoi( ElementOfInformation eoi ) {
        assert !isStandardized();
        if ( !eois.contains( eoi ) ) {
            if ( isNeed() ) {
                eoi.retainContentAndTimeSensitivityOnly();
            }
            eois.add( eoi );
        }
    }

    public Significance getSignificanceToSource() {
        return !isAskedFor() && significanceToSource == Significance.Triggers
                ? Significance.None
                : significanceToSource;
    }

    public void setSignificanceToSource( Significance significance ) {
        significanceToSource = significance;
    }

    public Significance getSignificanceToTarget() {
        return isAskedFor() && significanceToTarget == Significance.Triggers
                ? Significance.Useful
                : significanceToTarget;
    }

    public void setSignificanceToTarget( Significance significanceToTarget ) {
        this.significanceToTarget = significanceToTarget;
    }

    public boolean isProhibited() {
        return prohibited;
    }

    public void setProhibited( boolean prohibited ) {
        this.prohibited = prohibited;
    }

    public boolean isReferencesEventPhase() {
        return referencesEventPhase;
    }

    public void setReferencesEventPhase( boolean referencesEventPhase ) {
        this.referencesEventPhase = referencesEventPhase;
    }

    public boolean isCanBypassIntermediate() {
        return canBypassIntermediate;
    }

    public void setCanBypassIntermediate( boolean canBypassIntermediate ) {
        this.canBypassIntermediate = canBypassIntermediate;
    }

    public boolean isReceiptConfirmationRequested() {
        return receiptConfirmationRequested;
    }

    public void setReceiptConfirmationRequested( boolean receiptConfirmationRequested ) {
        this.receiptConfirmationRequested = receiptConfirmationRequested;
    }

    public boolean canGetCanBypassIntermediate() {
        return isSharing();
    }

    public boolean canGetReceiptConfirmationRequested() {
        return isSharing();
    }

    public boolean canGetProhibited() {
        return isSharing();
    }

    public boolean canSetProhibited() {
        return canGetProhibited();
    }

    public boolean canGetReferencesEventPhase() {
        return isSharing();
    }

    public boolean canSetReferencesEventPhase() {
        return canGetReferencesEventPhase();
    }

    public boolean canSetReceiptConfirmationRequested() {
        return canGetReceiptConfirmationRequested();
    }

    public boolean canSetCanBypassIntermediate() {
        return canGetCanBypassIntermediate();
    }

    public void setInfoProduct( InfoProduct infoProduct ) {
        this.infoProduct = infoProduct;
    }

    public boolean isStandardized() {
        return standardized;
    }

    public void setStandardized( boolean standardized ) {
        this.standardized = standardized;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished( boolean published ) {
        this.published = published;
    }

    public String getShortName( Node node, boolean qualified ) {
        String result = "somebody";

        if ( node != null ) {
            String sourceName = node.getName();
            if ( sourceName != null && !sourceName.trim().isEmpty() ) {
                result = sourceName;
            }

            if ( qualified && node.isPart() && ( (Part) node ).isOnlyRoleOrAgentType() )
                result = MessageFormat.format( isAll() ? "every {0}" : "any {0}", result );
        }

        return result;
    }

    /**
     * Provide a out-of-context description of the flow.
     *
     * @return the description
     */
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
     * Provide a description of the flow as a step in a checklist.
     *
     * @return the description
     */
    public String getStepTitle( boolean prerequisite, boolean answer ) {
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";
        StringBuilder sb = new StringBuilder();
        String intentLabel = getIntent() == null
                ? "information"
                : getIntent().getLabel().toLowerCase();
        if ( isAskedFor() ) {
            if ( answer ) {
                sb.append( prerequisite ? "Answering with " : "Answer with " )
                        .append( intentLabel )
                        .append( " \"" )
                        .append( message )
                        .append( "\"" )
                        .append( " to " )
                        .append( getShortName( getTarget(), false ) );

            } else {
                sb.append( prerequisite ? "Asking for " : "Ask for " )
                        .append( intentLabel )
                        .append( " \"" )
                        .append( message )
                        .append( "\"" )
                        .append( " from " )
                        .append( getShortName( getSource(), false ) );
            }
        } else {
            sb.append( prerequisite ? "Sending " : "Send " )
                    .append( intentLabel )
                    .append( " \"" )
                    .append( message )
                    .append( "\"" )
                    .append( " to " )
                    .append( getShortName( getTarget(), false ) );
            if ( isTerminatingToSource() ) {
                if ( !prerequisite ) sb.append( " - and stop" );
            }
        }
        return sb.toString();
    }


    /**
     * Provide a description of the flow, when viewed as a receive.
     *
     * @return the description
     */
    public String getReceiveTitle() {
        String title;
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = /*!isAskedFor() && isTriggeringToTarget() ? "do something" :*/ "something";
        if ( getIntent() != null ) {
            message += " (" + getIntent().getLabel().toLowerCase() + ")";
        }
        Node source = getSource();
        if ( source.isConnector() ) {
            title = MessageFormat.format(
                    isAskedFor() ? "Needs to ask for \"{0}\""
                            //  : isTriggeringToTarget() ? "Needs to be told to {0}"
                            : "Needs to be notified of \"{0}\"",
                    message.toLowerCase() );

        } else {
            Part part = (Part) source;
            title = MessageFormat.format(
                    isAskedFor() ? "Ask {1}{2}{3} for \"{0}\""
                            //   : isTriggeringToTarget() ? "Told to {0} by {1}{2}{3}"
                            : "Notified of \"{0}\" by {1}{2}{3}",
                    message.toLowerCase(),
                    getShortName( part, false ),
                    getOrganizationString( part ),
                    getJurisdictionString( part ) );
        }
        return ( isProhibited() ? "Prohibited: " : "" ) + title;
    }

    public static String getOrganizationString( Part part ) {
        Organization organization = part.getOrganization();
        return organization == null || part.getRole() == null && part.getActor() == null ? ""
                : MessageFormat.format( " in {0}", organization.getLabel() );
    }

    public static String getJurisdictionString( Part part ) {
        Place place = part.getJurisdiction();
        return place == null ? ""
                : MessageFormat.format( " for {0}", place );
    }

    /**
     * Provide a description of the flow, when viewed as a send.
     *
     * @return the description
     */
    public String getSendTitle() {
        String title;
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";
        if ( getIntent() != null ) {
            message += " (" + getIntent().getLabel().toLowerCase() + ")";
        }
        Node node = getTarget();
        if ( node.isConnector() ) {
            String format = isAskedFor() ? "Can answer with \"{0}\""
                    : "Can notify of \"{0}\"";

            title = MessageFormat.format( format,
                    message.toLowerCase() );

        } else {
            Part part = (Part) node;
            String format = isAskedFor() ? "Answer {1}{2}{3} with \"{0}\""
                    : "Notify {1}{2}{3} of \"{0}\"";

            title = MessageFormat.format(
                    format, message.toLowerCase(),
                    getShortName( node, true ),
                    getOrganizationString( part ),
                    getJurisdictionString( part ) );
        }
        return ( isProhibited() ? "Prohibited: " : "" ) + title;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * @return Get the source of this flow.
     */
    public abstract Node getSource();

    /**
     * @return the target of this flow.
     */
    public abstract Node getTarget();

    /**
     * Set the source of this flow.
     * Note: this method should not be called directly.
     *
     * @param source the source node.
     * @see com.mindalliance.channels.core.query.QueryService#connect(Node, Node, String)
     */
    abstract void setSource( Node source );

    /**
     * Set the target of this flow.
     * Note: this method should not be called directly.
     *
     * @param target the target node.
     * @see com.mindalliance.channels.core.query.QueryService#connect(Node, Node, String)
     */
    abstract void setTarget( Node target );

    /**
     * @return true for internal flows; false for external flows.
     */
    public abstract boolean isInternal();

    public boolean isAll() {
        return all;
    }

    public void setAll( boolean all ) {
        this.all = all;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent( Intent intent ) {
        this.intent = intent;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions( List<Restriction> restrictions ) {
        this.restrictions = restrictions;
    }

    public void addRestriction( Restriction restriction ) {
        if ( !restrictions.contains( restriction )
                && !contradictsRestrictions( restriction )
                && !isImplied( restriction ) ) {
            restrictions.add( restriction );
        }
    }

    private boolean isImplied( final Restriction restriction ) {
        return !restrictions.isEmpty() &&
                CollectionUtils.exists(
                        restrictions,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return Restriction.implies( (Restriction) object, restriction ); // first narrows the second
                            }
                        } );
    }

    private boolean contradictsRestrictions( final Restriction restriction ) {
        return !restrictions.isEmpty() &&
                CollectionUtils.exists(
                        restrictions,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Restriction) object ).contradicts( restriction );
                            }
                        } );
    }

    public boolean isIfTaskFails() {
        return ifTaskFails;
    }

    public void setIfTaskFails( boolean ifTaskFails ) {
        this.ifTaskFails = ifTaskFails;
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
     * @param isSend true for checking target, false for source
     * @param node   the node
     * @return true if node is included in this flow
     */
    public boolean isConnectedTo( boolean isSend, Node node ) {
        return isSend && getTarget().equals( node )
                || !isSend && getSource().equals( node );
    }

    public List<Channel> allChannels() {
        return getEffectiveChannels();
    }

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
    public boolean isTriggeringToSource() {
        return getSignificanceToSource() == Significance.Triggers;
    }

    /**
     * Change significance to triggers source
     */
    public void becomeTriggeringToSource() {
        setSignificanceToSource( Significance.Triggers );
    }

    /**
     * Whether flow triggers its target
     *
     * @return a boolean
     */
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
    public boolean isRequired() {
        return isCritical() || isTriggeringToTarget() || isTerminatingToTarget();
    }

    // Abstract methods

    /**
     * Whether the flow's name and elemsnts can be set.
     *
     * @return a boolean
     */
    public abstract boolean canSetNameAndElements();

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
    public List<Channel> getChannelsCopy() {
        List<Channel> channelsCopy = new ArrayList<Channel>();
        for ( Channel channel : getChannels() ) {
            channelsCopy.add( new Channel( channel ) );
        }
        return channelsCopy;
    }

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

    public String validate( Channel channel ) {
        TransmissionMedium medium = channel.getMedium();
        if ( medium == null || medium.isUnknown() ) {
            return "The medium is undefined";
        } else {
            return null;
        }
    }

    @Override
    public boolean isModelObject() {
        return true;
    }

    @Override
    public boolean hasChannelFor( final TransmissionMedium medium, final Place planLocale ) {
        return CollectionUtils.exists(
                getEffectiveChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Channel) object ).getMedium().narrowsOrEquals( medium, planLocale );
                    }
                }
        );
    }

    @Override
    public boolean canBeLocked() {
        return true;
    }

    @Override
    public boolean hasAddresses() {
        return false;
    }

    @Override
    public void setAddress( Channel channel, String address ) {
        channel.setAddress( address );
    }

    @Override
    public boolean canSetFormat() {
        return true;
    }

    /**
     * Get part being contacted if any.
     *
     * @return a part or null if contacting a connector
     */
    public Part getContactedPart() {
        Node node = isAskedFor() ? getSource() : getTarget();
        return node.isPart() ? (Part) node : null;
    }

    /**
     * Get node being contacted if any.
     *
     * @return a part or a connector
     */
    public Node getContactedNode() {
        return isAskedFor() ? getSource() : getTarget();
    }

    /**
     * Get segment-local part
     *
     * @return a part or null
     */
    public Part getLocalPart() {
        Node source = getSource();
        if ( source.isPart() && source.getSegment().equals( getSegment() ) ) {
            return (Part) source;
        } else {
            Node target = getTarget();
            if ( target.isPart() && target.getSegment().equals( getSegment() ) ) {
                return (Part) target;
            } else {
                // Should never happen?
                return null;
            }

        }
    }

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
    public String getKind() {
        return isAskedFor() ? "answer" : "notify";
    }

    /**
     * Get the broadcast channels associated with this flow.
     *
     * @return a collection of channels
     */
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
    public Set<TransmissionMedium> getUnicasts() {
        Set<TransmissionMedium> result = new HashSet<TransmissionMedium>();

        for ( Channel c : getEffectiveChannels() ) {
            TransmissionMedium medium = c.getMedium();
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
    public boolean isSharing() {
        return getSource().isPart() && getTarget().isPart();
    }

    /**
     * Flow is an information need.
     *
     * @return a boolean
     */
    public boolean isNeed() {
        return isInternal() && getSource().isConnector();
    }

    /**
     * Flow is an information capability.
     *
     * @return a boolean
     */
    public boolean isCapability() {
        return isInternal() && getTarget().isConnector();
    }

    public List<AttachmentImpl.Type> getAttachmentTypes() {
        List<AttachmentImpl.Type> types = super.getAttachmentTypes();
        if ( !hasImage() )
            types.add( AttachmentImpl.Type.Image );
        types.add( AttachmentImpl.Type.PolicyMust );
        types.add( AttachmentImpl.Type.PolicyCant );
        return types;
    }

    public String getTypeName() {
        return "flow";
    }

    /**
     * Get all distinct classifications of the flow's elements of information.
     *
     * @return a list of classifications
     */
    public List<Classification> getClassifications() {
        Set<Classification> classifications = new HashSet<Classification>();
        for ( ElementOfInformation eoi : getEffectiveEois() ) {
            classifications.addAll( eoi.getClassifications() );
        }
        return new ArrayList<Classification>( classifications );
    }

    /**
     * Generate a copy of the eois but each with the union of their classifications.
     *
     * @return a list of elements of information
     */
    @SuppressWarnings("unchecked")
    public List<ElementOfInformation> getEOISWithSameClassifications() {
        List<Classification> allClassifications = getAllEOIClassifications();
        List<ElementOfInformation> eoisCopy = new ArrayList<ElementOfInformation>();
        for ( ElementOfInformation eoi : getEffectiveEois() ) {
            ElementOfInformation copy = new ElementOfInformation();
            copy.setContent( eoi.getContent() );
            copy.setDescription( eoi.getDescription() );
            copy.setSpecialHandling( eoi.getSpecialHandling() );
            copy.setClassifications( new ArrayList<Classification>( allClassifications ) );
            eoisCopy.add( copy );
        }
        return eoisCopy;
    }

    @Override
    public boolean isLocalAndEffective( ElementOfInformation eoi ) {
        return isLocalEoi( eoi ) && !isOverridden( eoi, getInheritedEois() );
    }

    @Override
    public boolean isLocalEoi( ElementOfInformation eoi ) {
        return eois.contains( eoi );
    }

    /**
     * Whether all eois have the same classifications.
     *
     * @return a boolean
     */
    public boolean areAllEOIClassificationsSame() {
        // No eoi has classifications different from those of another eoi.
        final List<ElementOfInformation> list = getEffectiveEois();
        return !CollectionUtils.exists(
                list,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        final ElementOfInformation eoi = (ElementOfInformation) obj;
                        return CollectionUtils.exists(
                                list,
                                new Predicate() {
                                    public boolean evaluate( Object obj ) {
                                        return !CollectionUtils.isEqualCollection(
                                                eoi.getClassifications(),
                                                ( (ElementOfInformation) obj ).getClassifications()
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }

    /**
     * Get all classifications used in eois.
     *
     * @return a list of classifications
     */
    @SuppressWarnings("unchecked")
    public List<Classification> getAllEOIClassifications() {
        Set<Classification> allClassifications = new HashSet<Classification>();
        for ( ElementOfInformation eoi : getEffectiveEois() ) {
            allClassifications.addAll( eoi.getClassifications() );
        }
        return new ArrayList<Classification>( allClassifications );
    }

    /**
     * Whether flow is external.
     *
     * @return a boolean
     */
    public boolean isExternal() {
        return !isInternal();
    }

    /**
     * Whether the need is satisfied, even if only partially.
     *
     * @return a boolean
     */
    public boolean isSatisfied() {
        if ( isNeed() ) {
            for ( Iterator<Flow> it = getTarget().receives(); it.hasNext(); ) {
                Flow flow = it.next();
                if ( flow.isSharing() && Matcher.same( getName(), flow.getName() ) )
                    return true;
            }
        }
        return false;
    }

    /**
     * Whether the capability is used.
     *
     * @return a boolean
     */
    public boolean isSatisfying() {
        return isCapability() && ( (Connector) getTarget() ).externalFlows().hasNext()
                ||
                CollectionUtils.exists(
                        IteratorUtils.toList( getSource().sends() ),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                Flow flow = (Flow) obj;
                                return flow.isSharing() && Matcher.same( getName(),
                                        flow.getName() );
                            }
                        }
                );
    }

    public boolean references( final ModelObject mo ) {
        return CollectionUtils.exists(
                channels,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Channel) obj ).references( mo );
                    }
                } )
                || ( infoProduct != null && infoProduct.equals( mo ) );
    }

    /**
     * A flow is important (i.e. could be essential) if it is a sharing flow, and is either critical
     * to the target part or triggers it.
     *
     * @return a boolean
     */
    public boolean isImportant() {
        return isSharing()
                && !isIfTaskFails()
                && ( isCritical() || isTriggeringToTarget() );
    }

    /**
     * Flow has part as source or target.
     *
     * @param part a part
     * @return a boolean
     */
    public abstract boolean hasPart( Part part );

    /**
     * Get a copy of the elements of information in a flow.
     *
     * @return a list of elements of information
     */
    public List<ElementOfInformation> copyEois() {
        List<ElementOfInformation> copy = new ArrayList<ElementOfInformation>();
        if ( !isStandardized() ) {
            for ( ElementOfInformation eoi : eois ) {
                copy.add( new ElementOfInformation( eoi ) );
            }
        }
        return copy;
    }

    public String getRestrictionString( boolean isSend ) {
        if ( getRestrictions().isEmpty() ) {
            return "";
        } else {
            return ChannelsUtils.listToString( getRestrictionLabels( isSend ), ", ", " and " );
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getRestrictionLabels( final boolean isSend ) {
        return (List<String>) CollectionUtils.collect(
                getRestrictions(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Restriction) input ).getLabel( isSend );
                    }
                }
        );
    }

    /**
     * Whether the flow has an EOI of a given name (case insensitive).
     *
     * @param content a string
     * @return a boolean
     */
    public boolean hasEoiNamed( final String content ) {
        return CollectionUtils.exists(
                getEffectiveEois(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return Matcher.same(
                                ( (ElementOfInformation) object ).getContent(),
                                content );
                    }
                }
        );
    }

    /**
     * Find all subjects in this flow.
     *
     * @return a list of subjects
     */
    public List<Subject> getAllSubjects() {
        Set<Subject> subjects = new HashSet<Subject>();
        for ( ElementOfInformation eoi : getEffectiveEois() ) {
            Subject subject = new Subject( getName(), eoi.getContent() );
            subjects.add( subject );
        }
        List<Subject> results = new ArrayList<Subject>();
        results.addAll( subjects );
        Collections.sort( results );
        return results;
    }

    /**
     * Get the nature of the flow.
     *
     * @return a string
     */
    public String getNature() {
        return isSharing() ? "Flow" : isNeed() ? "Need" : "Capability";
    }

    /**
     * Get a copy of the list of channels.
     *
     * @return a list of channels
     */
    public List<Channel> copyChannels() {
        List<Channel> copy = new ArrayList<Channel>();
        for ( Channel channel : getChannels() ) {
            copy.add( new Channel( channel ) );
        }
        return copy;
    }

    /**
     * Other flow encompasses this one.
     *
     * @param other  a flow
     * @param locale the plan's locale
     * @return a boolean
     */
    public boolean matchesInfoOf( Flow other, Place locale ) {
        return Matcher.same( getName(), other.getName() )
                && getSegment().impliesEventPhaseAndContextOf( other.getSegment(), locale )
                && restrictionsImply( getRestrictions(), other.getRestrictions() );
    }

    private boolean restrictionsImply( List<Restriction> restrictions, List<Restriction> otherRestrictions ) {
        if ( restrictions.isEmpty() ) return true;
        if ( otherRestrictions.isEmpty() ) return false;
        boolean implied = true;
        for ( final Restriction restriction : restrictions ) {
            implied = implied && CollectionUtils.exists(
                    otherRestrictions,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return Restriction.implies( restriction, (Restriction) object );
                        }
                    }
            );
        }
        return implied;
    }

    /**
     * Whether this overrides another flow.
     *
     * @param other  a flow
     * @param locale a place
     * @return a boolean
     */
    public boolean overrides( Flow other, Place locale ) {
        if ( !equals( other ) && isSharing() && other.isSharing()
                && matchesInfoOf( other, locale ) ) {
            Part source = (Part) getSource();
            Part target = (Part) getTarget();
            Part otherSource = (Part) other.getSource();
            Part otherTarget = (Part) other.getTarget();
            return !( source.equals( otherSource ) )
                    && target.overridesOrEquals( otherTarget, locale )
                    && source.overridesOrEquals( otherSource, locale );
        } else
            return false;
    }

    public boolean hasEffectiveEoiNamedExactly( final String content ) {
        return CollectionUtils.exists(
                getEffectiveEois(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (ElementOfInformation) object ).getContent().equals( content );
                    }
                }
        );
    }

    public boolean isToSelf() {
        return restrictions.contains( Restriction.Self );
    }

    public boolean isTimeSensitive() {
        return getEffectiveEois().isEmpty()
                || CollectionUtils.exists( getEffectiveEois(), PredicateUtils.invokerPredicate( "isTimeSensitive" ) );
    }

    public boolean isTimeSensitive( final String eoiContent ) {
        return CollectionUtils.exists(
                getEffectiveEois(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        ElementOfInformation eoi = (ElementOfInformation) object;
                        return eoi.isTimeSensitive() && Matcher.same( eoi.getContent(), eoiContent );
                    }
                }
        );
    }

    public List<TransmissionMedium> transmissionMedia() {
        Set<TransmissionMedium> media = new HashSet<TransmissionMedium>();
        for ( Channel channel : getEffectiveChannels() ) {
            media.add( channel.getMedium() );
        }
        return new ArrayList<TransmissionMedium>( media );
    }

    /**
     * Get the list of all parts this flow's intermediated targets.
     *
     * @return a list of parts
     */
    public List<Part> intermediatedTargets() {
        Set<Part> intermediatedTargets = new HashSet<Part>();
        if ( isSharing() ) {
            if ( isAskedFor() || isCanBypassIntermediate() ) {  // requesters can bypass, notifiers can bypass
                Part target = (Part) getTarget();
                for ( Flow f : target.getAllSharingSends() ) {
                    if ( isNotification() && f.isNotification() || isAskedFor() && f.isAskedFor() ) {
                        if ( f.isNotification() || f.isCanBypassIntermediate() ) {  // requesters can bypass, notifiers can bypass
                            if ( this.containsAsMuchAs( f ) ) {
                                intermediatedTargets.add( (Part) f.getTarget() );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<Part>( intermediatedTargets );
    }


    /**
     * Get the list of all parts this flow's source intermediates.
     *
     * @return a list of parts
     */
    public List<Part> intermediatedSources() {
        Set<Part> intermediatedSources = new HashSet<Part>();
        if ( isSharing() ) {
            if ( isNotification() || isCanBypassIntermediate() ) {  // requesters can bypass, notifiers can bypass
                Part source = (Part) getSource();
                for ( Flow f : source.getAllSharingReceives() ) {
                    if ( isNotification() && f.isNotification() || isAskedFor() && f.isAskedFor() ) {
                        if ( f.isAskedFor() || f.isCanBypassIntermediate() ) {  // requesters can bypass, notifiers can bypass
                            if ( this.containsAsMuchAs( f ) ) {
                                intermediatedSources.add( (Part) f.getSource() );
                            }
                        }
                    }
                }
            }
        }

        return new ArrayList<Part>( intermediatedSources );
    }

    public boolean containsAsMuchAs( final Flow flow ) {
        final List<String> eoiContents = getEoiContents();
        return Matcher.same( getName(), flow.getName() )
                // no EOI from flow is missing in this
                && ( flow.getEffectiveEois().isEmpty()
                || ( !CollectionUtils.exists(
                flow.getEoiContents(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !eoiContents.contains( (String) object );
                    }
                } ) ) );
    }

    private List<String> getEoiContents() {
        List<String> contents = new ArrayList<String>();
        for ( ElementOfInformation eoi : getEffectiveEois() ) {
            contents.add( eoi.getContent().toLowerCase() );
        }
        return contents;
    }

    @Override
    public Map<String, Object> mapState() {
        Map<String, Object> state = super.mapState();
        state.put( "standardized", isStandardized() );
        state.put( "eois", copyEois() );
        state.put( "askedFor", isAskedFor() );
        state.put( "all", isAll() );
        state.put( "maxDelay", getMaxDelay().copy() );
        state.put( "channels", getChannelsCopy() );
        state.put( "significanceToTarget", getSignificanceToTarget() );
        state.put( "significanceToSource", getSignificanceToSource() );
        state.put( "intent", getIntent() );
        state.put( "restrictions", new ArrayList<Restriction>( getRestrictions() ) );
        state.put( "prohibited", isProhibited() );
        state.put( "published", isPublished() );
        state.put( "ifTaskFails", isIfTaskFails() );
        state.put( "referencesEventPhase", isReferencesEventPhase() );
        state.put( "canBypassIntermediate", isCanBypassIntermediate() );
        state.put( "receiptConfirmationRequested", isReceiptConfirmationRequested() );
        return state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initFromMap( Map<String, Object> state, CommunityService communityService ) {
        super.initFromMap( state, communityService );
        if ( state.containsKey( "standardized" ) )
            setStandardized( (Boolean) state.get( "standardized" ) );
        if ( !isStandardized() && state.containsKey( "eois" ) )
            setEois( (List<ElementOfInformation>) state.get( "eois" ) );
        if ( isStandardized() ) {
            setProductInfoFromName( communityService.getDao() );
        }
        if ( state.containsKey( "askedFor" ) )
            setAskedFor( (Boolean) state.get( "askedFor" ) );
        if ( state.containsKey( "all" ) )
            setAll( (Boolean) state.get( "all" ) );
        if ( state.containsKey( "maxDelay" ) )
            setMaxDelay( (Delay) state.get( "maxDelay" ) );
        if ( state.containsKey( "channels" ) )
            setChannels( (List<Channel>) state.get( "channels" ) );
        if ( state.containsKey( "significanceToTarget" ) )
            setSignificanceToTarget( (Significance) state.get( "significanceToTarget" ) );
        if ( state.containsKey( "significanceToSource" ) )
            setSignificanceToSource( (Significance) state.get( "significanceToSource" ) );
        if ( state.containsKey( "intent" ) )
            setIntent( (Intent) state.get( "intent" ) );
        if ( state.containsKey( "prohibited" ) )
            setProhibited( (Boolean) state.get( "prohibited" ) );
        if ( state.containsKey( "published" ) )
            setPublished( (Boolean) state.get( "published" ) );
        if ( state.containsKey( "restrictions" ) )
            setRestrictions( (List<Restriction>) state.get( "restrictions" ) );
        if ( state.containsKey( "ifTaskFails" ) )
            setIfTaskFails( (Boolean) state.get( "ifTaskFails" ) );
        if ( state.containsKey( "referencesEventPhase" ) )
            setReferencesEventPhase( (Boolean) state.get( "referencesEventPhase" ) );
        if ( state.containsKey( "canBypassIntermediate" ) )
            setCanBypassIntermediate( (Boolean) state.get( "canBypassIntermediate" ) );
        if ( state.containsKey( "receiptConfirmationRequested" ) )
            setReceiptConfirmationRequested( (Boolean) state.get( "receiptConfirmationRequested" ) );
    }

    public void setProductInfoFromName( AbstractModelObjectDao dao ) {
        assert isStandardized();
        if ( !getName().isEmpty() ) {
            infoProduct = dao.findOrCreateType( InfoProduct.class, getName(), null );
        }
    }

    /// EOIHolder


    @Override
    public boolean isClassificationsAccessible() {
        return !isNeed();
    }

    @Override
    public boolean isSpecialHandlingChangeable() {
        return !isNeed();
    }

    @Override
    public boolean isDescriptionChangeable() {
        return !isNeed();
    }

    @Override
    public boolean canSetTimeSensitivity() {
        return isNeed();
    }

    @Override
    public String getEOIHolderLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append( "In " );
        sb.append( isNeed()
                ? "need for "
                : isCapability()
                ? "availability of "
                : "sharing of " );
        sb.append( getName() );
        sb.append( isNeed()
                ? ""
                : isCapability()
                ? " from " + "\"" + getSource().getTitle() + "\""
                : " by " + "\"" + getSource().getTitle() + "\"" );
        sb.append( isCapability()
                ? ""
                : isNeed()
                ? " by " + "\"" + getTarget().getTitle() + "\""
                : " with " + "\"" + getTarget().getTitle() + "\"" );
        return sb.toString();
    }

    @Override
    public boolean canSetElements() {
        return canSetNameAndElements();
    }

    @Override
    public boolean isFlow() {
        return true;
    }

    /**
     * Whether this flow has the same name, significance, and all the EOis of the other flow.
     *
     * @param other  another flow
     * @param isSend whether the flow is looked at as a receive
     * @return a boolean
     */
    public boolean isAlternativeSharingTo( Flow other, boolean isSend, QueryService queryService ) {
        return !equals( other )
                && isSharing() && other.isSharing()
                && Matcher.same( getName(), other.getName() )
                && ( isSend && getSignificanceToSource() == other.getSignificanceToSource()
                || !isSend && getSignificanceToTarget() == other.getSignificanceToTarget() )
                && queryService.subsetOf( other.getEffectiveEois(), getEffectiveEois() );
    }

    public boolean isRestricted() {
        return !getRestrictions().isEmpty();
    }

    @SuppressWarnings("unchecked")
    public List<String> getEffectiveEoiNames() {
        return (List<String>) CollectionUtils.collect(
                getEffectiveEois(),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (ElementOfInformation) input ).getContent();
                    }
                }
        );
    }

    public ResourceSpec getSourceResourceSpec() {
        Node source = getSource();
        if ( source.isPart() ) {
            return ( (Part) source ).resourceSpec();
        } else {
            return new ResourceSpec();
        }
    }

    public ResourceSpec getTargetResourceSpec() {
        Node target = getTarget();
        if ( target.isPart() ) {
            return ( (Part) target ).resourceSpec();
        } else {
            return new ResourceSpec();
        }
    }


    /**
     * The significance of a flow.
     */
    public enum Significance {
        None( "none" ),
        Useful( "is useful to" ),
        Critical( "is critical to" ),
        Terminates( "terminates" ),
        Triggers( "triggers" );

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
        public List<Significance> getChoices() {
            return Arrays.asList( values() );
        }

        public String toString() {
            return name();
        }

        /**
         * Return the most significant of two significances.
         *
         * @param s1 a significance
         * @param s2 a significance
         * @return a significance
         */
        public static Significance max( Significance s1, Significance s2 ) {
            return s1.ordinal() > s2.ordinal()
                    ? s1
                    : s1.ordinal() < s2.ordinal()
                    ? s2
                    : s1;
        }
    }

    /**
     * Intent of flows.
     */
    public enum Intent {

        Alarm,
        Announcement,
        Command,
        Expertise,
        Feedback,
        Report;

        public String getLabel() {
            return name();
        }

        public String getHint() {
            switch ( this ) {
                case Report:
                    return "Summary of facts and conjectures: \"Here's what we know...\"";
                case Command:
                    return "An injunction: \"Do this!\" \"Stop that!\"";
                case Announcement:
                    return "Statement, communiqué, advertisement...";
                case Feedback:
                    return "Thumbs up or down, evaluation, comment...";
                case Alarm:
                    return "Signal of changes requiring outside intervention";
                case Expertise:
                    return "Expert knowledge or opinion";
                default:
                    return name();
            }
        }

        public static List<String> getAllLabels() {
            List<String> labels = new ArrayList<String>();
            for ( Intent intent : Intent.values() ) {
                labels.add( intent.getLabel() );
            }
            Collections.sort( labels );
            return labels;
        }

        public static Intent valueOfLabel( String label ) {
            for ( Intent intent : Intent.values() ) {
                if ( intent.getLabel().equals( label ) ) return intent;
            }
            return null;
        }

        public static boolean same( Intent intent, Intent other ) {
            return intent != null && other != null & intent.equals( other );
        }
    }

    /**
     * Restriction on implied sharing commitments set only by the source of the information.
     */
    public enum Restriction {

        SameTopOrganization,
        SameOrganization,
        SameLocation,
        DifferentOrganizations,
        DifferentTopOrganizations,
        DifferentLocations,
        Supervisor,
        Supervised,
        Self,
        Other;

        public String toString() {
            switch ( this ) {
                case SameTopOrganization:
                    return "the same overall organization";
                case SameOrganization:
                    return "the same organization";
                case SameLocation:
                    return "the same location";
                case DifferentOrganizations:
                    return "different organizations";
                case DifferentTopOrganizations:
                    return "different overall organizations";
                case DifferentLocations:
                    return "different locations";
                case Supervisor:
                    return "a supervisor";
                case Supervised:
                    return "a supervised";
                case Self:
                    return "self";
                case Other:
                    return "someone else";
                default:
                    return name();
            }
        }

        public String getLabel() {
            return getLabel( true );
        }

        public String getLabel( boolean isSend ) {
            if ( this == Supervisor || this == Supervised || this == Self || this == Other ) {
                StringBuilder sb = new StringBuilder();
                sb.append( isSend ? "to " : "from " );
                if ( this == Supervisor )
                    sb.append( isSend ? Supervisor.toString() : Supervised.toString() );
                else if ( this == Supervised )
                    sb.append( isSend ? Supervised.toString() : Supervisor.toString() );
                else sb.append( this.toString() );
                return sb.toString();
            } else {
                return "in " + toString();
            }
        }

        public static List<String> getAllLabels( boolean isSend ) {
            List<String> labels = new ArrayList<String>();
            for ( Restriction restriction : Restriction.values() ) {
                labels.add( restriction.getLabel( isSend ) );
            }
            Collections.sort( labels );
            return labels;
        }

        public static Restriction valueOfLabel( String label, boolean isSend ) {
            for ( Restriction restriction : Restriction.values() ) {
                if ( restriction.getLabel( isSend ).equals( label ) ) return restriction;
            }
            return null;
        }

        /**
         * Resolve to most constraining if compatible, else null.
         *
         * @param restriction a restriction
         * @param other       a restriction
         * @return a restriction or null
         */
        public static Restriction resolve( Restriction restriction, Restriction other ) {
            if ( restriction == null ) return other;
            if ( other == null ) return restriction;
            if ( restriction == other ) return restriction;
            if ( restriction == Self || other == Self ) return Self;
            if ( other == SameTopOrganization && restriction == SameOrganization )
                return SameOrganization;
            if ( restriction == DifferentTopOrganizations && other == DifferentOrganizations )
                return DifferentOrganizations;
            if ( other == DifferentTopOrganizations && restriction == DifferentOrganizations )
                return DifferentOrganizations;
            return null;
        }

        /**
         * Add to primary restrictions the secondary restrictions that neither contradict nor imply a primary.
         *
         * @param primaryRestrictions   primary restrictions
         * @param secondaryRestrictions secondary restrictions
         * @return a list of restrictions
         */
        public static List<Restriction> resolve( List<Restriction> primaryRestrictions,
                                                 List<Restriction> secondaryRestrictions ) {
            Set<Restriction> resolved = new HashSet<Restriction>( primaryRestrictions );
            for ( final Restriction secondary : secondaryRestrictions ) {
                if ( primaryRestrictions.isEmpty() || !CollectionUtils.exists(
                        primaryRestrictions,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Restriction primary = (Restriction) object;
                                return secondary.contradicts( primary )
                                        || Restriction.implies( secondary, primary );
                            }
                        }
                ) ) {
                    resolved.add( secondary );
                }
            }
            return new ArrayList<Restriction>( resolved );
        }

        /**
         * Does a restriction imply another (null means no restriction)?
         * I.e. does the restriction narrow the other?
         *
         * @param restriction a restriction or null
         * @param other       a restriction or null
         * @return a boolean
         */
        public static boolean implies( Restriction restriction, Restriction other ) {
            return restriction == null && other == null
                    || restriction == other
                    || SameOrganization == restriction && SameTopOrganization == other
                    || DifferentTopOrganizations == restriction && DifferentOrganizations == other;
        }


        public static boolean same( Restriction restriction, Restriction other ) {
            return restriction == null && other == null
                    || restriction != null && other != null && restriction.equals( other );
        }

        public boolean contradicts( Restriction restriction ) {
            switch ( this ) {
                case SameTopOrganization:
                    return restriction == DifferentTopOrganizations;
                case SameOrganization:
                    return restriction == DifferentOrganizations || restriction == DifferentTopOrganizations;
                case SameLocation:
                    return restriction == DifferentLocations;
                case DifferentOrganizations:
                    return restriction == SameOrganization;
                case DifferentTopOrganizations:
                    return restriction == SameOrganization || restriction == SameTopOrganization;
                case DifferentLocations:
                    return restriction == SameLocation;
                case Supervisor:
                    return restriction == Self || restriction == Supervised;
                case Supervised:
                    return restriction == Self || restriction == Supervisor;
                case Self:
                    return restriction == Other || restriction == Supervised || restriction == Supervisor
                            || restriction == DifferentLocations || restriction == DifferentOrganizations
                            || restriction == DifferentTopOrganizations;
                case Other:
                    return restriction == Self;
                default:
                    return false;

            }
        }

        public static boolean compatible( List<Restriction> restrictions, final List<Restriction> otherRestrictions ) {
            return restrictions.isEmpty() ||
                    otherRestrictions.isEmpty() ||
                    !CollectionUtils.exists(
                            restrictions,
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    final Restriction restriction = (Restriction) object;
                                    return CollectionUtils.exists(
                                            otherRestrictions,
                                            new Predicate() {
                                                @Override
                                                public boolean evaluate( Object object ) {
                                                    return ( (Restriction) object ).contradicts( restriction );
                                                }
                                            }
                                    );
                                }
                            }
                    );
        }

        public Restriction inverse() {
            switch ( this ) {
                case Supervisor:
                    return Supervised;
                case Supervised:
                    return Supervisor;
                default:
                    return this;
            }
        }

        // All need restrictions are implied by capability restrictions and none are contradicted.
        public static boolean satisfy( final List<Restriction> capabilityRestrictions, List<Restriction> needRestrictions ) {
            boolean noCapabilityRestrictionIncompatible = !CollectionUtils.exists(
                    needRestrictions,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            final Restriction needRestriction = (Restriction) object;
                            return CollectionUtils.exists(
                                    capabilityRestrictions,
                                    new Predicate() {
                                        @Override
                                        public boolean evaluate( Object object ) {
                                            Restriction capabilityRestriction = (Restriction) object;
                                            return capabilityRestriction.contradicts( needRestriction );
                                        }
                                    }
                            );
                        }
                    }
            );
            if ( !noCapabilityRestrictionIncompatible ) return false;
            boolean allNeedRestrictionsImplied = !CollectionUtils.exists(
                    needRestrictions,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            final Restriction needRestriction = (Restriction) object;
                            return !CollectionUtils.exists(
                                    capabilityRestrictions,
                                    new Predicate() {
                                        @Override
                                        public boolean evaluate( Object object ) {
                                            Restriction capabilityRestriction = (Restriction) object;
                                            return Restriction.implies( capabilityRestriction, needRestriction );
                                        }
                                    }
                            );
                        }
                    }
            );
            return allNeedRestrictionsImplied;
        }
    }

}
