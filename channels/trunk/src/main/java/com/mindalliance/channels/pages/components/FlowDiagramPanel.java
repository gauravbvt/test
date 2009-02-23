package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;
import org.apache.wicket.MarkupContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.FlowDiagram;

import java.text.MessageFormat;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 11:31:48 AM
 */
public class FlowDiagramPanel extends Panel {

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
    /**
     * Maximum size of graph
     */
    private double[] diagramSize;
    /**
     * Graph orientation
     */
    private String orientation;
    /**
     * The flow diagram
     */
    private FlowDiagram diagram;
    /**
     * Whether to add an image map
     */
    private boolean withImageMap;

    public FlowDiagramPanel( String id, IModel<Scenario> model ) {
        this( id, model, null, null, null, true );
    }

    public FlowDiagramPanel( String id,
                             IModel<Scenario> model,
                             Node selectedNode,
                             double[] diagramSize,
                             String orientation,
                             boolean withImageMap ) {
        super( id, model );
        scenario = model.getObject();
        this.selectedNode = selectedNode;
        this.diagramSize = diagramSize;
        this.orientation = orientation;
        final DiagramFactory diagramFactory = Project.diagramFactory();
        diagram = diagramFactory.newFlowDiagram( scenario );
        if ( diagramSize != null ) {
            diagram.setDiagramSize( diagramSize[0], diagramSize[1] );
        }
        if ( orientation != null ) {
            diagram.setOrientation( orientation );
        }
        this.withImageMap = withImageMap;
        init();
    }

    private void init() {
        MarkupContainer graph = new MarkupContainer( "graph" ) {                  // NON-NLS

            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                tag.put( "src", makeDiagramUrl() );                              // NON-NLS
                if ( withImageMap ) {
                    tag.put( "usemap", "#G" );
                }
            }

            @Override
            protected void onRender( MarkupStream markupStream ) {
                super.onRender( markupStream );
                if ( withImageMap ) {
                    try {
                        getResponse().write( diagram.makeImageMap() );
                    } catch ( DiagramException e ) {
                        LOG.error( "Can't generate image map", e );
                    }
                }
            }
        };

        graph.setOutputMarkupId( true );
        add( graph );
    }

    private String makeDiagramUrl() {
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
        if ( diagramSize != null ) {
            sb.append( "&size=" );
            sb.append( diagramSize[0] );
            sb.append( "," );
            sb.append( diagramSize[1] );
        }
        if ( orientation != null ) {
            sb.append( "&orientation=" );
            sb.append( orientation );
        }
        return sb.toString();
    }

}

