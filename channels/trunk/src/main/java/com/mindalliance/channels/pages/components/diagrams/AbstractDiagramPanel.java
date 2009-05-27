package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.diagrams.DiagramAjaxBehavior;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Abstract Diagram Panel
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 1:11:09 PM
 */
public abstract class AbstractDiagramPanel extends AbstractCommandablePanel {
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
    /**
     * IMage map holder.
     */
    private StringBuilder imageMapHolder;
    /**
     * Unique CSS identifier to image container.
     */
    private String domIdentifier;

    public AbstractDiagramPanel( String id ) {
        this( id, null, null, true, null );
    }

    public AbstractDiagramPanel( String id,
                                 double[] diagramSize,
                                 String orientation,
                                 boolean withImageMap ) {
        this( id, diagramSize, orientation, withImageMap, null );
    }

    public AbstractDiagramPanel( String id,
                                 double[] diagramSize,
                                 String orientation,
                                 boolean withImageMap,
                                 String domIdentifier ) {
        super( id );
        this.diagramSize = diagramSize;
        this.orientation = orientation;
        this.withImageMap = withImageMap;
        this.domIdentifier = domIdentifier;
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
     * Initialize.
     */
    protected void init() {
        diagram = makeDiagram();
        if ( withImageMap ) {
            imageMapHolder = new StringBuilder();
            updateImageMap();
            add( new DiagramAjaxBehavior( imageMapHolder, domIdentifier ) {
                protected void respond( AjaxRequestTarget target ) {
                    RequestCycle requestCycle = RequestCycle.get();
                    String graphId = requestCycle.getRequest().getParameter( "graph" );
                    String vertexId = requestCycle.getRequest().getParameter( "vertex" );
                    String edgeId = requestCycle.getRequest().getParameter( "edge" );
                    String width = requestCycle.getRequest().getParameter( "width" );
                    String height = requestCycle.getRequest().getParameter( "height" );
                    if ( graphId != null ) {
                        if ( vertexId == null && edgeId == null ) {
                            onSelectGraph( graphId, domIdentifier, 0, 0, target );
                        } else if ( vertexId != null ) {
                            int[] scroll = calculateVertexScroll( imageMapHolder, vertexId, width, height );
                            onSelectVertex( graphId, vertexId, domIdentifier, scroll[0], scroll[1], target );
                        } else {
                            int[] scroll = calculateEdgeScroll( imageMapHolder, edgeId, width, height );
                            onSelectEdge( graphId, edgeId, domIdentifier, scroll[0], scroll[1], target );
                        }
                    }
                }
            } );
        }
        MarkupContainer graph = new MarkupContainer( getContainerId() ) {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String url = makeDiagramUrl() + makeSeed();
                tag.put( "src", url );
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
                        LOG.debug( "Rendering image map ");
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

    private int[] calculateVertexScroll(
            StringBuilder imageMapHolder,
            String vertexId,
            String swidth,
            String sheight ) {
        int[] scroll = new int[2];
        String imageMap = imageMapHolder.toString();
        String s = "<area.*?vertex="
                + vertexId
                + "&.*?coords=\"(\\d+),(\\d+),(\\d+),(\\d+)\"";
        Pattern pattern = Pattern.compile( s );
        Matcher matcher = pattern.matcher( imageMap );
        if ( matcher.find() ) {
            int x1 = Integer.parseInt( matcher.group( 1 ) );
            int y1 = Integer.parseInt( matcher.group( 2 ) );
            int x2 = Integer.parseInt( matcher.group( 3 ) );
            int y2 = Integer.parseInt( matcher.group( 4 ) );
            int centerX = x2 - ( ( x2 - x1 ) / 2 );
            int centerY = y2 - ( ( y2 - y1 ) / 2 );
            int width = Integer.parseInt( swidth );
            int height = Integer.parseInt( sheight );
            scroll[1] = Math.max( 0, centerX - ( width / 2 ) );
            scroll[0] = Math.max( 0, centerY - ( height / 2 ) );
        }
        return scroll;
    }

    private int[] calculateEdgeScroll(
            StringBuilder imageMapHolder,
            String edgeId,
            String swidth,
            String sheight ) {
        int[] scroll = new int[2];
        String imageMap = imageMapHolder.toString();
        String s = "<area.*?edge="
                + edgeId
                + "&.*?coords=\"(\\d+),(\\d+),(\\d+),(\\d+)\"";
        Pattern pattern = Pattern.compile( s );
        Matcher matcher = pattern.matcher( imageMap );
        if ( matcher.find() ) {
            int x1 = Integer.parseInt( matcher.group( 1 ) );
            int y1 = Integer.parseInt( matcher.group( 2 ) );
            int x2 = Integer.parseInt( matcher.group( 3 ) );
            int y2 = Integer.parseInt( matcher.group( 4 ) );
            int centerX = x2 - ( ( x2 - x1 ) / 2 );
            int centerY = y2 - ( ( y2 - y1 ) / 2 );
            int width = Integer.parseInt( swidth );
            int height = Integer.parseInt( sheight );
            scroll[1] = Math.max( 0, centerX - ( width / 2 ) );
            scroll[0] = Math.max( 0, centerY - ( height / 2 ) );
        }
        return scroll;
    }

    private void updateImageMap() {
        String imageMap = diagram.makeImageMap();
        // imageMap = imageMap.replace( "id=\"G\"", "id=\"" + getContainerId() + "\"" );
        imageMap = imageMap.replace( "id=\"G\"", "" );
        imageMap = imageMap.replace( "name=\"G\"", "name=\"" + getContainerId() + "\"" );
        imageMapHolder.replace( 0, imageMapHolder.length(), imageMap );
    }

    /**
     * Add a parameter to the URL to control browser caching.
     * @return a string
     */
    protected String makeSeed() {
        // LOG.info("***Seed = " + getCommander().getLastModified() );
        return "&_modified=" + getCommander().getLastModified();
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
     * Return the wicket id of the diagram's container.
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
     * @param graphId       a string
     * @param domIdentifier -- dom identifier of diagram container - can be null
     * @param scrollTop     where to scroll to top
     * @param scrollLeft    where to scroll to left                         in
     * @param target        an ajax request target
     */
    protected abstract void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target );

    /**
     * Vertex selected event.
     *
     * @param graphId       a string
     * @param vertexId      a string
     * @param domIdentifier -- dom identifier of diagram container - can be null
     * @param scrollTop     where to scroll to top
     * @param scrollLeft    where to scroll to left
     * @param target        an ajax request target
     */
    protected abstract void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target );

    /**
     * Edge selected event.
     *
     * @param graphId       a string
     * @param edgeId        a string
     * @param domIdentifier -- dom identifier of diagram container - can be null
     * @param scrollTop     where to scroll to top
     * @param scrollLeft    where to scroll to left
     * @param target        an ajax request target
     */
    protected abstract void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            AjaxRequestTarget target );


    /**
     * Append javascript to cause scrolling.
     *
     * @param domIdentifier a CSS path to a dom element
     * @param scrollTop     an int
     * @param scrollLeft    an int
     * @return a script that will cause scrolling
     */
    protected String scroll(
            String domIdentifier,
            int scrollTop,
            int scrollLeft ) {
        String script = null;
        if ( domIdentifier != null ) {
            // Timeout needed to let document fully update.
            script = "{ setTimeout(\"$('"
                    + domIdentifier
                    + "').scrollTop("
                    + scrollTop
                    + ").scrollLeft("
                    + scrollLeft
                    + ")\", 500) }";
        }
        return script;
    }

}
