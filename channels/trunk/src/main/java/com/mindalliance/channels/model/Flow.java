package com.mindalliance.channels.model;

import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An arrow between two nodes in the information flow graph.
 */
public abstract class Flow extends ModelObject implements Channelable, SegmentObject, Operationable {

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
     * Restriction on implied sharing commitments.
     */
    private Restriction restriction;
    /**
     * Flow applies only if task fails. (Send only)
     */
    private boolean ifTaskFails;

    /**
     * Whether operational.
     */
    private boolean operational = true;

    protected Flow() {
    }

    /**
     * {@inheritDoc}
     */
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
    public void addChannel( TransmissionMedium medium, String address ) {
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
        return eois;
    }

    public void setEois( List<ElementOfInformation> elements ) {
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
                eois,
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
    public void addEoi( ElementOfInformation eoi ) {
        if ( !eois.contains( eoi ) ) {
            if ( isNeed() ) {
                eoi.retainContentOnly();
            }
            eois.add( eoi );
        }
    }

    public Significance getSignificanceToSource() {
        return significanceToSource;
    }

    public void setSignificanceToSource( Significance significance ) {
        significanceToSource = significance;
    }

    public Significance getSignificanceToTarget() {
        return significanceToTarget;
    }

    public void setSignificanceToTarget( Significance significanceToTarget ) {
        this.significanceToTarget = significanceToTarget;
    }

    public boolean isOperational() {
        return operational;
    }

    public void setOperational( boolean operational ) {
        this.operational = operational;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEffectivelyOperational() {
        return isOperational()
                &&
                ( isNeed() && ( (Part) getTarget() ).isOperational()
                        || isCapability() && ( (Part) getSource() ).isOperational()
                        || isOperationalizable() );
    }

    public boolean canGetOperational() {
        return isOperationalizable();
    }

    public boolean canSetOperational() {
        return isOperationalizable();
    }

    public boolean isOperationalizable() {
        return isSharing()
                && ( (Part) getSource() ).isOperational()
                && ( (Part) getTarget() ).isOperational();
    }

    public String getShortName( Node node, boolean qualified ) {
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
     * Provide a description of the flow, when viewed as a receive.
     *
     * @return the description
     */
    public String getReceiveTitle() {
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = /*!isAskedFor() && isTriggeringToTarget() ? "do something" :*/ "something";
        if ( getIntent() != null ) {
            message += " (" + getIntent().getLabel().toLowerCase() + ")";
        }
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
        String message = getName();
        if ( message == null || message.trim().isEmpty() )
            message = "something";
        if ( getIntent() != null ) {
            message += " (" + getIntent().getLabel().toLowerCase() + ")";
        }
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
     * @see com.mindalliance.channels.query.QueryService#connect(Node, Node, String)
     */
    abstract void setSource( Node source );

    /**
     * Set the target of this flow.
     * Note: this method should not be called directly.
     *
     * @param target the target node.
     * @see com.mindalliance.channels.query.QueryService#connect(Node, Node, String)
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

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction( Restriction restriction ) {
        this.restriction = restriction;
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
     * Initialize relevant properties as a capability satisfying a need.
     *
     * @param capability   a flow
     * @param need         a flow
     * @param queryService a query service
     */
    public void initSharingFrom( Flow capability, Flow need, QueryService queryService ) {
        setEois( queryService.findCommonEOIs( capability, need ) );
        setSignificanceToSource( capability.getSignificanceToSource() );
        setSignificanceToTarget( need.getSignificanceToTarget() );
        setChannels( Channel.intersect(
                capability.getChannels(),
                need.getChannels(), queryService.getPlan().getLocale() ) );
        setMaxDelay( Delay.min( capability.getMaxDelay(), need.getMaxDelay() ) );
        setIntent( capability.getIntent() != null ? capability.getIntent() : need.getIntent() );
        setRestriction( Flow.Restriction.resolve(
                need.getRestriction(),
                capability.getRestriction() ) );
        setIfTaskFails( capability.isIfTaskFails() );
    }

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

    /**
     * {@inheritDoc }
     */
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
        TransmissionMedium medium = channel.getMedium();
        if ( medium == null || medium.isUnknown() ) {
            return "The medium is undefined";
        } else {
            return null;
        }
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
        if ( source.isPart() && source.getSegment() == getSegment() ) {
            return (Part) source;
        } else {
            Node target = getTarget();
            if ( target.isPart() && target.getSegment() == getSegment() ) {
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

    /**
     * {@inheritDoc}
     */
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
    public List<Classification> getClassifications() {
        Set<Classification> classifications = new HashSet<Classification>();
        for ( ElementOfInformation eoi : eois ) {
            classifications.addAll( eoi.getClassifications() );
        }
        return new ArrayList<Classification>( classifications );
    }

    /**
     * Generate a copy of the eois but each with the union of their classifications.
     *
     * @return a list of elements of information
     */
    @SuppressWarnings( "unchecked" )
    public List<ElementOfInformation> getEOISWithSameClassifications() {
        List<Classification> allClassifications = getAllEOIClassifications();
        List<ElementOfInformation> eoisCopy = new ArrayList<ElementOfInformation>();
        for ( ElementOfInformation eoi : eois ) {
            ElementOfInformation copy = new ElementOfInformation();
            copy.setContent( eoi.getContent() );
            copy.setDescription( eoi.getDescription() );
            copy.setSpecialHandling( eoi.getSpecialHandling() );
            copy.setClassifications( new ArrayList<Classification>( allClassifications ) );
            eoisCopy.add( copy );
        }
        return eoisCopy;
    }

    /**
     * Whether all eois have the same classifications.
     *
     * @return a boolean
     */
    public boolean areAllEOIClassificationsSame() {
        // No eoi has classifications different from those of another eoi.
        return !CollectionUtils.exists(
                eois,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        final ElementOfInformation eoi = (ElementOfInformation) obj;
                        return CollectionUtils.exists(
                                eois,
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
    @SuppressWarnings( "unchecked" )
    public List<Classification> getAllEOIClassifications() {
        Set<Classification> allClassifications = new HashSet<Classification>();
        for ( ElementOfInformation eoi : eois ) {
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
        assert isNeed();
        for ( Iterator<Flow> it = getTarget().receives(); it.hasNext(); ) {
            Flow flow = it.next();
            if ( flow.isSharing() && Matcher.getInstance().same( getName(), flow.getName() ) )
                return true;
        }
        return false;
    }

    /**
     * Whether the capability is used.
     *
     * @return a boolean
     */
    public boolean isSatisfying() {
        assert isCapability();
        return ( (Connector) getTarget() ).externalFlows().hasNext()
                ||
                CollectionUtils.exists(
                        IteratorUtils.toList( getSource().sends() ),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                Flow flow = (Flow) obj;
                                return flow.isSharing() && Matcher.getInstance().same( getName(),
                                        flow.getName() );
                            }
                        }
                );
    }

    /**
     * {@inheritDoc}
     */
    public boolean references( final ModelObject mo ) {
        return CollectionUtils.exists(
                channels,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Channel) obj ).references( mo );
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    public List<Flow> getEssentialFlows( boolean assumeFails, QueryService queryService ) {
        if ( isEssential( assumeFails, queryService ) ) {
            return new ArrayList<Flow>( getTarget().getEssentialFlows( assumeFails, queryService ) );
        } else {
            return new ArrayList<Flow>();
        }
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
     * Whether the flow could be essential to risk mitigation.
     *
     * @param assumeFails  whether alternate flows are assumed
     * @param queryService a query service
     * @return a boolean
     */
    public boolean isEssential( boolean assumeFails, QueryService queryService ) {
        return isImportant()
                && ( assumeFails || queryService.getAlternates( this ).isEmpty() )
                && !isSharingWithSelf( queryService );
    }

    /**
     * Whether this is a sharing flow where source actor is target actor.
     *
     * @param queryService a query service
     * @return a boolean
     */
    public boolean isSharingWithSelf( QueryService queryService ) {
        boolean sharingWithSelf = false;
        if ( isSharing() ) {
            Part sourcePart = (Part) getSource();
            Part targetPart = (Part) getTarget();
            Actor onlySource = sourcePart.getKnownActualActor( queryService );
            if ( onlySource != null ) {
                Actor onlyTarget = targetPart.getKnownActualActor( queryService );
                if ( onlyTarget != null ) {
                    sharingWithSelf = onlySource.equals( onlyTarget );
                }
            }
        }
        return sharingWithSelf;
    }

    /**
     * Flow has part as source or target.
     *
     * @param part a part
     * @return a boolean
     */
    public abstract boolean hasPart( Part part );

    /**
     * Get CSS class for flow priority.
     *
     * @param queryService a query service
     * @return a string
     */
    public String getPriorityCssClass( QueryService queryService ) {
        if ( isSharing() ) {
            Level priority = queryService.computeSharingPriority( this );
            return priority.getNegativeLabel().toLowerCase();
        } else {
            return "none";
        }
    }

    /**
     * Get a copy of the elements fo information in a flow.
     *
     * @return a list of elements of information
     */
    public List<ElementOfInformation> copyEois() {
        return new ArrayList<ElementOfInformation>( eois );
    }

    /**
     * Whether flow is prohibited beacuse it has at least one forbidding policy attached.
     *
     * @return a boolean
     */
    public boolean isProhibited() {
        return CollectionUtils.exists(
                getAttachments(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        Attachment attachment = (Attachment) object;
                        return attachment.getType().equals( Attachment.Type.PolicyCant );
                    }
                }
        );
    }

    public String getRestrictionString( boolean isSend ) {
        if ( getRestriction() == null ) {
            return "";
        } else {
            return " if " + getRestriction().getLabel( isSend );
        }
    }

    /**
     * Whether ths flow has an EOI of a given name (case insensitive).
     *
     * @param content a string
     * @return a boolean
     */
    public boolean hasEoiNamed( final String content ) {
        return CollectionUtils.exists(
                getEois(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return Matcher.getInstance().same(
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
        for ( ElementOfInformation eoi : getEois() ) {
            Subject subject = new Subject( getName(), eoi.getContent() );
            subjects.add( subject );
        }
        List<Subject> results = new ArrayList<Subject>();
        results.addAll( subjects );
        Collections.sort( results );
        return results;
    }

    public boolean allowsCommitment(
            Assignment committer,
            Assignment beneficiary,
            Place locale,
            QueryService queryService ) {

        if ( restriction != null ) {
            Organization committerOrg = committer.getOrganization();
            Organization beneficiaryOrg = beneficiary.getOrganization();
            Place committerLocation = committer.getLocation();
            Place beneficiaryLocation = beneficiary.getLocation();
            switch ( restriction ) {
                case SameTopOrganization:
                    return committerOrg == null
                            || beneficiaryOrg == null
                            || committerOrg.getTopOrganization()
                            .equals( beneficiaryOrg.getTopOrganization() );

                case SameOrganization:
                    return committerOrg == null
                            || beneficiaryOrg == null
                            || committerOrg.narrowsOrEquals( beneficiaryOrg, locale )
                            || beneficiaryOrg.narrowsOrEquals( committerOrg, locale );

                case DifferentOrganizations:
                    return committerOrg == null
                            || beneficiaryOrg == null
                            || ( !committerOrg.narrowsOrEquals( beneficiaryOrg, locale )
                            && !beneficiaryOrg.narrowsOrEquals( committerOrg, locale ) );

                case DifferentTopOrganizations:
                    return committerOrg == null
                            || beneficiaryOrg == null
                            || !committerOrg.getTopOrganization()
                            .equals( beneficiaryOrg.getTopOrganization() );

                case SameLocation:
                    return committerLocation == null
                            || beneficiaryLocation == null
                            || committerLocation.narrowsOrEquals( beneficiaryLocation, locale )
                            || beneficiaryLocation.narrowsOrEquals( committerLocation, locale );

                case DifferentLocations:
                    return committerLocation == null
                            || beneficiaryLocation == null
                            || ( !committerLocation.narrowsOrEquals( beneficiaryLocation, locale )
                            && !beneficiaryLocation.narrowsOrEquals( committerLocation, locale ) );

                case Supervisor:
                    return committer.getActor() == null
                            || beneficiary.getActor() == null
                            || queryService.isSupervisorOf( beneficiary.getActor(), committer.getActor() );
            }
        }

        return true;
    }

    /**
     * Get the nature of the flow.
     *
     * @return a string
     */
    public String getNature() {
        return isSharing() ? "Flow" : isNeed() ? "Need" : "Capability";
    }

    public String getOperationalLabel() {
        return isEffectivelyOperational() ? "Yes" : "No";
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

        /**
         * {@inheritDoc}
         */
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
     * Restriction on implied sharing commitments.
     */
    public enum Restriction {

        SameTopOrganization,
        SameOrganization,
        SameLocation,
        DifferentOrganizations,
        DifferentTopOrganizations,
        DifferentLocations,
        Supervisor;

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
                default:
                    return name();
            }
        }

        public String getLabel() {
            return getLabel( true );
        }

        public String getLabel( boolean isSend ) {
            if ( this == Supervisor ) {
                return (isSend ? "to " : "from ") + toString();
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


        public static Restriction resolve( Restriction restriction, Restriction other ) {
            if ( restriction == null ) return other;
            if ( other == null ) return restriction;
            if ( restriction == other ) return restriction;
            if ( restriction == SameTopOrganization && other == SameOrganization ) return SameOrganization;
            if ( other == SameTopOrganization && restriction == SameOrganization ) return SameOrganization;
            if ( restriction == DifferentTopOrganizations && other == DifferentOrganizations ) return DifferentOrganizations;
            if ( other == DifferentTopOrganizations && restriction == DifferentOrganizations ) return DifferentOrganizations;
            return null;
        }

        /**
         * Is a restriction matched by another (null means no restriction)?
         *
         * @param restriction a restriction or null
         * @param other       a restriction or null
         * @return a boolean
         */
        public static boolean matchedBy( Restriction restriction, Restriction other ) {
            return restriction == null
                    || other == null
                    || restriction.equals( other )
                    || restriction == SameTopOrganization && other == SameOrganization;
        }


    }

}
