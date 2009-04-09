package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.command.Change;
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
            EntityRelationship<T> selectedEntityRel ) {
        this( id, entityModel, selectedEntityRel, null, null, true );
    }

    public EntityNetworkDiagramPanel(
            String id,
            IModel<T> entityModel,
            EntityRelationship<T> selectedEntityRel,
            double[] diagramSize,
            String orientation,
            boolean withImageMap ) {
        super( id, diagramSize, orientation, withImageMap );
        this.entityModel = entityModel;
        this.selectedEntityRel = selectedEntityRel;
        init();
    }


    protected String getContainerId() {
        return "entity-network";
    }

    protected Diagram makeDiagram() {
        return getDiagramFactory().newEntityNetworkDiagram(
                entityModel.getObject(),
                selectedEntityRel );
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
        update( target, new Change( Change.Type.Selected, Project.getProject() ) );
    }

    protected void onSelectGraph( String graphId, AjaxRequestTarget target ) {
        // Do nothing -- never called
    }

    protected void onSelectVertex( String graphId, String vertexId, AjaxRequestTarget target ) {
        try {
            T entity = (T)getDqo().find( ModelObject.class, Long.valueOf( vertexId ) );
            if (entity != getEntity())
                update( target, new Change( Change.Type.Selected, entity ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Not found", e );
        }
     }

    protected void onSelectEdge( String graphId, String edgeId, AjaxRequestTarget target ) {
        EntityRelationship<T> entityRelationship = new EntityRelationship<T>();
        entityRelationship.setId( Long.valueOf( edgeId ), getDqo() );
        update( target, new Change( Change.Type.Selected, entityRelationship ) );
    }

    private T getEntity() {
        return entityModel.getObject();
    }
}
