package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Flows between entities of the same kind.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 5:03:25 PM
 */
public class EntityRelationship<T extends ModelEntity> extends Relationship {

    /**
     * External flows in fromSegment referencing node in toSegment
     */
    private List<Flow> flows = new ArrayList<Flow>();

    public EntityRelationship() {
    }

    public EntityRelationship( T fromEntity, T toEntity ) {
        super( fromEntity, toEntity );
    }


    public void setId( long id, QueryService queryService ) {
        super.setId( id, queryService );
        EntityRelationship entityRel = queryService.findEntityRelationship(
                getFromEntity( queryService ),
                getToEntity( queryService ) );
        if ( entityRel != null ) flows = entityRel.getFlows();
    }

    private ModelEntity getFromEntity( QueryService queryService ) {
        return (ModelEntity) getFromIdentifiable( queryService );
    }

    private ModelEntity getToEntity( QueryService queryService ) {
        return (ModelEntity) getToIdentifiable( queryService );
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows( List<Flow> flows ) {
        this.flows = flows;
    }

    /**
     * Does any of the external flows have issues?
     *
     * @param analyst an analyst
     * @return a boolean
     */
    public boolean hasIssues( Analyst analyst ) {
        boolean hasIssues = false;
        Iterator<Flow> iterator = flows.iterator();
        while ( !hasIssues && iterator.hasNext() ) {
            hasIssues = analyst.hasUnwaivedIssues( iterator.next(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
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
        for ( Flow flow : flows ) {
            count += analyst.listUnwaivedIssues( flow, Analyst.INCLUDE_PROPERTY_SPECIFIC ).size();
        }
        return count + ( count > 1 ? " issues" : " issue" );
    }

}
