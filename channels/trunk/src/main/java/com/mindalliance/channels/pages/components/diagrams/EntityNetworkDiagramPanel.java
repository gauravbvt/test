package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.ModelManager;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Entity network diagram panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 7:31:07 PM
 */
public class EntityNetworkDiagramPanel<T extends ModelEntity> extends AbstractDiagramPanel {

    /**
     * Plan manager.
     */
    @SpringBean
    private ModelManager modelManager;


    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntityNetworkDiagramPanel.class );
    /**
     * Entity model.
     */
    private IModel<T> entityModel;
    /**
     * Selected entity relationship.
     */
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
        super( id, new Settings( domIdentifier, orientation, diagramSize, true, withImageMap ) );
        this.entityModel = entityModel;
        this.selectedEntityRel = selectedEntityRel;
        init();
    }


    /**
     * {@inheritDoc}
     */
    protected String getContainerId() {
        return "entity-network";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
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
        sb.append( "&");
        sb.append( TICKET_PARM );
        sb.append( '=' );
        sb.append( getTicket() );
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    protected String makeSeed() {
        // Force regeneration
        return getCollaborationModel().isDevelopment() ? "&_modified=" + System.currentTimeMillis() : "";
    }

    protected void onClick( AjaxRequestTarget target ) {
        update( target, new Change( Change.Type.Selected, getCollaborationModel() ) );
    }

    /**
     * {@inheritDoc}
     */
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        // Do nothing -- never called
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        try {
            T entity = (T) getQueryService().find( ModelEntity.class, Long.valueOf( vertexId ) );
            if ( !entity.equals( getEntity() ) ) {
                String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, entity, "network" );
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
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        EntityRelationship<T> entityRelationship = new EntityRelationship<T>();
        entityRelationship.setId( Long.valueOf( edgeId ), null, getCommunityService(), getAnalyst() );
        String js = scroll( domIdentifier, scrollTop, scrollLeft );
        Change change = new Change( Change.Type.Selected, entityRelationship );
        change.setScript( js );
        update( target, change );
    }

    private T getEntity() {
        return entityModel.getObject();
    }
}
