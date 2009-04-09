package com.mindalliance.channels.pages.png;

import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.DataQueryObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.analysis.graph.EntityRelationship;

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

    private ModelObject entity;
    private EntityRelationship selectedEntityRel;

    public EntityNetworkPage( PageParameters parameters ) {
        super( parameters );
        DataQueryObject dqo = getDqo();
        if ( parameters.containsKey( "entity" ) && !parameters.getString( "entity" ).equals( "NONE" ) ) {
            Long entityId = Long.valueOf(parameters.getString( "entity" ));
            try {
                entity = dqo.find( ModelObject.class, entityId);
            } catch ( NotFoundException e ) {
                LOG.warn("Selected entity not found at :" + entityId, e);
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long scRelId = Long.valueOf(parameters.getString( "connection" ));
            selectedEntityRel = new EntityRelationship();
            selectedEntityRel.setId( scRelId, getDqo() );
        }
    }

    protected Diagram makeDiagram() {
        DiagramFactory<ModelObject, EntityRelationship> factory = getDiagramFactory();
        return factory.newEntityNetworkDiagram( entity, selectedEntityRel );
    }
}
