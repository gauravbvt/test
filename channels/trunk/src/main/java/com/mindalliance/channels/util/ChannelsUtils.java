package com.mindalliance.channels.util;

import com.mindalliance.channels.model.Attachment;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Goal;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        attributes.put( "eois", flow.copyEois() );
        attributes.put( "askedFor", flow.isAskedFor() );
        attributes.put( "all", flow.isAll() );
        attributes.put( "maxDelay", new Delay( flow.getMaxDelay() ) );
        attributes.put( "channels", flow.getChannelsCopy() );
        attributes.put( "attachments", new ArrayList<Attachment>( flow.getAttachments() ) );
        attributes.put( "waivedIssueDetections", new ArrayList<String>( flow.getWaivedIssueDetections() ) );
        attributes.put( "significanceToTarget", flow.getSignificanceToTarget() );
        attributes.put( "significanceToSource", flow.getSignificanceToSource() );
        return attributes;
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
        state.put( "task", part.getTask() );
        state.put( "repeatsEvery", new Delay( part.getRepeatsEvery() ) );
        state.put( "completionTime", new Delay( part.getCompletionTime() ) );
        state.put( "attachments", new ArrayList<Attachment>( part.getAttachments() ) );
        state.put( "waivedIssueDetections", new ArrayList<String>( part.getWaivedIssueDetections() ) );
        state.put( "selfTerminating", part.isSelfTerminating() );
        state.put( "repeating", part.isRepeating() );
        state.put( "terminatesEventPhase", part.isTerminatesEventPhase() );
        state.put( "startsWithSegment", part.isStartsWithSegment() );
        state.put( "goals", new ArrayList<Goal>( part.getGoals() ) );
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
        copy.put( "segment", part.getSegment().getId() );
        copy.put( "partState", getPartState( part ) );
        Iterator<Flow> needs = part.receives();
        List<Map<String, Object>> needStates = new ArrayList<Map<String, Object>>();
        while ( needs.hasNext() ) {
            needStates.add( getNeedState( needs.next(), part ) );
        }
        copy.put( "needs", needStates );
        Iterator<Flow> capabilities = part.sends();
        List<Map<String, Object>> capabilityStates = new ArrayList<Map<String, Object>>();
        while ( capabilities.hasNext() ) {
            capabilityStates.add( getCapabilityState( capabilities.next(), part ) );
        }
        copy.put( "capabilities", capabilityStates );
        return copy;
    }

    /**
     * Get state of a part's capability.
     *
     * @param flow a flow
     * @param part a part
     * @return a map
     */
    @SuppressWarnings( "unchecked" )
    public static Map<String, Object> getCapabilityState( Flow flow, Part part ) {
        Map<String, Object> capabilityState = getFlowState( flow, part );
        Map<String, Object> attributes = (Map<String, Object>) capabilityState.get( "attributes" );
        attributes.remove( "significanceToTarget" );
        attributes.remove( "all" );
        if ( !flow.isAskedFor() ) attributes.remove( "channels" );
        return capabilityState;
    }

    /**
     * Get state of a part's need.
     *
     * @param flow a flow
     * @param part a part
     * @return a map
     */
    @SuppressWarnings( "unchecked" )
    public static Map<String, Object> getNeedState( Flow flow, Part part ) {
        Map<String, Object> needState = getFlowState( flow, part );
        Map<String, Object> attributes = (Map<String, Object>) needState.get( "attributes" );
        attributes.remove( "significanceToSource" );
        attributes.remove( "all" );
        if ( flow.isAskedFor() ) attributes.remove( "channels" );
        return needState;
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
}
