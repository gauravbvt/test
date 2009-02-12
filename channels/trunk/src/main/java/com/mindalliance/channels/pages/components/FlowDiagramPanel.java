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
import com.mindalliance.channels.graph.DiagramMaker;
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
     * Mximum size of graph
     */
    private double[] diagramSize;
    /**
     * Graph orientation
     */
    private String orientation;

    private FlowDiagram diagram;

    public FlowDiagramPanel( String id, IModel<Scenario> model ) {
       this(id, model, null, null);
    }    

    public FlowDiagramPanel( String id, IModel<Scenario> model, double[]diagramSize, String orientation ) {
        super( id, model );
        scenario = model.getObject();
        this.diagramSize = diagramSize;
        this.orientation = orientation;
        final DiagramMaker diagramMaker = Project.getProject().getDiagramMaker();
        diagram = diagramMaker.newFlowDiagram(scenario);
        if ( diagramSize != null) {
            diagram.setDiagramSize( diagramSize[0], diagramSize[1] );
        }
        if ( orientation != null) {
            diagram.setOrientation( orientation );
        }
        init();
    }

    private void init() {
        MarkupContainer graph = new MarkupContainer( "graph" ) {                                      // NON-NLS

            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                tag.put( "src", makeDiagramUrl());                                                      // NON-NLS
            }

            @Override
            protected void onRender( MarkupStream markupStream ) {
                super.onRender( markupStream );
                try {
                    getResponse().write( diagram.makeImageMap( ) );
                } catch ( DiagramException e ) {
                    LOG.error( "Can't generate image map", e );
                }
            }
        };

        graph.setOutputMarkupId( true );
        add( graph );
    }

    private String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        Node n = scenario.getDefaultPart();
        sb.append("scenario.png?scenario=");
        sb.append(scenario.getId());
        sb.append("&amp;node=");
        sb.append(n.getId());
        sb.append("&amp;time=");
        sb.append(MessageFormat.format("{2,number,0}",System.currentTimeMillis()));
        if (diagramSize != null) {
            sb.append("&amp;size=");
            sb.append(diagramSize[0]);
            sb.append(",");
            sb.append(diagramSize[1]);
        }
        if ( orientation != null) {
            sb.append("&amp;orientation=");
            sb.append(orientation);
        }
        return sb.toString();
        /*return MessageFormat.format(
                                "scenario.png?scenario={0}&amp;node={1}&amp;time={2,number,0}", // NON-NLS
                                scenario.getId(),
                                n.getId(),
                                System.currentTimeMillis() );*/
    }

}

