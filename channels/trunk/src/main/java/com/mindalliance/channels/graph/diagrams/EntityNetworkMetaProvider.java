package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.ModelEntity;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
/**
 * Entity network meta provider.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 8:33:28 PM
 */
public class EntityNetworkMetaProvider extends AbstractMetaProvider {

    /**
     * Font for node labels
     */
    private static final String ENTITY_FONT = "Arial";
    /**
     * Font size for node labels.
     */
    private static final String ENTITY_FONT_SIZE = "10";

    public EntityNetworkMetaProvider(
            String outputFormat,
            Resource imageDirectory,
            Analyst analyst ) {
        super( outputFormat, imageDirectory, analyst );
    }

    public Object getContext() {
        return getPlan();
    }

    public URLProvider getURLProvider() {
        return new URLProvider<ModelEntity, EntityRelationship>() {

            public String getGraphURL( ModelEntity vertex ) {
                return null;
            }

            public String getVertexURL( ModelEntity entity ) {
                if ( entity.isUnknown() ) {
                    return null;
                } else {
                    Object[] args = {0, entity.getId()};
                    return MessageFormat.format( VERTEX_URL_FORMAT, args );
                }
            }

            public String getEdgeURL( EntityRelationship entityRelationship ) {
                // Plan id = 0 for now sice there is only one plan
                Object[] args = {0, entityRelationship.getId()};
                return MessageFormat.format( EDGE_URL_FORMAT, args );
            }
        };
    }

    public EdgeNameProvider getEdgeLabelProvider() {
        return new EdgeNameProvider<EntityRelationship>() {
            public String getEdgeName( EntityRelationship entityRelationship ) {
                int count = entityRelationship.getFlows().size();
                return count + ( count > 1 ? " flows" : " flow" );
            }
        };
    }

    public VertexNameProvider getVertexLabelProvider() {
        return new VertexNameProvider<ModelEntity>() {
            public String getVertexName( ModelEntity entity ) {
                String label = getIdentifiableLabel( entity ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    public VertexNameProvider getVertexIDProvider() {
        return new VertexNameProvider<ModelEntity>() {
            public String getVertexName( ModelEntity entity ) {
                return "" + entity.getId();
            }
        };
    }

    public DOTAttributeProvider getDOTAttributeProvider() {
        return new NetwordDOTAttributeProvider();
    }

    private class NetwordDOTAttributeProvider implements DOTAttributeProvider<ModelEntity, EntityRelationship> {
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            return DOTAttribute.emptyList();
        }

        public List<DOTAttribute> getVertexAttributes( ModelEntity vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( getAnalyst().getImagingService(), vertex ) ) );
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
            if ( getAnalyst().hasUnwaivedIssues( vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }


        public List<DOTAttribute> getEdgeAttributes( EntityRelationship edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowhead", "vee" ) );
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "penwidth", "3.0" ) );
            }
            // Issue coloring
            if ( edge.hasIssues( getAnalyst() ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( edge.getIssuesSummary( getAnalyst() ) ) ) );
            }
            return list;
        }

        private String getIcon( ImagingService service, ModelEntity entity ) {
            String iconName;
            String imagesDirName;
            try {
                imagesDirName = getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            String label = getIdentifiableLabel( entity );
            String[] lines = label.split( "\\|" );
            int numLines = Math.min( lines.length, 3 );
            iconName = service.findIconName( entity );
            return iconName + ( numLines > 0 ? numLines : "" ) + ".png";
        }

    }
}
