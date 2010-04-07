package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.model.Hierarchical;

/**
 * Hierarchical relationship from superior to inferior.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 26, 2009
 * Time: 8:28:44 PM
 */
public class HierarchyRelationship<T extends Hierarchical> extends Relationship {

    public HierarchyRelationship() {
    }

    public HierarchyRelationship( T fromHierarchical, T toHierarchical ) {
        super( fromHierarchical, toHierarchical );
    }

    public void setId( long id, QueryService queryService ) {
        super.setId( id, queryService );
        HierarchyRelationship hierarchyRel =  new HierarchyRelationship(
                getFromHierarchical( queryService ),
                getToHierarchical( queryService ) );
    }

    private Hierarchical getFromHierarchical( QueryService queryService ) {
        return (Hierarchical) getFromIdentifiable( queryService );
    }

    private Hierarchical getToHierarchical( QueryService queryService ) {
        return (Hierarchical) getToIdentifiable( queryService );
    }

}
