package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.ModelObject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Entity network diagram panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 7:31:07 PM
 */
public class EntityNetworkDiagramPanel<T extends ModelObject> extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntityNetworkDiagramPanel.class );
    private IModel<T> entityModel;
    private EntityRelationship<T> selectedEntityRel;

    public EntityNetworkDiagramPanel(
            String id,
            IModel<T> entityModel,
            EntityRelationship<T> selectedEntityRel,
            double[] diagramSize,
            String domIdentifier ) {
        this( id, entityModel, selectedEntityRel, diagramSize, null, true, domIdentifier );
    }

    public EntityNetworkDiagramPanel(
            String id,
            IModel<T> entityModel,
            EntityRelationship<T> selectedEntityRel,
            double[] diagramSize,
            String orientation,
            boolean withImageMap,
            String domIdentifier ) {
        super( id, diagramSize, orientation, withImageMap, domIdentifier );
        this.entityModel = entityModel;
        this.selectedEntityRel = selectedEntityRel;
        init();
    }


    protected String getContainerId() {
        return "entity-network";
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram() {
        return getDiagramFactory().newEntityNetworkDiagram(
                entityModel.getObject(),
                selectedEntityRel,
                getDiagramSize(),
                getOrientation() );
    }

    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "network.png?entity=" );
        sb.append( getEntity().getId() );
        sb.append( "&connection=" );
        sb.append( selectedEntityRel == null ? "NONE" : selectedEntityRel.getId() );
        sb.append( "&time=" );
        sb.append( MessageFormat.format( "{2,number,0}", System.currentTimeMillis() ) );
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
        update( target, new Change( Change.Type.Selected, Channels.getPlan() ) );
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
        // Do nothing -- never called
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
            T entity = (T) getQueryService().find( ModelObject.class, Long.valueOf( vertexId ) );
            if ( entity != getEntity() ) {
                String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, entity );
                change.setScript( js );
                update( target, change );
            }
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

    private T getEntity() {
        return entityModel.getObject();
    }
}
