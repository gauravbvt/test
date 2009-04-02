package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Abstract Diagram Panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 1:11:09 PM
 */
public abstract class AbstractDiagramPanel extends Panel {
    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractDiagramPanel.class );
    /**
     * Model.
     */
    private IModel<? extends Serializable> model;
    /**
     * The flow diagram
     */
    private Diagram diagram;
    /**
     * Whether to add an image map
     */
    private boolean withImageMap;
    /**
     * The requested size of the diagram (can be null).
     */
    private double[] diagramSize;
    /**
     * The orientation of the diagram.
     */
    private String orientation;

    public AbstractDiagramPanel( String id, IModel<? extends Serializable> model ) {
        this( id, model, null, null, true );
    }

    public AbstractDiagramPanel( String id,
                                 IModel<? extends Serializable> model,
                                 double[] diagramSize,
                                 String orientation,
                                 boolean withImageMap ) {
        super( id, model );
        this.model = model;
        this.diagramSize = diagramSize;
        this.orientation = orientation;
        this.withImageMap = withImageMap;
    }

    public IModel<? extends Serializable> getModel() {
        return model;
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public boolean isWithImageMap() {
        return withImageMap;
    }

    public double[] getDiagramSize() {
        return diagramSize;
    }

    public String getOrientation() {
        return orientation;
    }

    /**
     * Get diagram factory.
     * @return diagram factory
     */
    protected DiagramFactory getDiagramFactory() {
        return Project.diagramFactory();
    }

    protected void init() {
        diagram = makeDiagram();
        if ( diagramSize != null ) {
            diagram.setDiagramSize( diagramSize[0], diagramSize[1] );
        }
        if ( orientation != null ) {
            diagram.setOrientation( orientation );
        }
        MarkupContainer graph = new MarkupContainer( "graph" ) {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                tag.put( "src", makeDiagramUrl() );
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

    /**
     * Instantiate diagram to display.
     *
     * @return a diagram
     */
    protected abstract Diagram makeDiagram();

    /**
     * Get URL for requesting the image of the diagram.
     *
     * @return a string
     */
    protected abstract String makeDiagramUrl();
}
