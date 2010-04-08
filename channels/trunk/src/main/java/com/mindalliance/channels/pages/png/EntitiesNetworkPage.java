package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Generation of an entities network PNG.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2010
 * Time: 11:59:49 AM
 */
public class EntitiesNetworkPage extends PngWebPage {
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntitiesNetworkPage.class );

    private List<ModelEntity> entities;
    private EntityRelationship selectedEntityRel;

    public EntitiesNetworkPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        if ( parameters.containsKey( "ids" ) && !parameters.getString( "ids" ).equals( "NONE" ) ) {
            entities = findEntities( parameters.getString( "ids"), queryService );
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf( parameters.getString( "connection" ) );
            selectedEntityRel = new EntityRelationship();
            selectedEntityRel.setId( scRelId, getQueryService() );
        }
    }

    private List<ModelEntity> findEntities( String ids, QueryService queryService ) {
        List<ModelEntity> modelEntities = new ArrayList<ModelEntity>();
        String[] entityIds = StringUtils.split( ids, "," );
        for ( String s : entityIds ) {
            Long entityId = Long.valueOf( s );
            try {
                ModelEntity entity = queryService.find( ModelEntity.class, entityId );
                modelEntities.add( entity );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected entity not found at :" + entityId, e );
            }
        }
        return modelEntities;
    }

    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        DiagramFactory<ModelEntity, EntityRelationship> factory = getDiagramFactory();
        return factory.newEntitiesNetworkDiagram( entities, selectedEntityRel, size, orientation );
    }}
