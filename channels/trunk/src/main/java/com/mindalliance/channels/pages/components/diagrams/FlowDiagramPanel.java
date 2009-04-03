package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 11:31:48 AM
 */
public class FlowDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FlowDiagramPanel.class );
    /**
     * Scenario to be diagrammed
     */
    private Scenario scenario;
    /**
     * Selected node. Null if none selected.
     */
    private Node selectedNode;

    public FlowDiagramPanel( String id, IModel<Scenario> model ) {
        this( id, model, null, null, null, true );
    }

    public FlowDiagramPanel( String id, IModel<Scenario> model, IModel<Part> partModel ) {
        this( id, model, partModel.getObject(), null, null, true );
    }

    public FlowDiagramPanel( String id,
                             IModel<Scenario> model,
                             Node selectedNode,
                             double[] diagramSize,
                             String orientation,
                             boolean withImageMap ) {
        super( id, diagramSize, orientation, withImageMap );
        scenario = model.getObject();
        this.selectedNode = selectedNode;
        init();
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram() {
        return getDiagramFactory().newFlowMapDiagram( scenario );
    }

    /**
     * {@inheritDoc}
     */
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "scenario.png?scenario=" );
        sb.append( scenario.getId() );
        sb.append( "&node=" );
        if ( selectedNode != null ) {
            sb.append( selectedNode.getId() );
        } else {
            sb.append( "NONE" );
        }
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

    protected void onSelectGraph( String graphId, AjaxRequestTarget target ) {
        try {
            Scenario scenario = getDqo().find( Scenario.class, Long.valueOf( graphId ) );
            this.update( target, new Change( Change.Type.Selected, scenario ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    protected void onSelectVertex( String graphId, String vertexId, AjaxRequestTarget target ) {
        try {
            Scenario scenario = getDqo().find( Scenario.class, Long.valueOf( graphId ) );
            Part part = (Part)scenario.getNode( Long.valueOf( vertexId ) );
            if ( part != null ) {
                this.update( target, new Change( Change.Type.Selected, part ) );
            } else {
                throw new NotFoundException();
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    protected void onSelectEdge( String graphId, String edgeId, AjaxRequestTarget target ) {
        // do nothing - never called
    }
}

