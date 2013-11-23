package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generation of an entity network PNG.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2009
 * Time: 10:07:09 AM
 */
public class EntityNetworkPng extends DiagramPng {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntityNetworkPng.class );

    protected Diagram makeDiagram( double[] size,
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        ModelEntity entity = null;
        EntityRelationship selectedEntityRel = null;
        if ( parameters.getNamedKeys().contains( "entity" )
                && !parameters.get( "entity" ).toString().equals( "NONE" ) ) {
            Long entityId = parameters.get( "entity" ).toLong();
            try {
                entity = communityService.find( ModelEntity.class, entityId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected entity not found at :" + entityId, e );
            }
        }
        if ( parameters.getNamedKeys().contains( "connection" )
                && !parameters.get( "connection" ).toString().equals( "NONE" ) ) {
            Long scRelId = parameters.get( "connection" ).toLong();
            selectedEntityRel = new EntityRelationship();
            selectedEntityRel.setId( scRelId, communityService );
        }
        return diagramFactory.newEntityNetworkDiagram( entity, selectedEntityRel, size, orientation );
    }
}
