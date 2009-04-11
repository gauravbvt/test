package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.diagrams.DiagramAjaxBehavior;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;


/**
 * Abstract Diagram Panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 1:11:09 PM
 */
public abstract class AbstractDiagramPanel extends AbstractUpdatablePanel {
    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractDiagramPanel.class );
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

    private StringBuilder imageMapHolder;

    public AbstractDiagramPanel( String id ) {
        this( id, null, null, true );
    }

    public AbstractDiagramPanel( String id,
                                 double[] diagramSize,
                                 String orientation,
                                 boolean withImageMap ) {
        super( id );
        this.diagramSize = diagramSize;
        this.orientation = orientation;
        this.withImageMap = withImageMap;
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
     *
     * @return diagram factory
     */
    protected DiagramFactory getDiagramFactory() {
        return Project.diagramFactory();
    }

    /**
     * Initialize.
     */
    protected void init() {
        diagram = makeDiagram();
        if ( diagramSize != null ) {
            diagram.setDiagramSize( diagramSize[0], diagramSize[1] );
        }
        if ( orientation != null ) {
            diagram.setOrientation( orientation );
        }
        if ( withImageMap ) {
            imageMapHolder = new StringBuilder();
            updateImageMap();
            add( new DiagramAjaxBehavior( imageMapHolder ) {
                protected void respond( AjaxRequestTarget target ) {
                    RequestCycle requestCycle = RequestCycle.get();
                    String graphId = requestCycle.getRequest().getParameter( "graph" );
                    String vertexId = requestCycle.getRequest().getParameter( "vertex" );
                    String edgeId = requestCycle.getRequest().getParameter( "edge" );
                    if ( graphId != null ) {
                        if ( vertexId == null && edgeId == null ) {
                            onSelectGraph( graphId, target );
                        } else if ( vertexId != null ) {
                            onSelectVertex( graphId, vertexId, target );
                        } else {
                            onSelectEdge( graphId, edgeId, target );
                        }
                    }
                }
            } );
        }
        MarkupContainer graph = new MarkupContainer( getContainerId() ) {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                tag.put( "src", makeDiagramUrl() + makeSeed() );
                if ( withImageMap ) {
                    // TODO may not be unique in the page but should be
                    tag.put( "usemap", "#" + getContainerId() );
                }
            }

            @Override
            protected void onRender( MarkupStream markupStream ) {
                super.onRender( markupStream );
                if ( withImageMap ) {
                    try {
                        getResponse().write( imageMapHolder.toString() );
                    } catch ( DiagramException e ) {
                        LOG.error( "Can't generate image map", e );
                    }
                }
            }
        };
        graph.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                onClick( target );
            }
        } );
        graph.setOutputMarkupId( true );
        add( graph );
    }

    private void updateImageMap() {
        String imageMap = diagram.makeImageMap();
        // imageMap = imageMap.replace( "id=\"G\"", "id=\"" + getContainerId() + "\"" );
        imageMap = imageMap.replace( "id=\"G\"", "" );
        imageMap = imageMap.replace( "name=\"G\"", "name=\"" + getContainerId() + "\"" );
        imageMapHolder.replace( 0, imageMapHolder.length(), imageMap );
    }

    private String makeSeed() {
        return "&_seed=" + System.currentTimeMillis() + new Random().nextInt();
    }

    /**
     * Only refresh the image.
     *
     * @param target an ajax request target
     */
    public void refreshImage( AjaxRequestTarget target ) {
        add( new AttributeModifier( "src", true, new Model<String>( makeDiagramUrl() + makeSeed() ) ) );
        target.addComponent( this );
    }

    /**
     * Return the wisket id of the diagram's container.
     *
     * @return a string
     */
    protected abstract String getContainerId();

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

    /**
     * Image is clicked.
     *
     * @param target an ajax request target
     */
    protected abstract void onClick( AjaxRequestTarget target );

    /**
     * Graph selected event.
     *
     * @param graphId a string
     * @param target  an ajax request target
     */
    protected abstract void onSelectGraph( String graphId, AjaxRequestTarget target );

    /**
     * Vertex selected event.
     *
     * @param graphId  a string
     * @param vertexId a string
     * @param target   an ajax request target
     */
    protected abstract void onSelectVertex( String graphId, String vertexId, AjaxRequestTarget target );

    /**
     * Edge selected event.
     *
     * @param graphId a string
     * @param edgeId  a string
     * @param target  an ajax request target
     */
    protected abstract void onSelectEdge( String graphId, String edgeId, AjaxRequestTarget target );


}
