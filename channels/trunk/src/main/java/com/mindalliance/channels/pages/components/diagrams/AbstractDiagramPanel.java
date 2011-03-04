package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.diagrams.DiagramAjaxBehavior;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract Diagram Panel.
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

    private static String[] STANDARD_ARGS = { "graph", "vertex", "edge", "width", "height" };

    /**
     * The flow diagram.
     */
    private Diagram diagram;

    /**
     * Diagram generation settings.
     */
    private final Settings settings;

    /**
     * IMage map holder.
     */
    private StringBuilder imageMapHolder;

    @SpringBean
    private Analyst analyst;


    @SpringBean
    private DiagramFactory diagramFactory;

    protected AbstractDiagramPanel( String id, Settings settings ) {
        super( id );
        this.settings = settings;
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public boolean isWithImageMap() {
        return settings.isUsingMap();
    }

    public double[] getDiagramSize() {
        return settings.getSize();
    }

    public String getOrientation() {
        return settings.getOrientation();
    }

    /**
     * Initialize.
     */
    protected void init() {
        diagram = makeDiagram();
        if ( isWithImageMap() ) {
            imageMapHolder = createMapHolder( analyst, diagramFactory );
            if ( imageMapHolder.toString().isEmpty() )
                LOG.warn( "Empty image map" );
            if ( isUsingAjax() )
                add( new DiagramAjaxBehavior( imageMapHolder, getDomIdentifier() ) {
                    @Override
                    protected void respond( AjaxRequestTarget target ) {
                        RequestCycle requestCycle = RequestCycle.get();
                        String graphId = requestCycle.getRequest().getParameter( "graph" );
                        String vertexId = requestCycle.getRequest().getParameter( "vertex" );
                        String edgeId = requestCycle.getRequest().getParameter( "edge" );
                        String width = requestCycle.getRequest().getParameter( "width" );
                        String height = requestCycle.getRequest().getParameter( "height" );
                        Map<String, String>extras = new HashMap<String,String>();
                        List<String> standardArgs = Arrays.asList( STANDARD_ARGS );
                        for ( String argName : requestCycle.getRequest().getParameterMap().keySet() ) {
                            if ( !standardArgs.contains( argName ) ) {
                                extras.put( argName, requestCycle.getRequest().getParameter( argName ));
                            }
                        }
                        if ( graphId != null ) {
                            if ( vertexId == null && edgeId == null ) {
                                onSelectGraph( graphId, getDomIdentifier(), 0, 0, extras, target );
                            } else if ( vertexId != null ) {
                                int[] scroll = calculateVertexScroll( vertexId, width, height );
                                onSelectVertex( graphId, vertexId,
                                        getDomIdentifier(), scroll[0], scroll[1], extras, target );
                            } else {
                                int[] scroll = calculateEdgeScroll( imageMapHolder, edgeId, width, height );
                                onSelectEdge( graphId, edgeId,
                                        getDomIdentifier(), scroll[0], scroll[1], extras, target );
                            }
                        }
                    }
                } );
        }

        MarkupContainer graph = new MarkupContainer( getContainerId() ) {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String url = makeDiagramUrl()
                        + makeSeed()
                        + AbstractChannelsWebPage.queryParameters( getPlan() );
                tag.put( "src", url );
                if ( isWithImageMap() ) {
                    // TODO may not be unique in the page but should be
                    tag.put( "usemap", "#" + getContainerId() );
                }
            }

            @Override
            protected void onRender( MarkupStream markupStream ) {
                super.onRender( markupStream );
                if ( isWithImageMap() ) {
                    try {
                        LOG.debug( "Rendering image map " );
                        if ( imageMapHolder.toString().isEmpty() )
                            LOG.warn( "Empty image map" );
                        getResponse().write( imageMapHolder.toString() );
                    } catch ( DiagramException e ) {
                        LOG.error( "Can't generate image map", e );
                    }
                }
            }
        };
        if ( isUsingAjax() ) {
            graph.add( new AjaxEventBehavior( "onclick" ) {
                @Override
                protected void onEvent( AjaxRequestTarget target ) {
                    onClick( target );
                }
            } );
            graph.setOutputMarkupId( true );
        }
        add( graph );
    }

    private int[] calculateVertexScroll(
            String vertexId, String swidth, String sheight ) {
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

    private StringBuilder createMapHolder( Analyst analyst, DiagramFactory diagramFactory ) {
        StringBuilder builder = new StringBuilder();
        String imageMap = diagram.makeImageMap( analyst, diagramFactory );
        // imageMap = imageMap.replace( "id=\"G\"", "id=\"" + getContainerId() + "\"" );
        imageMap = imageMap.replace( "id=\"G\"", "" );
        imageMap = imageMap.replace( "name=\"G\"", "name=\"" + getContainerId() + "\"" );
        builder.replace( 0, builder.length(), imageMap );
        return builder;
    }

    /**
     * Add a parameter to the URL to control browser caching.
     *
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
     * @param scrollLeft    where to scroll to left
     * @param extras        a map of string to string
     * @param target        an ajax request target
     */
    protected abstract void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target );

    /**
     * Vertex selected event.
     *
     * @param graphId       a string
     * @param vertexId      a string
     * @param domIdentifier -- dom identifier of diagram container - can be null
     * @param scrollTop     where to scroll to top
     * @param scrollLeft    where to scroll to left
     * @param extras        a map of string to string
     * @param target        an ajax request target
     */
    protected abstract void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target );

    /**
     * Edge selected event.
     *
     * @param graphId       a string
     * @param edgeId        a string
     * @param domIdentifier -- dom identifier of diagram container - can be null
     * @param scrollTop     where to scroll to top
     * @param scrollLeft    where to scroll to left
     * @param extras        a map of string to string
     * @param target        an ajax request target
     */
    protected abstract void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
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

    public boolean isUsingAjax() {
        return settings.isUsingAjax();
    }

    public String getDomIdentifier() {
        return settings.getDomIdentifier();
    }
}
