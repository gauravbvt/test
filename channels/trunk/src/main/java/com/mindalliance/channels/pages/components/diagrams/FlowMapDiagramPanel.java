package com.mindalliance.channels.pages.components.diagrams;

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
public class FlowMapDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FlowMapDiagramPanel.class );
    /**
     * Scenario to be diagrammed
     */
    private IModel<Scenario> scenarioModel;
    /**
     * Selected node. Null if none selected.
     */
    private IModel<Part> partModel;

    public FlowMapDiagramPanel( String id, IModel<Scenario> model ) {
        this( id, model, null, null, null, true );
    }

    public FlowMapDiagramPanel( String id, IModel<Scenario> scenarioModel, IModel<Part> partModel ) {
        this( id, scenarioModel, partModel, null, null, true );
    }

    public FlowMapDiagramPanel( String id,
                                IModel<Scenario> scenariomodel,
                                IModel<Part> partModel,
                                double[] diagramSize,
                                String orientation,
                                boolean withImageMap ) {
        super( id, diagramSize, orientation, withImageMap );
        this.scenarioModel = scenariomodel;
        this.partModel = partModel;
        init();
    }

    /**
     * {@inheritDoc}
     */
    protected String getContainerId() {
        return "flow-map";
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram() {
        return getDiagramFactory().newFlowMapDiagram( getScenario(), getPart() );
    }

    /**
     * {@inheritDoc}
     */
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "scenario.png?scenario=" );
        sb.append( getScenario().getId() );
        sb.append( "&node=" );
        if ( getPart() != null ) {
            sb.append( getPart().getId() );
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

    /**
     * {@inheritDoc }
     */
    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    /**
     * {@inheritDoc }
     */
    protected void onSelectGraph( String graphId, AjaxRequestTarget target ) {
        try {
            Scenario scenario = getDqo().find( Scenario.class, Long.valueOf( graphId ) );
            this.update( target, new Change( Change.Type.Selected, scenario ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    /**
     * {@inheritDoc }
     */
    protected void onSelectVertex( String graphId, String vertexId, AjaxRequestTarget target ) {
        try {
            Scenario scenario = getDqo().find( Scenario.class, Long.valueOf( graphId ) );
            Part part = (Part) scenario.getNode( Long.valueOf( vertexId ) );
            if ( part != null ) {
                this.update( target, new Change( Change.Type.Selected, part ) );
            } else {
                throw new NotFoundException();
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    /**
     * {@inheritDoc }
     */
    protected void onSelectEdge( String graphId, String edgeId, AjaxRequestTarget target ) {
        // do nothing - never called
    }

    private Scenario getScenario() {
        return scenarioModel.getObject();
    }

    private Part getPart() {
        if ( partModel == null )
            return null;
        else
            return partModel.getObject();
    }
}

