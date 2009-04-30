package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.analysis.graph.EntityRelationship;

/**
 * Entity network DOT exporter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 8:34:55 PM
 */
public class EntityNetworkDOTExporter extends AbstractDOTExporter<ModelObject, EntityRelationship> {

    public EntityNetworkDOTExporter( MetaProvider<ModelObject, EntityRelationship> metaProvider ) {
        super( metaProvider );
    }
}
