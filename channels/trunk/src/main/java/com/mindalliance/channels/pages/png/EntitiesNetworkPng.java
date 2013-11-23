package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generation of an entities network PNG.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2010
 * Time: 11:59:49 AM
 */
public class EntitiesNetworkPng extends DiagramPng {
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntitiesNetworkPng.class );

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram(
            double[] size,
            String orientation,
            PageParameters parameters,
            CommunityService communityService,
            DiagramFactory diagramFactory) throws DiagramException {
        Class entityClass = null;
        Segment segment = null;
        EntityRelationship selectedEntityRel = null;
        if ( parameters.getNamedKeys().contains( "class" )
                && !parameters.get( "class" ).toString().equals( "NONE" ) ) {
            String className = parameters.get( "class" ).toString();
            try {
                entityClass = Class.forName( className );
            } catch ( ClassNotFoundException e ) {
                LOG.error( "Class not found", e );
                throw new RuntimeException( e );
            }
        }
        if ( parameters.getNamedKeys().contains( "segment" )
                && !parameters.get( "segment" ).toString().equals( "NONE" ) ) {
            long id = Long.parseLong( parameters.get( "segment" ).toString() );
            try {
                segment = communityService.getPlanService().find( Segment.class, id );
            } catch ( NotFoundException e ) {
                LOG.warn( "Segment not found", e );
            }
        }
        if ( parameters.getNamedKeys().contains( "connection" )
                && !parameters.get( "connection" ).toString().equals( "NONE" ) ) {
            Long scRelId = Long.valueOf( parameters.get( "connection" ).toString() );
            selectedEntityRel = new EntityRelationship();
            selectedEntityRel.setId( scRelId, communityService );
        }
       DiagramFactory<ModelEntity, EntityRelationship> factory = diagramFactory;
        return factory.newEntitiesNetworkDiagram(
                entityClass,
                segment,
                selectedEntityRel,
                size,
                orientation );
    }
}
