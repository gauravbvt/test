package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.engine.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hierarchy PNG.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 26, 2009
 * Time: 8:15:39 AM
 */
public class HierarchyPage extends PngWebPage {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( HierarchyPage.class );
    private Hierarchical hierarchical;
    private String algo = "dot";

    public HierarchyPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        if ( parameters.containsKey( "entity" ) && !parameters.getString( "entity" ).equals( "NONE" ) ) {
            Long entityId = Long.valueOf( parameters.getString( "entity" ) );
            try {
                hierarchical = (Hierarchical) queryService.find( ModelObject.class, entityId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected entity not found at :" + entityId, e );
            }
        }
        if ( parameters.containsKey( "algo" ) ) {
            algo = parameters.getString( "algo" );
        }
    }

    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        DiagramFactory<Hierarchical, HierarchyRelationship> factory = getDiagramFactory();
        return factory.newHierarchyDiagram( hierarchical, size, orientation, algo );
    }
}
