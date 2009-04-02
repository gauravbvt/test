package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.MetaProvider;

/**
 * Plan DOT exporter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 8:03:37 PM
 */
public class PlanMapDOTExporter extends AbstractDOTExporter<Scenario, ScenarioRelationship> {

    public PlanMapDOTExporter( MetaProvider<Scenario, ScenarioRelationship> metaProvider ) {
        super( metaProvider );
    }

}
