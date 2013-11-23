/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.QueryService;

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
     * @param communityService a community service
     * @return an entity relationship
     * @throws NotFoundException if id not valid
     */
    public static EntityRelationship fromId( long id, CommunityService communityService ) throws NotFoundException {
        EntityRelationship entityRel = new EntityRelationship();
        entityRel.setId( id, communityService );
        if ( entityRel.isValid() )
            return entityRel;
        else
            throw new NotFoundException();
    }

    /**
     * Tell the number of issues on all external flows.
     *
     * @param analyst an analyst
     * @param communityService the community service
     * @return a string
     */
    public String getIssuesSummary( Analyst analyst, CommunityService communityService ) {
        int count = 0;
        for ( Flow flow : flows )
            count += analyst.listUnwaivedIssues( communityService,
                                                 flow,
                                                 Analyst.INCLUDE_PROPERTY_SPECIFIC ).size();
        return count + ( count > 1 ? " issues" : " issue" );
    }

    /**
     * Does any of the external flows have issues?
     *
     * @param analyst an analyst
     * @param communityService the community service
     * @return a boolean
     */
    public boolean hasIssues( Analyst analyst, CommunityService communityService ) {
        boolean hasIssues = false;
        Iterator<Flow> iterator = flows.iterator();
        while ( !hasIssues && iterator.hasNext() ) {
            hasIssues = analyst.hasUnwaivedIssues( communityService,
                                                   iterator.next(),
                                                   Analyst.INCLUDE_PROPERTY_SPECIFIC );
        }
        return hasIssues;
    }

    public void setId( long id, Segment segment, CommunityService communityService, Analyst analyst ) {
        QueryService queryService = communityService.getPlanService();
        setId( id, communityService );
        EntityRelationship entityRel = segment == null ?
                                       analyst.findEntityRelationship( communityService,
                                                                       getFromEntity( communityService ),
                                                                       getToEntity( communityService ) ) :
                                       analyst.findEntityRelationshipInPlan( communityService,
                                               getFromEntity( communityService ),
                                               getToEntity( communityService ),
                                               segment );
        if ( entityRel != null )
            flows = entityRel.getFlows();
    }

    private ModelEntity getFromEntity( CommunityService communityService ) {
        return (ModelEntity) getFromIdentifiable( communityService );
    }

    private ModelEntity getToEntity( CommunityService communityService ) {
        return (ModelEntity) getToIdentifiable( communityService );
    }

    //-------------------------------
    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows( List<Flow> flows ) {
        this.flows = flows;
    }
}
