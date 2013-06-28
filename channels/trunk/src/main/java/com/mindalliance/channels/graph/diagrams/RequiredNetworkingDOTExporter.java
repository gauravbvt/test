package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.MetaProvider;

/**
 * Required networking DOT exporter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 6:18 PM
 */
public class RequiredNetworkingDOTExporter extends AbstractDOTExporter<Agency, RequirementRelationship> {

    public RequiredNetworkingDOTExporter( MetaProvider<Agency, RequirementRelationship> metaProvider ) {
        super( metaProvider );
    }

}
