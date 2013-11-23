package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Hierarchical;

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

    public void setId( long id, CommunityService communityService ) {
        super.setId( id, communityService );
    }

    private Hierarchical getFromHierarchical( CommunityService communityService ) {
        return (Hierarchical) getFromIdentifiable( communityService );
    }

    private Hierarchical getToHierarchical( CommunityService communityService ) {
        return (Hierarchical) getToIdentifiable( communityService );
    }

}
