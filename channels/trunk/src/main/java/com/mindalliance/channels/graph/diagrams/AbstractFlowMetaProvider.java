package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 22, 2010
 * Time: 3:14:04 PM
 */
public abstract class AbstractFlowMetaProvider<V extends Node, E>
        extends AbstractMetaProvider<V, E> {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractFlowMetaProvider.class );

    /**
     * Color for subgraph contour
     */
    protected static final String SUBGRAPH_COLOR = "#e6e9ed";

    /**
     * Font for subgraph labels.
     */
    protected static final String SUBGRAPH_FONT = "Helvetica-Bold";

    /**
     * Font size for subgraph labels.
     */
    protected static final String SUBGRAPH_FONT_SIZE = "10";

    /**
     * Font for node labels
     */
    public static final String NODE_FONT = "AvantGarde-Book";

    /**
     * Font size for node labels.
     */
    public static final String NODE_FONT_SIZE = "8";

    /**
     * Distance for edge head and tail labels.
     */
    protected static final String LABEL_DISTANCE = "2.0";

    /**
     * Distance for edge head and tail labels.
     */
    protected static final String LABEL_ANGLE = "45";

    /**
     * Highlight pen width.
     */
    protected static final String HIGHLIGHT_PENWIDTH = "2.0";

    /**
     * Highlight pen color.
     */
    protected static final String HIGHLIGHT_COLOR = "#C25036";

    /**
     * Font of highlighted node.
     */
    protected static final String HIGHLIGHT_NODE_FONT = "AvantGarde-Book";
    /**
     * Color for implied flows.
     */
    protected static final String OVERRIDDEN_COLOR = "gray30";

    /**
     * Segment in context.
     */
    private ModelObject context;

    /**
     * Whether to show goals.
     */
    private boolean showingGoals;

    /**
     * Whether to show connectors.
     */
    private boolean showingConnectors;

    private boolean hidingNoop;

    protected AbstractFlowMetaProvider( ModelObject modelObject, String outputFormat, Resource imageDirectory, Analyst analyst,
                                        boolean showingGoals, boolean showingConnectors, boolean hidingNoop,
                                        QueryService queryService ) {
        super( outputFormat, imageDirectory, analyst, queryService );
        this.context = modelObject;
        this.showingGoals = showingGoals;
        this.showingConnectors = showingConnectors;
        this.hidingNoop = hidingNoop;
    }

    public boolean isShowingGoals() {
        return showingGoals;
    }

    public boolean isShowingConnectors() {
        return showingConnectors;
    }

    public boolean isHidingNoop() {
        return hidingNoop;
    }

    /**
     * Get context provisioned from.
     *
     * @return an object that knows of the vertices and edges
     */
    @Override
    public Object getContext() {
        return context;
    }

    @Override
    public VertexNameProvider<V> getVertexLabelProvider() {
        return new VertexNameProvider<V>() {
            @Override
            public String getVertexName( Node node ) {
                String label = getNodeLabel( node ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider<V> getVertexIDProvider() {
        return new VertexNameProvider<V>() {
            @Override
            public String getVertexName( V vertex ) {
                return String.valueOf( vertex.getId() );
            }
        };
    }

    protected String getNodeLabel( Node node ) {
        if ( node.isPart() ) {
            Part part = (Part) node;
            return getQueryService().getFullTitle( "|", part );
        } else {
            return "c";
        }
    }

    public static String getDefaultActor() {
        return Part.DEFAULT_ACTOR;
    }

    protected static String listActors( List<Actor> partActors ) {
        Iterator<Actor> actors = partActors.iterator();
        if ( !actors.hasNext() ) {
            return "no one";
        } else {
            StringBuilder sb = new StringBuilder();
            while ( actors.hasNext() ) {
                sb.append( actors.next().getName() );
                if ( actors.hasNext() )
                    sb.append( ", " );
            }
            return sb.toString();
        }
    }

    protected String getIcon( CommunityService communityService, ImagingService imagingService, Node node ) {
        String imagesDirName;
        String negated = "";
        try {
            imagesDirName = getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }

        int numLines = 0;
        String iconName;
        if ( node.isConnector() ) {
            Flow flow = ( (Connector) node ).getInnerFlow();
            boolean satisfaction = flow.isNeed() && flow.isSatisfied()
                    || flow.isCapability() && flow.isSatisfying();

            iconName = imagesDirName +
                    ( hidingNoop && getAnalyst().isEffectivelyConceptual( getQueryService(), flow ) ? "/connector_blank"
                            : satisfaction ? "/connector"
                            : "/connector_red" );
        }

        // node is a part
        else {
            String[] lines = getNodeLabel( node ).split( "\\|" );
            numLines = Math.min( lines.length, 5 );
            Part part = (Part) node;
            if ( hidingNoop && getAnalyst().isEffectivelyConceptual( getQueryService(), part ) )
                iconName = "blank";
            else {
                negated = !getPlan().isViewableByAll() && getAnalyst().isEffectivelyConceptual( getQueryService(),
                                                                                           part ) // todo - this has failed with NullPointerException on timed update - why?
                                        ? ImagingService.NEGATED
                                        : "";
                iconName = imagingService.findIconName(
                        communityService,
                        part,
                        getQueryService().getAssignments() );
            }
        }
        String name = iconName + ( numLines > 0 ? numLines : "" ) + negated + ".png";
        if ( !new File( name ).canRead() ) {
            LOG.warn( "Icon file not found " + name );
        }
        return name;
    }
}
