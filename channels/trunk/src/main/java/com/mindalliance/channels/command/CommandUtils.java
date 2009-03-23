package com.mindalliance.channels.command;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Identifiable;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Command framework utilities.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 6, 2009
 * Time: 3:42:39 PM
 */
public final class CommandUtils {

    private CommandUtils() {

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
        attributes.put( "askedFor", flow.isAskedFor() );
        attributes.put( "maxDelay", new Delay( flow.getMaxDelay() ) );
        attributes.put( "channels", flow.getChannelsCopy() );
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
        final boolean isOutcome;
        if ( flow.isInternal() ) {
            isOutcome = flow.getSource() == part;
            other = isOutcome ? flow.getTarget() : flow.getSource();
        } else {
            ExternalFlow externalFlow = (ExternalFlow) flow;
            isOutcome = !externalFlow.isPartTargeted();
            other = externalFlow.getConnector();
        }
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "name", flow.getName() );
        state.put( "isOutcome", isOutcome );
        state.put( "scenario", part.getScenario().getId() );
        state.put( "part", part.getId() );
        state.put( "otherScenario", other.getScenario().getId() );
        if ( !other.isConnector() ) state.put( "other", other.getId() );
        state.put( "attributes", getFlowAttributes( flow ) );
        return state;
    }

    /**
     * Capture the state of a part, minus its flows
     *
     * @param part a part
     * @return a map of attribute names and values
     */
    public static Map<String, Object> getPartState( final Part part ) {
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "description", part.getDescription() );
        state.put( "task", part.getTask() );
        state.put( "actor", part.getActor() );
        state.put( "role", part.getRole() );
        state.put( "organization", part.getOrganization() );
        state.put( "jurisdiction", part.getJurisdiction() );
        state.put( "location", part.getLocation() );
        state.put( "repeatsEvery", part.getRepeatsEvery() );
        state.put( "completionTime", part.getCompletionTime() );
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
        if ( flow.isInternal() ) {
            lockingSet.add( flow.getSource() );
            lockingSet.add( flow.getTarget() );
        } else {
            ExternalFlow externalFlow = (ExternalFlow) flow;
            lockingSet.add( externalFlow.getConnector() );
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
        Iterator<Flow> outcomes = part.outcomes();
        while ( outcomes.hasNext() ) {
            Flow outcome = outcomes.next();
            lockingSet.add( outcome );
            lockingSet.addAll( getLockingSetFor( outcome ) );
        }
        Iterator<Flow> requirements = part.requirements();
        while ( requirements.hasNext() ) {
            Flow requirement = requirements.next();
            lockingSet.add( requirement );
            lockingSet.addAll( getLockingSetFor( requirement ) );
        }
        return lockingSet;
    }

    /**
     * Initialize an object from attributes
     *
     * @param object     an object
     * @param attributes attributes
     */
    public static void initialize( Object object, Map<String, Object> attributes ) {
        for ( String property : attributes.keySet() ) {
            Object value = attributes.get( property );
            try {
                PropertyUtils.setProperty( object, property, value );
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( e );
            } catch ( InvocationTargetException e ) {
                throw new RuntimeException( e );
            } catch ( NoSuchMethodException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    public static Object getProperty( Object bean, String property, Object defaultValue ) {
        Object value = null;
        try {
            value = PropertyUtils.getProperty( bean, property );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        }
        return value != null ? value : defaultValue;
    }
}
