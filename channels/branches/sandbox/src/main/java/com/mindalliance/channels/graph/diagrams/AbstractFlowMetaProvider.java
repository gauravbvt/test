package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

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
abstract public class AbstractFlowMetaProvider<V extends Node,E> extends AbstractMetaProvider<V, E> {
    /**
      * Color for subgraph contour
      */
     protected static final String SUBGRAPH_COLOR = "azure2";
     /**
      * Font for subgraph labels.
      */
     protected static final String SUBGRAPH_FONT = "Arial Bold Oblique";
     /**
      * Font size for subgraph labels.
      */
     protected static final String SUBGRAPH_FONT_SIZE = "10";
     /**
      * Font for node labels
      */
     public static final String NODE_FONT = "Arial";
     /**
      * Font size for node labels.
      */
     public static final String NODE_FONT_SIZE = "10";
     /**
      * Distance for edge head and tail labels.
      */
     protected static final String LABEL_DISTANCE = "1.0";
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
     protected static final String HIGHLIGHT_COLOR = "gray";
     /**
      * Font of highlighted node.
      */
     protected static final String HIGHLIGHT_NODE_FONT = "Arial Bold";
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

    public AbstractFlowMetaProvider( ModelObject modelObject,
                                String outputFormat,
                                Resource imageDirectory,
                                Analyst analyst ) {
        this( modelObject, outputFormat, imageDirectory, analyst, false, false );
    }

    public AbstractFlowMetaProvider( ModelObject modelObject,
                                String outputFormat,
                                Resource imageDirectory,
                                Analyst analyst,
                                boolean showingGoals,
                                boolean showingConnectors ) {
        super( outputFormat, imageDirectory, analyst );
        this.context = modelObject;
        this.showingGoals = showingGoals;
        this.showingConnectors = showingConnectors;
    }

    public boolean isShowingGoals() {
        return showingGoals;
    }

    public boolean isShowingConnectors() {
        return showingConnectors;
    }

    /**
     * Get context provisioned from.
     *
     * @return an object that knows of the vertices and edges
     */
    public Object getContext() {
        return context;
    }

    /**
      * {@inheritDoc}
      */
     public VertexNameProvider<V> getVertexLabelProvider() {
         return new VertexNameProvider<V>() {
             public String getVertexName( Node node ) {
                 String label = getNodeLabel( node ).replaceAll( "\\|", "\\\\n" );
                 return sanitize( label );
             }
         };
     }

     /**
      * {@inheritDoc}
      */
     public VertexNameProvider<V> getVertexIDProvider() {
         return new VertexNameProvider<V>() {
             public String getVertexName( V node ) {
                 return "" + node.getId();
             }
         };
     }

     protected String getNodeLabel( com.mindalliance.channels.model.Node node ) {
         if ( node.isPart() ) {
             Part part = (Part) node;
             return part.getFullTitle( "|", getAnalyst().getQueryService() );
         } else {
             return "c";
         }
     }

     public static String getDefaultActor() {
         return Part.DEFAULT_ACTOR;
     }

    protected String listActors( List<Actor> partActors ) {
        StringBuilder sb = new StringBuilder();
        Iterator<Actor> actors = partActors.iterator();
        while ( actors.hasNext() ) {
            sb.append( actors.next().getName() );
            if ( actors.hasNext() ) sb.append( ", " );
        }
        return sb.toString();
    }

    protected String getIcon( ImagingService imagingService, com.mindalliance.channels.model.Node node ) {
        String iconName;
        int numLines = 0;
        String imagesDirName;
        try {
            imagesDirName = getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        if ( node.isConnector() ) {
            Connector connector = (Connector) node;
            Flow flow = connector.getInnerFlow();
            if ( flow.isNeed() && flow.isSatisfied()
                    || flow.isCapability() && flow.isSatisfying() ) {
                iconName = imagesDirName + "/connector";
            } else {
                iconName = imagesDirName + "/connector_red";
            }
        }
        // node is a part
        else {
            String label = getNodeLabel( node );
            String[] lines = label.split( "\\|" );
            numLines = Math.min( lines.length, 5 );
            Part part = (Part) node;
            iconName = imagingService.findIconName( part, imagesDirName, getAnalyst().getQueryService() );
        }
        return iconName + ( numLines > 0 ? numLines : "" ) + ".png";
    }



}
