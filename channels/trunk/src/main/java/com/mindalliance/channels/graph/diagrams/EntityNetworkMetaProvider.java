/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.core.model.ModelEntity;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Entity network meta provider.
 */
public class EntityNetworkMetaProvider extends AbstractMetaProvider {

    /**
     * Font for node labels.
     */
    private static final String ENTITY_FONT = "Arial";

    /**
     * Font size for node labels.
     */
    private static final String ENTITY_FONT_SIZE = "10";

    public EntityNetworkMetaProvider( String outputFormat, Resource imageDirectory, Analyst analyst,
                                      QueryService queryService ) {
        super( outputFormat, imageDirectory, analyst, queryService );
    }

    @Override
    public Object getContext() {
        return getPlan();
    }

    @Override
    public URLProvider getURLProvider() {
        return new URLProvider<ModelEntity, EntityRelationship>() {

            @Override
            public String getGraphURL( ModelEntity vertex ) {
                return null;
            }

            @Override
            public String getVertexURL( ModelEntity entity ) {
                if ( entity.isUnknown() ) {
                    return null;
                } else {
                    Object[] args = { 0, entity.getId() };
                    return MessageFormat.format( VERTEX_URL_FORMAT, args );
                }
            }

            @Override
            public String getEdgeURL( EntityRelationship entityRelationship ) {
                // Plan id = 0 for now since there is only one plan
                Object[] args = { 0, entityRelationship.getId() };
                return MessageFormat.format( EDGE_URL_FORMAT, args );
            }
        };
    }

    @Override
    public EdgeNameProvider getEdgeLabelProvider() {
        return new EdgeNameProvider<EntityRelationship>() {
            @Override
            public String getEdgeName( EntityRelationship entityRelationship ) {
                int count = entityRelationship.getFlows().size();
                return count + ( count > 1 ? " flows" : " flow" );
            }
        };
    }

    @Override
    public VertexNameProvider getVertexLabelProvider() {
        return new VertexNameProvider<ModelEntity>() {
            @Override
            public String getVertexName( ModelEntity entity ) {
                String label = getIdentifiableLabel( entity ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider getVertexIDProvider() {
        return new VertexNameProvider<ModelEntity>() {
            @Override
            public String getVertexName( ModelEntity entity ) {
                return String.valueOf( entity.getId() );
            }
        };
    }

    @Override
    public DOTAttributeProvider getDOTAttributeProvider() {
        return new NetwordDOTAttributeProvider();
    }

    private class NetwordDOTAttributeProvider implements DOTAttributeProvider<ModelEntity, EntityRelationship> {

        @Override
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            list.add( new DOTAttribute( "overlap", "false" ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            return DOTAttribute.emptyList();
        }

        @Override
        public List<DOTAttribute> getVertexAttributes( QueryService queryService, ModelEntity vertex,
                                                       boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( getAnalyst().getImagingService(), vertex ) ) );
            list.add( new DOTAttribute( "labelloc", "b" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "shape", "box" ) );
                list.add( new DOTAttribute( "style", "solid" ) );
                list.add( new DOTAttribute( "color", "gray" ) );
            } else
                list.add( new DOTAttribute( "shape", "none" ) );
            list.add( new DOTAttribute( "fontsize", ENTITY_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", ENTITY_FONT ) );
            if ( !getPlan().isTemplate() && getAnalyst().hasUnwaivedIssues( queryService,
                                                                            vertex,
                                                                            Analyst.INCLUDE_PROPERTY_SPECIFIC ) )
            {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip",
                                            sanitize( getAnalyst().getIssuesSummary( queryService,
                                                                                     vertex,
                                                                                     Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( QueryService queryService, EntityRelationship edge,
                                                     boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowhead", "vee" ) );
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            if ( highlighted )
                list.add( new DOTAttribute( "penwidth", "3.0" ) );
            if ( !getPlan().isTemplate() && edge.hasIssues( getAnalyst(), queryService ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip",
                                            sanitize( edge.getIssuesSummary( getAnalyst(), queryService ) ) ) );
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
            iconName = service.findIconName( getPlan(), entity );
            return iconName + ( numLines > 0 ? numLines : "" ) + ".png";
        }
    }
}
