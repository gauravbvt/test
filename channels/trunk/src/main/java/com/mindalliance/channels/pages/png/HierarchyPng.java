package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
public class HierarchyPng extends DiagramPng {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( HierarchyPng.class );

    protected Diagram makeDiagram( double[] size,
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        Hierarchical hierarchical = null;
        String algo = "dot";
        if ( parameters.getNamedKeys().contains( "entity" )
                && !parameters.get( "entity" ).toString().equals( "NONE" ) ) {
            Long entityId = parameters.get( "entity" ).toLong();
            try {
                hierarchical = (Hierarchical) communityService.getModelService().find( ModelObject.class, entityId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected entity not found at :" + entityId, e );
            }
        }
        if ( parameters.getNamedKeys().contains( "algo" ) ) {
            algo = parameters.get( "algo" ).toString();
        }
        return diagramFactory.newHierarchyDiagram( hierarchical, size, orientation, algo );
    }
}
