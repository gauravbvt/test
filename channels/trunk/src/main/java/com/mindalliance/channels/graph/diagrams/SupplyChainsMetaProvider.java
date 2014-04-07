package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.AssignmentAssetLink;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 9:58 PM
 */
public class SupplyChainsMetaProvider extends AbstractMetaProvider<Assignment, AssignmentAssetLink> {

    private final MaterialAsset assetFocus;

    /**
     * Color for subgraph contour.
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
     * Font for node labels.
     */
    public static final String NODE_FONT = "Arial";

    /**
     * Font size for node labels.
     */
    public static final String NODE_FONT_SIZE = "10";

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

    // Extra arguments MUST start with '_'
    private static final String SUPPLY_VERTEX_URL_FORMAT =
            "?graph={0,number,0}&vertex={1,number,0}&_actor={2,number,0}&_role={3,number,0}&_org={4,number,0}";

    public SupplyChainsMetaProvider( MaterialAsset assetFocus,
                                     String outputFormat,
                                     Resource imageDirectory,
                                     Analyst analyst,
                                     CommunityService communityService ) {
        super( outputFormat, imageDirectory, analyst, communityService );
        this.assetFocus = assetFocus;
    }


    @Override
    public Object getContext() {
        return getCommunityService().getPlanCommunity();
    }

    @Override
    public URLProvider<Assignment, AssignmentAssetLink> getURLProvider() {
        return new URLProvider<Assignment, AssignmentAssetLink>() {
            /**
             * The URL for the graph that contains the vertex
             *
             * @param node -- a vertex
             * @return a URL string
             */
            @Override
            public String getGraphURL( Assignment node ) {
                // Plan id = 0 since there is only one plan
                Object[] args = {0};
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            }

            /**
             * The vertex's URL. Returns null if none.
             *
             * @param assignment -- a vertex
             * @return a URL string
             */
            @Override
            public String getVertexURL( Assignment assignment ) {
                Part part = assignment.getPart();
                Actor actor = assignment.getActor();
                Role role = assignment.getRole();
                Organization organization = assignment.getOrganization();
                Object[] args = {
                        part.getSegment().getId(), part.getId(),
                        actor == null || actor.isUnknown() ? 0 : actor.getId(),
                        role == null || role.isUnknown() ? 0 : role.getId(),
                        organization == null || organization.isUnknown() ? 0 : organization.getId()
                };
                return MessageFormat.format( SUPPLY_VERTEX_URL_FORMAT, args );
            }

            /**
             * The edges's URL. Returns null if none.
             *
             * @param assignmentAssetLink -- an edge
             * @return a URL string
             */
            @Override
            public String getEdgeURL( AssignmentAssetLink assignmentAssetLink ) {
                return null;
            }
        };
    }

    @Override
    public DOTAttributeProvider<Assignment, AssignmentAssetLink> getDOTAttributeProvider() {
        return new SupplyChainsDOTAttributeProvider();
    }

    @Override
    public EdgeNameProvider<AssignmentAssetLink> getEdgeLabelProvider() {
        return new EdgeNameProvider<AssignmentAssetLink>() {
            @Override
            public String getEdgeName( AssignmentAssetLink assignmentAssetLink ) {
                String label = AbstractMetaProvider.separate(
                        assignmentAssetLink.getMaterialAsset().getName(), LINE_WRAP_SIZE )
                        .replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider<Assignment> getVertexLabelProvider() {
        return new VertexNameProvider<Assignment>() {
            @Override
            public String getVertexName( Assignment assignment ) {
                String label = assignment.getFullTitle( "|" ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider<Assignment> getVertexIDProvider() {
        return new VertexNameProvider<Assignment>() {
            @Override
            public String getVertexName( Assignment assignment ) {
                return sanitizeToId( assignment.getFullTitle( "|" ) ) + String.valueOf( assignment.getPart().getId() );
            }
        };
    }

    /**
     * A DOTAttributeProvider for segments.
     */
    private class SupplyChainsDOTAttributeProvider implements DOTAttributeProvider<Assignment, AssignmentAssetLink> {

        private SupplyChainsDOTAttributeProvider() {
        }

        @Override
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
            }
            return list;
        }

        /**
         * Gets semi-colon-separated style declarations for subgraphs.
         *
         * @return the style declarations
         */
        @Override
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) { // not used
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", SUBGRAPH_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SUBGRAPH_FONT ) );
            list.add( new DOTAttribute( "color", SUBGRAPH_COLOR ) );
            list.add( new DOTAttribute( "style", "filled" ) );
            return list;
        }

        @Override
        public List<DOTAttribute> getVertexAttributes( CommunityService communityService, Assignment assignment,
                                                       boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            if ( getOutputFormat().equalsIgnoreCase( DiagramFactory.SVG ) ) {
                list.add( new DOTAttribute( "shape", "box" ) );
                // assuming a bitmap format
            } else {
                list.add( new DOTAttribute( "image",
                        getIcon( communityService, SupplyChainsMetaProvider.this.getAnalyst().getImagingService(),
                                assignment )
                ) );
                list.add( new DOTAttribute( "labelloc", "b" ) );
                if ( highlighted ) {
                    list.add( new DOTAttribute( "shape", "box" ) );
                    list.add( new DOTAttribute( "style", "solid" ) );
                    list.add( new DOTAttribute( "color", HIGHLIGHT_COLOR ) );
                    list.add( new DOTAttribute( "penwidth", HIGHLIGHT_PENWIDTH ) );
                    list.add( new DOTAttribute( "fontname", HIGHLIGHT_NODE_FONT ) );
                } else {
                    list.add( new DOTAttribute( "shape", "none" ) );
                    list.add( new DOTAttribute( "fontname", NODE_FONT ) );
                }
            }
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", NODE_FONT_SIZE ) );
            String tooltip = assignment.getPart().getTitle();
            list.add( new DOTAttribute( "tooltip", tooltip ) );
            return list;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( CommunityService communityService, AssignmentAssetLink edge, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "label", getEdgeLabel( edge, highlighted ) ) );
            list.add( new DOTAttribute( "color", "steelblue" ) );
            list.add( new DOTAttribute( "arrowhead", edge.isSupplyCommitment() ? "normal" : "vee" ) );
            list.add( new DOTAttribute( "style", edge.isSupplyCommitment() ? "solid" : "dotted" ) );
            list.add( new DOTAttribute( "arrowsize", edge.isSupplyCommitment() ? "0.75" : "0.5") );
            list.add( new DOTAttribute( "fontname", AbstractMetaProvider.EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", AbstractMetaProvider.EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "dimgray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            list.add( new DOTAttribute( "penwidth", "1.0" ) );
            return list;
        }

        protected String getEdgeLabel( AssignmentAssetLink assignmentAssetLink, boolean highlighted ) {
            String text = assignmentAssetLink.getMaterialAsset().getName()
                    + ( assignmentAssetLink.isSupplyCommitment() ? " supplied" : " available");
            String label = AbstractMetaProvider.separate(
                    text,
                    LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
        }

        protected String getIcon( CommunityService communityService, ImagingService imagingService, Assignment assignment ) {
            String iconName;
            String[] lines = assignment.getFullTitle( "|" ).split( "\\|" );
            int numLines = Math.min( lines.length, 5 );
            Part part = assignment.getPart();
            String uses = !part.findAssetsUsed().isEmpty()
                    ? ImagingService.USES
                    : "";
            String produces = !part.getAssetConnections().producing().isEmpty()
                    ? ImagingService.PRODUCES
                    : "";
            iconName = imagingService.findIconName( communityService, assignment );

            return iconName + ( numLines > 0 ? numLines : "" ) + uses + produces + ".png";
        }
    }
}