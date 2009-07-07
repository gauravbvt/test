package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.analysis.graph.HierarchyRelationship;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Role;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Hierarchy diagram meta provider.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 6, 2009
 * Time: 4:07:33 PM
 */
public class HierarchyMetaProvider extends AbstractMetaProvider {

    /**
     * Font for node labels
     */
    private static final String ENTITY_FONT = "Arial";
    /**
     * Font size for node labels.
     */
    private static final String ENTITY_FONT_SIZE = "10";

    public HierarchyMetaProvider(
            String outputFormat,
            Resource imageDirectory,
            Analyst analyst ) {
        super( outputFormat, imageDirectory, analyst );
    }

    public Object getContext() {
        return Channels.getPlan();
    }

    public URLProvider getURLProvider() {
        return new URLProvider<Hierarchical, HierarchyRelationship>() {

            public String getGraphURL( Hierarchical vertex ) {
                return null;
            }

            public String getVertexURL( Hierarchical hierarchical ) {
                Object[] args = {0, hierarchical.getId()};
                return MessageFormat.format( VERTEX_URL_FORMAT, args );
            }

            public String getEdgeURL( HierarchyRelationship hierarchyRelationship ) {
                return null;
            }
        };
    }

    public EdgeNameProvider getEdgeLabelProvider() {
        return new EdgeNameProvider<HierarchyRelationship>() {
            public String getEdgeName( HierarchyRelationship hierarchyRelationship ) {
                return "";
            }
        };
    }

    public VertexNameProvider getVertexLabelProvider() {
        return new VertexNameProvider<Hierarchical>() {
            public String getVertexName( Hierarchical hierarchical ) {
                String label = getIdentifiableLabel( hierarchical ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    public VertexNameProvider getVertexIDProvider() {
        return new VertexNameProvider<Hierarchical>() {
            public String getVertexName( Hierarchical hierarchical ) {
                return "" + hierarchical.getId();
            }
        };
    }

    public DOTAttributeProvider getDOTAttributeProvider() {
        return new HierarchyDOTAttributeProvider();
    }

    /**
     * {@inheritDoc}
     */
    public String getGraphOrientation() {
        // Hierarchies always displayed top down.
        return "TB";
    }

    private class HierarchyDOTAttributeProvider implements DOTAttributeProvider<Hierarchical, HierarchyRelationship> {
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        public List<DOTAttribute> getSubgraphAttributes() {
            return DOTAttribute.emptyList();
        }

        public List<DOTAttribute> getVertexAttributes( Hierarchical vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( vertex ) ) );
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
            if ( getAnalyst().hasUnwaivedIssues( (ModelObject) vertex, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip", sanitize( getAnalyst().getIssuesSummary( (ModelObject) vertex,
                        Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
            }
            return list;
        }


        public List<DOTAttribute> getEdgeAttributes( HierarchyRelationship edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowhead", "none" ) );
            //  list.add( new DOTAttribute( "arrowsize", "0.75" ) );
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

        private String getIcon( Hierarchical modelObject ) {
            String iconName;
            int numLines;
            String label = getIdentifiableLabel( modelObject );
            String[] lines = label.split( "\\|" );
            numLines = Math.min( lines.length, 3 );
            if ( modelObject instanceof Actor ) {
                boolean isSystem = modelObject.getName().toLowerCase().contains( "system" );
                iconName = isSystem ? "system" : "person";
            } else if ( modelObject instanceof Role ) {
                boolean isSystem = modelObject.getName().toLowerCase().contains( "system" );
                iconName = isSystem ? "system" : "role";
            } else if ( modelObject instanceof Organization ) {
                iconName = "organization";
            } else {
                iconName = "unknown";
            }
            String dirName;
            try {
                dirName = getImageDirectory().getFile().getAbsolutePath();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to get image directory location", e );
            }
            return dirName
                    + "/" + iconName + ( numLines > 0 ? numLines : "" ) + ".png";
        }

    }

}
