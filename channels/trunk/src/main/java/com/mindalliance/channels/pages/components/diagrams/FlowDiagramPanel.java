package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.model.IModel;

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

    public FlowDiagramPanel( String id,
                             IModel<Scenario> model,
                             Node selectedNode,
                             double[] diagramSize,
                             String orientation,
                             boolean withImageMap ) {
        super( id, model, diagramSize, orientation, withImageMap );
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

}

