package com.mindalliance.channels.analysis.network;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.analysis.Analyst;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A link from a scenario to another composed of aggregated external flows.
 * The external flows are defined in the "from" scenario and reference connectors in the "to" scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 7:11:08 PM
 */
public class ScenarioRelationship implements Identifiable {
    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ScenarioRelationship.class );

    /**
     * Scenario wehre external links are defined.
     */
    private Long fromScenarioId;
    /**
     * Scenario where referenced connectors are defined.
     */
    private Long toScenarioId;

    /**
     * External flows in fromScenario referencing node in toScenario
     */
    private List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();

    public ScenarioRelationship() {
    }

    public ScenarioRelationship( Scenario fromScenario, Scenario toScenario ) {
        fromScenarioId = fromScenario.getId();
        toScenarioId = toScenario.getId();
    }

    /**
     * Long value of(<fromScenario id as string>
     * concatenated to  <toScenario id as string of lenght 9, left padded with 0>.
     *
     * @return a long
     */
    public long getId() {
        String toId = Long.toString( toScenarioId );
        toId = StringUtils.leftPad( toId, 9, '0' );
        String fromId = Long.toString( fromScenarioId );
        return Long.valueOf( fromId + toId );
    }

    public void setId( long id, DataQueryObject dqo ) {
        String s = Long.toString( id );
        String toId = s.substring( s.length() - 9 );
        String fromId = s.substring( 0, s.length() - 9 );
        fromScenarioId = Long.valueOf( fromId );
        toScenarioId = Long.valueOf( toId );
        ScenarioRelationship scRel = dqo.findScenarioRelationship(
                getFromScenario( dqo ),
                getToScenario( dqo ) );
        if ( scRel != null ) externalFlows = scRel.getExternalFlows();
    }

    public String getName() {
        return "From " + fromScenarioId + " to " + toScenarioId;
    }

    public String getDescription() {
        return "";
    }

    public Long getFromScenarioId() {
        return fromScenarioId;
    }

    public Long getToScenarioId() {
        return toScenarioId;
    }

    public List<ExternalFlow> getExternalFlows() {
        return externalFlows;
    }

    public void setExternalFlows( List<ExternalFlow> externalFlows ) {
        this.externalFlows = externalFlows;
    }

    /**
     * Get from-scenario.
     *
     * @param dqo a data query object
     * @return a scenario
     */
    public Scenario getFromScenario( DataQueryObject dqo ) {
        try {
            return dqo.find( Scenario.class, fromScenarioId );
        } catch ( NotFoundException e ) {
            LOG.warn( "From-scenario not found", e );
            return null;
        }
    }

    /**
     * Get to-scenario.
     *
     * @param dqo a data query object
     * @return a scenario
     */
    public Scenario getToScenario( DataQueryObject dqo ) {
        try {
            return dqo.find( Scenario.class, toScenarioId );
        } catch ( NotFoundException e ) {
            LOG.warn( "To-scenario not found", e );
            return null;
        }
    }

    /**
     * Does any of the external flows have issues?
     *
     * @param analyst an analyst
     * @return a boolean
     */
    public boolean hasIssues( Analyst analyst ) {
        boolean hasIssues = false;
        Iterator<ExternalFlow> iterator = externalFlows.iterator();
        while ( !hasIssues && iterator.hasNext() ) {
            hasIssues = analyst.hasIssues( iterator.next(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
        }
        return hasIssues;
    }

    /**
     * Tell the number of issues on all external flows.
     *
     * @param analyst an analyst
     * @return a string
     */
    public String getIssuesSummary( Analyst analyst ) {
        int count = 0;
        for ( ExternalFlow externalFlow : externalFlows ) {
            count += analyst.listIssues( externalFlow, Analyst.INCLUDE_PROPERTY_SPECIFIC ).size();
        }
        return count + ( count > 1 ? " issues" : " issue" );
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj instanceof ScenarioRelationship
                && getId() == ( (ScenarioRelationship) obj ).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Long.valueOf( getId() ).hashCode();
    }

}
