package com.mindalliance.channels.analysis.network;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.analysis.Analyst;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

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
     * Scenario wehre external links are defined.
     */
    private Scenario fromScenario;
    /**
     * Scenario where referenced connectors are defined.
     */
    private Scenario toScenario;

    private long id = 0L;
    /**
     * External flows in fromScenario referencing node in toScenario
     */
    private List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();

    public ScenarioRelationship( Scenario fromScenario, Scenario toScenario) {
        this.fromScenario = fromScenario;
        this.toScenario = toScenario;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getName() {
        return "From " + fromScenario.getName() + " to " + toScenario.getName();
    }

    public String getDescription() {
        return "";
    }

    public Scenario getFromScenario() {
        return fromScenario;
    }

    public Scenario getToScenario() {
        return toScenario;
    }

    public List<ExternalFlow> getExternalFlows() {
        return externalFlows;
    }

    public void setExternalFlows( List<ExternalFlow> externalFlows ) {
        this.externalFlows = externalFlows;
    }

    /**
     * Does any of the external flows have issues?
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
     * @param analyst an analyst
     * @return a string
     */
    public String getIssuesSummary( Analyst analyst ) {
        int count = 0;
        for (ExternalFlow externalFlow : externalFlows) {
            count += analyst.listIssues( externalFlow, Analyst.INCLUDE_PROPERTY_SPECIFIC ).size();
        }
        return count + (count > 1 ? " issues" : " issue");
    }
}
