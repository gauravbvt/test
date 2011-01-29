package com.mindalliance.channels.util;

import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility functions.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 3:42:39 PM
 */
public final class ChannelsUtils {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChannelsUtils.class );

    private ChannelsUtils() {

    }

    /**
     * Captures the state of a flow minus the nodes
     *
     * @param flow a flow
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getFlowAttributes( final Flow flow ) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put( "description", flow.getDescription() );
        attributes.put( "tags", flow.getTags() );
        attributes.put( "eois", flow.copyEois() );
        attributes.put( "askedFor", flow.isAskedFor() );
        attributes.put( "all", flow.isAll() );
        attributes.put( "maxDelay", new Delay( flow.getMaxDelay() ) );
        attributes.put( "channels", flow.getChannelsCopy() );
        attributes.put( "attachments", new ArrayList<Attachment>( flow.getAttachments() ) );
        attributes.put( "waivedIssueDetections", new ArrayList<String>( flow.getWaivedIssueDetections() ) );
        attributes.put( "significanceToTarget", flow.getSignificanceToTarget() );
        attributes.put( "significanceToSource", flow.getSignificanceToSource() );
        attributes.put( "intent", flow.getIntent() );
        attributes.put( "restriction", flow.getRestriction() );
        attributes.put( "ifTaskFails", flow.isIfTaskFails() );
        return attributes;
    }

    @SuppressWarnings( "unchecked" )
    private static Map<String, Object> mergeFlowAttributes(
            Map<String, Object> attributes,
            Map<String, Object> others ) {
        Map<String, Object> merged = new HashMap<String, Object>();
        String desc1 = (String) attributes.get( "description" );
        String desc2 = (String) others.get( "description" );
        merged.put(
                "description",
                desc2.length() > desc1.length() ? desc2 : desc1 );
        merged.put( "tags", attributes.get( "tags" ) + Tag.SEPARATOR + others.get( "tags" ) );
        merged.put( "eois", aggregateEOIs(
                (List<ElementOfInformation>) attributes.get( "eois" ),
                (List<ElementOfInformation>) others.get( "eois" ) ) );
        if ( attributes.containsKey( "all" ) && others.containsKey( "all" ) ) {
            merged.put(
                    "all",
                    (Boolean) attributes.get( "all" ) || (Boolean) others.get( "all" ) );
        }
        merged.put(
                "maxDelay",
                ( (Delay) others.get( "maxDelay" ) ).shorterThan( (Delay) attributes.get( "maxDelay" ) )
                        ? others.get( "maxDelay" ) : attributes.get( "maxDelay" )
        );
        if ( attributes.containsKey( "channels" ) && others.containsKey( "channels" ) ) {
            merged.put(
                    "channels",
                    aggregateChannels(
                            (List<Channel>) attributes.get( "channels" ),
                            (List<Channel>) others.get( "channels" ) )
            );
        }
        merged.put(
                "attachments",
                aggregateAttachments(
                        (List<Attachment>) attributes.get( "attachments" ),
                        (List<Attachment>) others.get( "attachments" ) )
        );
        Set<String> waivers = new HashSet<String>( (List<String>) attributes.get( "waivedIssueDetections" ) );
        waivers.addAll( (List<String>) others.get( "waivedIssueDetections" ) );
        merged.put(
                "waivedIssueDetections",
                new ArrayList<String>( waivers ) );
        if ( attributes.containsKey( "significanceToTarget" ) && others.containsKey( "significanceToTarget" ) ) {
            Flow.Significance significance = Flow.Significance.max(
                    (Flow.Significance) attributes.get( "significanceToTarget" ),
                    (Flow.Significance) others.get( "significanceToTarget" ) );
            merged.put(
                    "significanceToTarget",
                    significance
            );
        }
        if ( attributes.containsKey( "significanceToSource" ) && others.containsKey( "significanceToSource" ) ) {
            Flow.Significance significance = Flow.Significance.max(
                    (Flow.Significance) attributes.get( "significanceToSource" ),
                    (Flow.Significance) others.get( "significanceToSource" ) );
            merged.put(
                    "significanceToSource",
                    significance
            );
        }
        // Make intent null (unknown) if there is a conflict.
        Flow.Intent intent1 = (Flow.Intent) attributes.get( "intent" );
        Flow.Intent intent2 = (Flow.Intent) others.get( "intent" );
        if ( Flow.Intent.same( intent1, intent2 ) ) {
            merged.put( "intent", intent1 );
        } else {
            merged.put( "intent", null );
        }
        // Make restriction null (unknown) if there is a conflict.
        merged.put( "restriction", Flow.Restriction.resolve(
                (Flow.Restriction) attributes.get( "restriction" ),
                (Flow.Restriction) others.get( "restriction" ) ) );
        // Merge is task failure flow
        merged.put(
                "ifTaskFails",
                (Boolean) attributes.get( "ifTaskFails" ) || (Boolean) others.get( "ifTaskFails" ) );
        return merged;
    }

    private static List<ElementOfInformation> aggregateEOIs(
            List<ElementOfInformation> eois,
            List<ElementOfInformation> others ) {
        List<ElementOfInformation> aggregate = new ArrayList<ElementOfInformation>();
        for ( ElementOfInformation eoi : eois ) {
            aggregate.add( new ElementOfInformation( eoi ) );
        }
        for ( final ElementOfInformation eoi : others ) {
            ElementOfInformation synonymous = (ElementOfInformation) CollectionUtils.find(
                    eois,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return Matcher.getInstance().same(
                                    eoi.getContent(),
                                    ( (ElementOfInformation) object ).getContent() );
                        }
                    }
            );
            if ( synonymous == null ) {
                aggregate.add( new ElementOfInformation( eoi ) );
            } else {
                aggregate.remove( synonymous );
                aggregate.add( ElementOfInformation.merge( synonymous, eoi ) );
            }
        }
        return aggregate;
    }

    private static List<Channel> aggregateChannels( List<Channel> channels, List<Channel> others ) {
        List<Channel> aggregate = new ArrayList<Channel>();
        for ( Channel channel : channels ) {
            aggregate.add( new Channel( channel ) );
        }
        for ( final Channel channel : others ) {
            Channel synonymous = (Channel) CollectionUtils.find(
                    channels,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return channel.getMedium().equals(
                                    ( (Channel) object ).getMedium() );
                        }
                    }
            );
            if ( synonymous == null ) {
                aggregate.add( new Channel( channel ) );
            } else {
                aggregate.remove( synonymous );
                aggregate.add( Channel.merge( synonymous, channel ) );
            }
        }
        return aggregate;
    }

    private static List<Attachment> aggregateAttachments( List<Attachment> attachments, List<Attachment> others ) {
        List<Attachment> aggregate = new ArrayList<Attachment>();
        for ( Attachment attachment : attachments ) {
            aggregate.add( new Attachment( attachment ) );
        }
        for ( final Attachment attachment : others ) {
            Attachment synonymous = (Attachment) CollectionUtils.find(
                    attachments,
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return attachment.getUrl().equals(
                                    ( (Attachment) object ).getUrl() );
                        }
                    }
            );
            if ( synonymous == null ) {
                aggregate.add( new Attachment( attachment ) );
            } else {
                aggregate.remove( synonymous );
                aggregate.add( Attachment.merge( synonymous, attachment ) );
            }
        }
        return aggregate;
    }


    /**
     * Captures the connection of a flow.
     *
     * @param flow a flow
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getFlowState( final Flow flow ) {
        Part part;
        if ( flow.isInternal() ) {
            part = flow.getSource().isPart() ? (Part) flow.getSource() : (Part) flow.getTarget();
        } else {
            part = ( (ExternalFlow) flow ).getPart();
        }
        return getFlowState( flow, part );
    }

    /**
     * Captures the connection of a flow from the perspective of a part.
     *
     * @param flow a flow
     * @param part a part
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getFlowState( final Flow flow, final Part part ) {
        final Node other;
        final boolean isSend;
        if ( flow.isInternal() ) {
            isSend = flow.getSource() == part;
            other = isSend ? flow.getTarget() : flow.getSource();
        } else {
            ExternalFlow externalFlow = (ExternalFlow) flow;
            isSend = !externalFlow.isPartTargeted();
            other = externalFlow.getConnector();
        }
        Map<String, Object> state = new HashMap<String, Object>();
        // state.put( "id", flow.getId() );
        state.put( "name", flow.getName() );
        state.put( "tags", Tag.tagsToString( flow.getTags() ) );
        state.put( "isSend", isSend );
        state.put( "segment", part.getSegment().getId() );
        state.put( "part", part.getId() );
        state.put( "otherSegment", other.getSegment().getId() );
        if ( other.isConnector() ) {
            // Remember the connector's inner flow if external (its id is persistent, if mapped)
            if ( part.getSegment() != other.getSegment() ) {
                state.put( "other", ( (Connector) other ).getInnerFlow().getId() );
            }
        } else {
            state.put( "other", other.getId() );
        }
        state.put( "attributes", getFlowAttributes( flow ) );
        return state;
    }

    /**
     * Capture the state of a part, minus its flows
     *
     * @param part a part.
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getPartState( final Part part ) {
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "description", part.getDescription() );
        state.put( "tags", Tag.tagsToString( part.getTags() ) );
        state.put( "task", part.getTask() );
        state.put( "repeatsEvery", new Delay( part.getRepeatsEvery() ) );
        state.put( "completionTime", new Delay( part.getCompletionTime() ) );
        state.put( "attachments", new ArrayList<Attachment>( part.getAttachments() ) );
        state.put( "waivedIssueDetections", new ArrayList<String>( part.getWaivedIssueDetections() ) );
        state.put( "selfTerminating", part.isSelfTerminating() );
        state.put( "repeating", part.isRepeating() );
        state.put( "terminatesEventPhase", part.isTerminatesEventPhase() );
        state.put( "startsWithSegment", part.isStartsWithSegment() );
        state.put( "category", part.getCategory() );
        List<Map<String, Object>> mappedGoals = new ArrayList<Map<String, Object>>();
        for ( Goal goal : part.getGoals() ) {
            mappedGoals.add( goal.toMap() );
        }
        state.put( "goals", mappedGoals );
        if ( part.getInitiatedEvent() != null )
            state.put(
                    "initiatedEvent",
                    part.getInitiatedEvent().getName() );
        if ( part.getActor() != null )
            state.put(
                    "actor",
                    Arrays.asList( part.getActor().getName(), part.getActor().isType() ) );
        if ( part.getRole() != null )
            state.put( "role",
                    Arrays.asList( part.getRole().getName(), part.getRole().isType() ) );
        if ( part.getOrganization() != null )
            state.put(
                    "organization",
                    Arrays.asList( part.getOrganization().getName(), part.getOrganization().isType() ) );
        if ( part.getJurisdiction() != null )
            state.put(
                    "jurisdiction",
                    Arrays.asList( part.getJurisdiction().getName(), part.getJurisdiction().isType() ) );
        if ( part.getLocation() != null )
            state.put(
                    "location",
                    Arrays.asList( part.getLocation().getName(), part.getLocation().isType() ) );
        return state;
    }

    /**
     * Capture the state of an attachment.
     *
     * @param attachment an attachment
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getAttachmentState( Attachment attachment ) {
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "type", attachment.getType().name() );
        state.put( "url", attachment.getUrl() );
        state.put( "name", attachment.getName() );
        return state;
    }

    /**
     * Get the set of identifiables to be locked if a flow and its components are to be locked.
     *
     * @param flow a flow
     * @return a list of identifiable
     */
    public static List<Identifiable> getLockingSetFor( Flow flow ) {
        List<Identifiable> lockingSet = new ArrayList<Identifiable>();
        lockingSet.add( flow );
        if ( flow.isInternal() ) {
            Node node = flow.getSource();
            if ( node.isPart() ) lockingSet.add( node );
            node = flow.getTarget();
            if ( node.isPart() ) lockingSet.add( node );
        } else {
            ExternalFlow externalFlow = (ExternalFlow) flow;
            // lockingSet.add( externalFlow.getConnector() );
            lockingSet.add( externalFlow.getPart() );
        }
        return lockingSet;
    }

    /**
     * Get the set of identifiables to be locked if a part and its components are to be locked.
     *
     * @param part a part
     * @return a list of identifiable
     */
    public static List<Identifiable> getLockingSetFor( Part part ) {
        List<Identifiable> lockingSet = new ArrayList<Identifiable>();
        lockingSet.add( part );
        Iterator<Flow> sends = part.sends();
        while ( sends.hasNext() ) {
            Flow send = sends.next();
            lockingSet.add( send );
            lockingSet.addAll( getLockingSetFor( send ) );
        }
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            Flow receive = receives.next();
            lockingSet.add( receive );
            lockingSet.addAll( getLockingSetFor( receive ) );
        }
        return lockingSet;
    }

    /**
     * Initialize an object from attributes
     *
     * @param object     an object
     * @param attributes attributes
     */
    @SuppressWarnings( "unchecked" )
    public static void initialize( Object object, Map<String, Object> attributes ) {
        for ( String property : attributes.keySet() ) {
            Object value = attributes.get( property );
            try {
                if ( value instanceof List ) {
                    List copy = (List) value.getClass().newInstance();
                    copy.addAll( (List) value );
                    value = copy;
                }
                PropertyUtils.setProperty( object, property, value );
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( e );
            } catch ( InvocationTargetException e ) {
                throw new RuntimeException( e );
            } catch ( NoSuchMethodException e ) {
                throw new RuntimeException( e );
            } catch ( InstantiationException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Get a bean's property value.
     *
     * @param bean         an object
     * @param property     a string
     * @param defaultValue an object
     * @return an object
     */
    public static Object getProperty( Object bean, String property, Object defaultValue ) {
        if ( property.isEmpty() ) return bean;
        Object value;
        try {
            value = PropertyUtils.getProperty( bean, property );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        } catch ( NestedNullException e ) {
            LOG.debug( "Nested null on " + property + " for " + bean );
            value = null;
        }
        return value != null ? value : defaultValue;
    }

    /**
     * Get a copy of a part, including needs and capabilities.
     *
     * @param part a part
     * @return a map
     */
    public static Map<String, Object> getPartCopy( Part part ) {
        Map<String, Object> copy = new HashMap<String, Object>();
        // copy.put( "segment", part.getSegment().getId() );
        copy.put( "partState", getPartState( part ) );
        copy.put( "needs", getNeedStates( part ) );
        Iterator<Flow> capabilities = part.sends();
        copy.put( "capabilities", getCapabilityStates( part ) );
        return copy;
    }

    private static List<Map<String, Object>> getNeedStates( Part part ) {
        List<Map<String, Object>> needStates = new ArrayList<Map<String, Object>>();
        Iterator<Flow> receives = part.receives();
        while ( receives.hasNext() ) {
            mergeFlowState( needStates, getReceiveState( receives.next(), part ) );
        }
        return needStates;
    }

    private static List<Map<String, Object>> getCapabilityStates( Part part ) {
        List<Map<String, Object>> capabilityStates = new ArrayList<Map<String, Object>>();
        Iterator<Flow> sends = part.sends();
        while ( sends.hasNext() ) {
            mergeFlowState( capabilityStates, getSendState( sends.next(), part ) );
        }
        return capabilityStates;
    }

    /**
     * Aggregate EOIs of synonymous flows. Drop "other" node.
     * Keep strongest attribute values when merging (terminates > triggers> critical > useful ).
     *
     * @param flowStates a list of flow states
     * @param flowState  a flow state to merge
     */
    @SuppressWarnings( "unchecked" )
    private static void mergeFlowState(
            List<Map<String, Object>> flowStates,
            final Map<String, Object> flowState ) {
        Map<String, Object> synonymousState = (Map<String, Object>) CollectionUtils.find(
                flowStates,
                new Predicate() {
                    @SuppressWarnings( "unchecked" )
                    public boolean evaluate( Object object ) {
                        String name = (String) ( (Map<String, Object>) object ).get( "name" );
                        String otherName = (String) flowState.get( "name" );
                        return Matcher.getInstance().same( name, otherName );
                    }
                } );
        if ( synonymousState == null ) {
            flowState.put( "other", null );
            flowState.put( "otherSegment", null );
            ( (Map<String, Object>) flowState.get( "attributes" ) ).put( "askedFor", false );
            flowStates.add( flowState );
        } else {
            Map<String, Object> mergedAttributes = mergeFlowAttributes(
                    (Map<String, Object>) synonymousState.get( "attributes" ),
                    (Map<String, Object>) flowState.get( "attributes" ) );
            synonymousState.put( "attributes", mergedAttributes );
        }
    }


    /**
     * Get state of a part's capability.
     *
     * @param flow a flow
     * @param part a part
     * @return a map
     */
    @SuppressWarnings( "unchecked" )
    public static Map<String, Object> getSendState( Flow flow, Part part ) {
        Map<String, Object> sendState = getFlowState( flow, part );
        Map<String, Object> attributes = (Map<String, Object>) sendState.get( "attributes" );
        attributes.remove( "significanceToTarget" );
        attributes.remove( "all" );
        if ( !flow.isAskedFor() ) attributes.remove( "channels" );
        return sendState;
    }

    /**
     * Get state of a part's need.
     *
     * @param flow a flow
     * @param part a part
     * @return a map
     */
    @SuppressWarnings( "unchecked" )
    public static Map<String, Object> getReceiveState( Flow flow, Part part ) {
        Map<String, Object> receiveState = getFlowState( flow, part );
        Map<String, Object> attributes = (Map<String, Object>) receiveState.get( "attributes" );
        attributes.remove( "significanceToSource" );
        attributes.remove( "all" );
        if ( flow.isAskedFor() ) attributes.remove( "channels" );
        return receiveState;
    }

    /**
     * Capture a flow's identity (id and state).
     *
     * @param flow a flow
     * @param part a part
     * @return a map
     */
    public static Map<String, Object> getFlowIdentity( Flow flow, Part part ) {
        Map<String, Object> identity = new HashMap<String, Object>();
        identity.put( "flow", flow.getId() );
        identity.put( "state", getFlowState( flow, part ) );
        return identity;
    }

    /**
     * Whetehr a string starts with a vowel.
     *
     * @param str a string
     * @return a boolean
     */
    public static boolean startsWithVowel( String str ) {
        return !str.isEmpty() && "AEIOUaeiou".indexOf( str.charAt( 0 ) ) != -1;
    }
}
