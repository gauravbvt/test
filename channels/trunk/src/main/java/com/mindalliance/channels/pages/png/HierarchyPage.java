package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.ModelObject;
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
    }

    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        DiagramFactory<Hierarchical, HierarchyRelationship> factory = getDiagramFactory();
        return factory.newHierarchyDiagram( hierarchical, size, orientation );
    }
}
