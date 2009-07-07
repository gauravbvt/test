package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.model.Hierarchical;

/**
 * Hierarchy DOT exporter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 6, 2009
 * Time: 4:26:46 PM
 */
public class HierarchyDOTExporter extends AbstractDOTExporter<Hierarchical, HierarchyRelationship> {
    public HierarchyDOTExporter( MetaProvider<Hierarchical, HierarchyRelationship> metaProvider ) {
        super( metaProvider );
    }
}
