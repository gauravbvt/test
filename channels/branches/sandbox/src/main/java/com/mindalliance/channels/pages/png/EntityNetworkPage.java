package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.ModelEntity;
import org.apache.wicket.PageParameters;
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
public class EntityNetworkPage extends PngWebPage {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntityNetworkPage.class );

    private ModelEntity entity;
    private EntityRelationship selectedEntityRel;

    public EntityNetworkPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        if ( parameters.containsKey( "entity" ) && !parameters.getString( "entity" ).equals( "NONE" ) ) {
            Long entityId = Long.valueOf( parameters.getString( "entity" ) );
            try {
                entity = queryService.find( ModelEntity.class, entityId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected entity not found at :" + entityId, e );
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf( parameters.getString( "connection" ) );
            selectedEntityRel = new EntityRelationship();
            selectedEntityRel.setId( scRelId, getQueryService() );
        }
    }

    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        DiagramFactory<ModelEntity, EntityRelationship> factory = getDiagramFactory();
        return factory.newEntityNetworkDiagram( entity, selectedEntityRel, size, orientation );
    }
}
