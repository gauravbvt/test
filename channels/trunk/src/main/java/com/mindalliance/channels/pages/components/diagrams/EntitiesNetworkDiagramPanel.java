package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2010
 * Time: 3:14:36 PM
 */
public class EntitiesNetworkDiagramPanel<T extends ModelEntity> extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntitiesNetworkDiagramPanel.class );
    private Class<T> entityClass;
    private Segment segment;
    /**
     * Selected organization relationship.
     */
    private EntityRelationship<? extends ModelEntity> selectedEntityRel;

    public EntitiesNetworkDiagramPanel(
            String id,
            Class<T> entityClass,
            Segment segment,
            EntityRelationship<T> selectedEntityRel,
            double[] diagramSize,
            String domIdentifier ) {
        this( id, entityClass, segment, selectedEntityRel, diagramSize, null, true, domIdentifier );
    }

    public EntitiesNetworkDiagramPanel(
            String id,
            Class<T> entityClass,
            Segment segment,
            EntityRelationship<T> selectedEntityRel,
            double[] diagramSize,
            String orientation,
            boolean withImageMap,
            String domIdentifier ) {
        super( id, new Settings( domIdentifier, orientation, diagramSize, true, withImageMap ) );
        this.entityClass = entityClass;
        this.segment = segment;
        this.selectedEntityRel = selectedEntityRel;
        init();
    }

    protected String getContainerId() {
        return "entities-network";
    }

    protected Diagram makeDiagram() {
        return getDiagramFactory().newEntitiesNetworkDiagram(
                entityClass,
                segment,
                selectedEntityRel,
                getDiagramSize(),
                getOrientation());
    }

    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "/entities.png?class=" );
        sb.append( entityClass.getName() );
        sb.append( "&segment=" );
        sb.append( segment == null ? "NONE" : segment.getId() );
        sb.append( "&connection=" );
        sb.append( selectedEntityRel == null ? "NONE" : selectedEntityRel.getId() );
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

    /**
     * {@inheritDoc}
     */
    protected void onClick( AjaxRequestTarget target ) {
        update( target, new Change( Change.Type.Selected, getPlan() ) );
    }

    /**
     * {@inheritDoc}
     */
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            ModelEntity entity = getQueryService().find( Organization.class, Long.valueOf( vertexId ) );
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Change change = new Change( Change.Type.Selected, entity );
            change.setScript( js );
            update( target, change );
        } catch ( NotFoundException e ) {
            LOG.warn( "Not found", e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        EntityRelationship<T> entityRelationship = new EntityRelationship<T>();
        entityRelationship.setId( Long.valueOf( edgeId ), getQueryService() );
        String js = scroll( domIdentifier, scrollTop, scrollLeft );
        Change change = new Change( Change.Type.Selected, entityRelationship );
        change.setScript( js );
        update( target, change );
    }
}
