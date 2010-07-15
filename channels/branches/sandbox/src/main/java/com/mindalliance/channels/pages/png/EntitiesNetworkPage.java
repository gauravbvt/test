package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
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
public class EntitiesNetworkPage extends PngWebPage {
    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntitiesNetworkPage.class );

    private Class entityClass;
    private Segment segment;
    private EntityRelationship selectedEntityRel;

    public EntitiesNetworkPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        if ( parameters.containsKey( "class" ) && !parameters.getString( "class" ).equals( "NONE" ) ) {
            String className = parameters.getString( "class" );
            try {
                entityClass = Class.forName( className );
            } catch ( ClassNotFoundException e ) {
                LOG.error( "Class not found", e );
                throw new RuntimeException( e );
            }
        }
        if ( parameters.containsKey( "segment" ) && !parameters.getString( "segment" ).equals( "NONE" ) ) {
            long id = Long.parseLong( parameters.getString( "segment" ) );
            try {
                segment = queryService.find( Segment.class, id );
            } catch ( NotFoundException e ) {
                LOG.warn( "Segment not found", e );
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf( parameters.getString( "connection" ) );
            selectedEntityRel = new EntityRelationship();
            selectedEntityRel.setId( scRelId, getQueryService() );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        DiagramFactory<ModelEntity, EntityRelationship> factory = getDiagramFactory();
        return factory.newEntitiesNetworkDiagram(
                entityClass,
                segment,
                selectedEntityRel,
                size,
                orientation );
    }
}
