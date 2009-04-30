package com.mindalliance.channels.command;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Delay;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.InternalFlow;

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
        attributes.put( "all", flow.isAll() );
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
        if ( other.isConnector() ) {
            // Remember the connector's inner flow if external (its id is persistent, if mapped)
            if ( part.getScenario() != other.getScenario() ) {
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

    /**
     * Get a bean's property value.
     *
     * @param bean         an object
     * @param property     a string
     * @param defaultValue an object
     * @return an object
     */
    public static Object getProperty( Object bean, String property, Object defaultValue ) {
        Object value;
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

    /**
     * Get a copy of a part, including needs and capabilities.
     *
     * @param part a part
     * @return a map
     */
    public static Map<String, Object> getPartCopy( Part part ) {
        Map<String, Object> copy = new HashMap<String, Object>();
        copy.put( "scenario", part.getScenario().getId() );
        copy.put( "partState", getPartState( part ) );
        Iterator<Flow> needs = part.requirements();
        List<Map<String, Object>> needStates = new ArrayList<Map<String, Object>>();
        while ( needs.hasNext() ) {
            needStates.add( getNeedState( needs.next(), part ) );
        }
        copy.put( "needs", needStates );
        Iterator<Flow> capabilities = part.outcomes();
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
     * Make a duplicate of the flow
     *
     * @param flow      a flow to duplicate
     * @param isOutcome whether to replicate as outcome or requirement
     * @return a created flow
     */
    public static Flow duplicate( Flow flow, boolean isOutcome ) {
        Flow duplicate;
        if ( isOutcome ) {
            Node source = flow.getSource();
            Scenario scenario = flow.getSource().getScenario();
            DataQueryObject dqo = scenario.getDqo();
            duplicate = dqo.connect( source, dqo.createConnector( scenario ), flow.getName() );
        } else {
            Node target = flow.getTarget();
            Scenario scenario = target.getScenario();
            DataQueryObject dqo = scenario.getDqo();
            duplicate = dqo.connect( dqo.createConnector( scenario ), target, flow.getName() );
        }
        duplicate.initFrom( flow );
        return duplicate;
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
     * Resolve a node from an id.
     * @param id a long
     * @param scenario a scenario in context
     * @param dqo a data query object
     * @return a node
     * @throws CommandException if not found
     */
    public static Node resolveNode( Long id, Scenario scenario, DataQueryObject dqo ) throws CommandException {
        Node node;
        // null id represents a local connector
        if ( id != null ) {
            ModelObject mo;
            try {
                mo = dqo.find( ModelObject.class, id );
            } catch ( NotFoundException e ) {
                throw new CommandException( "You need to refresh.", e );
            }
            // How external an connector is captured
            if ( mo instanceof InternalFlow ) {
                InternalFlow internalFlow = (InternalFlow) mo;
                assert ( internalFlow.hasConnector() );
                node = internalFlow.getSource().isConnector()
                        ? internalFlow.getSource()
                        : internalFlow.getTarget();
            } else {
                node = scenario.getNode( id );
            }

        } else {
            node = dqo.createConnector( scenario );
        }
        return node;
    }
}
