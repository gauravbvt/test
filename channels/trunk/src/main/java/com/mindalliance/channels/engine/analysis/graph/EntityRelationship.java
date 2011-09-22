/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Flows between entities of the same kind.
 */
public class EntityRelationship<T extends ModelEntity> extends Relationship {

    /**
     * External flows in fromSegment referencing node in toSegment
     */
    private List<Flow> flows = new ArrayList<Flow>();

    //-------------------------------
    public EntityRelationship() {
    }

    public EntityRelationship( T fromEntity, T toEntity ) {
        super( fromEntity, toEntity );
    }

    //-------------------------------

    /**
     * Create entity relationship from its synthetic id.
     *
     * @param id a long
     * @param queryService a query service
     * @return an entity relationship
     * @throws NotFoundException if id not valid
     */
    public static EntityRelationship fromId( long id, QueryService queryService ) throws NotFoundException {
        EntityRelationship entityRel = new EntityRelationship();
        entityRel.setId( id, queryService );
        if ( entityRel.isValid() )
            return entityRel;
        else
            throw new NotFoundException();
    }

    /**
     * Tell the number of issues on all external flows.
     *
     * @param analyst an analyst
     * @param queryService the query service
     * @return a string
     */
    public String getIssuesSummary( Analyst analyst, QueryService queryService ) {
        int count = 0;
        for ( Flow flow : flows )
            count += analyst.listUnwaivedIssues( queryService,
                                                 flow,
                                                 Analyst.INCLUDE_PROPERTY_SPECIFIC ).size();
        return count + ( count > 1 ? " issues" : " issue" );
    }

    /**
     * Does any of the external flows have issues?
     *
     * @param analyst an analyst
     * @param queryService the query service
     * @return a boolean
     */
    public boolean hasIssues( Analyst analyst, QueryService queryService ) {
        boolean hasIssues = false;
        Iterator<Flow> iterator = flows.iterator();
        while ( !hasIssues && iterator.hasNext() ) {
            hasIssues = analyst.hasUnwaivedIssues( queryService,
                                                   iterator.next(),
                                                   Analyst.INCLUDE_PROPERTY_SPECIFIC );
        }
        return hasIssues;
    }

    public void setId( long id, Segment segment, QueryService queryService, Analyst analyst ) {
        setId( id, queryService );
        EntityRelationship entityRel = segment == null ?
                                       analyst.findEntityRelationship( queryService,
                                                                       getFromEntity( queryService ),
                                                                       getToEntity( queryService ) ) :
                                       analyst.findEntityRelationship( queryService,
                                                                       getFromEntity( queryService ),
                                                                       getToEntity( queryService ),
                                                                       segment );
        if ( entityRel != null )
            flows = entityRel.getFlows();
    }

    private ModelEntity getFromEntity( QueryService queryService ) {
        return (ModelEntity) getFromIdentifiable( queryService );
    }

    private ModelEntity getToEntity( QueryService queryService ) {
        return (ModelEntity) getToIdentifiable( queryService );
    }

    //-------------------------------
    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows( List<Flow> flows ) {
        this.flows = flows;
    }
}
