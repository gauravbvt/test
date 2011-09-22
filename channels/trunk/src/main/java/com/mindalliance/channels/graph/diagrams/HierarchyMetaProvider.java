/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelObject;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Hierarchy diagram meta provider.
 */
public class HierarchyMetaProvider extends AbstractMetaProvider {

    /**
     * Font for node labels.
     */
    private static final String ENTITY_FONT = "Arial";

    /**
     * Font size for node labels.
     */
    private static final String ENTITY_FONT_SIZE = "10";

    public HierarchyMetaProvider( String outputFormat, Resource imageDirectory, Analyst analyst,
                                  QueryService queryService ) {
        super( outputFormat, imageDirectory, analyst, queryService );
    }

    @Override
    public Object getContext() {
        return getPlan();
    }

    @Override
    public URLProvider getURLProvider() {
        return new URLProvider<Hierarchical, HierarchyRelationship>() {

            @Override
            public String getGraphURL( Hierarchical vertex ) {
                return null;
            }

            @Override
            public String getVertexURL( Hierarchical hierarchical ) {
                Object[] args = { 0, hierarchical.getId() };
                return MessageFormat.format( VERTEX_URL_FORMAT, args );
            }

            @Override
            public String getEdgeURL( HierarchyRelationship hierarchyRelationship ) {
                return null;
            }
        };
    }

    @Override
    public EdgeNameProvider getEdgeLabelProvider() {
        return new EdgeNameProvider<HierarchyRelationship>() {
            @Override
            public String getEdgeName( HierarchyRelationship hierarchyRelationship ) {
                return "";
            }
        };
    }

    @Override
    public VertexNameProvider getVertexLabelProvider() {
        return new VertexNameProvider<Hierarchical>() {
            @Override
            public String getVertexName( Hierarchical hierarchical ) {
                String label = getIdentifiableLabel( hierarchical ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider getVertexIDProvider() {
        return new VertexNameProvider<Hierarchical>() {
            @Override
            public String getVertexName( Hierarchical hierarchical ) {
                return "" + hierarchical.getId();
            }
        };
    }

    @Override
    public DOTAttributeProvider getDOTAttributeProvider() {
        return new HierarchyDOTAttributeProvider();
    }

    @Override
    public String getGraphOrientation() {
        // Hierarchies always displayed top down.
        return "TB";
    }

    private class HierarchyDOTAttributeProvider implements DOTAttributeProvider<Hierarchical, HierarchyRelationship> {

        @Override
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
                list.add( new DOTAttribute( "overlap", "false" ) );
                // list.add( new DOTAttribute( "mode", "hier" ) );
                // list.add( new DOTAttribute( "sep", "+100,100" ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            return DOTAttribute.emptyList();
        }

        @Override
        public List<DOTAttribute> getVertexAttributes( QueryService queryService, Hierarchical vertex,
                                                       boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( getAnalyst().getImagingService(), (ModelObject) vertex ) ) );
            list.add( new DOTAttribute( "labelloc", "b" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "shape", "box" ) );
                list.add( new DOTAttribute( "style", "solid" ) );
                list.add( new DOTAttribute( "color", "gray" ) );
            } else {
                list.add( new DOTAttribute( "shape", "none" ) );
            }
            list.add( new DOTAttribute( "fontsize", ENTITY_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", ENTITY_FONT ) );
            if ( getAnalyst().hasUnwaivedIssues( queryService,
                                                 (ModelObject) vertex,
                                                 Analyst.INCLUDE_PROPERTY_SPECIFIC ) )
            {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip",
                                            sanitize( getAnalyst().getIssuesSummary( queryService,
                                                                                     (ModelObject) vertex,
                                                                                     Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( QueryService queryService, HierarchyRelationship edge,
                                                     boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowhead", "none" ) );
            list.add( new DOTAttribute( "arrowtail", "open" ) );
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "penwidth", "3.0" ) );
            }
            return list;
        }

        private String getIcon( ImagingService imagingService, ModelObject modelObject ) {
            String iconName;
            String imagesDirName;
            try {
                imagesDirName = getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            String label = getIdentifiableLabel( modelObject );
            String[] lines = label.split( "\\|" );
            int numLines = Math.min( lines.length, 3 );
            iconName = imagingService.findIconName( getPlan(), modelObject );
            return iconName + ( numLines > 0 ? numLines : "" ) + ".png";
        }
    }
}
