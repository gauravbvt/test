package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.ModelObject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 25, 2009
 * Time: 3:04:13 PM
 */
public class HierarchyDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( HierarchyDiagramPanel.class );
    /**
     * Hierarchical object model.
     */
    private IModel<Hierarchical> hierarchicalModel;

    public HierarchyDiagramPanel(
            String id,
            IModel<Hierarchical> hierarchicalModel,
            double[] diagramSize,
            String domIdentifier ) {
        super( id, diagramSize, null, true, domIdentifier );
        this.hierarchicalModel = hierarchicalModel;
        init();
    }

    protected String getContainerId() {
        return "hierarchy-diagram";
    }

    protected Diagram makeDiagram() {
        return getDiagramFactory().newHierarchyDiagram(
                hierarchicalModel.getObject(),
                getDiagramSize(),
                getOrientation() );
    }

    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "/hierarchy.png?entity=" );
        sb.append( getHierarchical().getId() );
        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null ) {
            sb.append( "&size=" );
            sb.append( diagramSize[0] );
            sb.append( "," );
            sb.append( diagramSize[1] );
        }
        String orientation = getOrientation();
        if ( orientation != null ) {
            sb.append( "&orientation=" );
            sb.append( orientation );
        }
        return sb.toString();
    }

    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        // Do nothing -- never called
    }

    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            Hierarchical entity = (Hierarchical) getQueryService().find(
                    ModelObject.class,
                    Long.valueOf( vertexId ) );
            if ( entity != getHierarchical() ) {
                // String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Expanded, entity );
                // change.setScript( js );
                update( target, change );
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Not found", e );
        }
    }

    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        // Never called
    }

    private Hierarchical getHierarchical() {
        return hierarchicalModel.getObject();
    }

}
