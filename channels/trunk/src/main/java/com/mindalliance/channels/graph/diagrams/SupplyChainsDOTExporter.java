package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.engine.analysis.graph.AssignmentAssetLink;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.MetaProvider;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 9:58 PM
 */
public class SupplyChainsDOTExporter extends AbstractDOTExporter<Assignment, AssignmentAssetLink> {

    public SupplyChainsDOTExporter( MetaProvider<Assignment, AssignmentAssetLink> metaProvider ) {
        super( metaProvider );
    }

    // nothing added for now

}
