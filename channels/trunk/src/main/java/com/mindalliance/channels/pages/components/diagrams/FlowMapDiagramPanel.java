package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  /*  public FlowMapDiagramPanel( String id, IModel<Scenario> model, String domIdentifier ) {
        this( id, model, null, null, null, true, domIdentifier );
    }*/

    public FlowMapDiagramPanel( String id,
                                IModel<Scenario> scenarioModel,
                                IModel<Part> partModel,
                                double[] diagramSize,
                                String domIdentifier ) {
        this( id, scenarioModel, partModel, diagramSize, null, true, domIdentifier );
    }

    public FlowMapDiagramPanel( String id,
                                IModel<Scenario> scenariomodel,
                                IModel<Part> partModel,
                                double[] diagramSize,
                                String orientation,
                                boolean withImageMap,
                                String domIdentifier) {
        super( id, diagramSize, orientation, withImageMap, domIdentifier );
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
    protected Diagram makeDiagram( ) {
        return getDiagramFactory().newFlowMapDiagram(
                getScenario(),
                getPart(),
                getDiagramSize(),
                getOrientation() );
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
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            Scenario scenario = getQueryService().find( Scenario.class, Long.valueOf( graphId ) );
            this.update( target, new Change( Change.Type.Selected, scenario ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    /**
     * {@inheritDoc }
     */
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
        try {
            Scenario scenario = getQueryService().find( Scenario.class, Long.valueOf( graphId ) );
            Part part = (Part) scenario.getNode( Long.valueOf( vertexId ) );
            if ( part != null ) {
                String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, part );
                change.setScript( js );
                this.update( target, change );

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
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target ) {
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

