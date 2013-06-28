package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.text.MessageFormat;
import java.util.List;

/**
 * Required networking meta provider.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 5:05 PM
 */
public class RequiredNetworkingMetaProvider extends AbstractMetaProvider<Agency, RequirementRelationship> {

    /**
     * Font for node labels.
     */
    private static final String ORG_FONT = "Arial";

    /**
     * Font size for node labels.
     */
    private static final String ORG_FONT_SIZE = "10";

    private final Agency selectedAgency;
    private final RequirementRelationship selectedRequirementRel;
    private final Phase.Timing timing;
    private final Event event;
    private static final String REQUIREMENT_EDGE_URL_FORMAT = "?graph={0,number,0}&edge={1}";

    public RequiredNetworkingMetaProvider(
            Agency selectedAgency,
            RequirementRelationship selectedRequirementRel,
            Phase.Timing timing,
            Event event,
            String outputFormat,
            Resource imageDirectory,
            CommunityService communityService ) {
        super( outputFormat, imageDirectory, communityService.getAnalyst(), communityService.getPlanService() );
        this.selectedAgency = selectedAgency;
        this.selectedRequirementRel = selectedRequirementRel;
        this.timing = timing;
        this.event = event;
    }

    @Override
    public Object getContext() {
        return getQueryService().getPlan();
    }

    @Override
    public URLProvider<Agency, RequirementRelationship> getURLProvider() {
        return new URLProvider<Agency, RequirementRelationship>() {
            @Override
            public String getGraphURL( Agency agency ) {
                return null;
            }

            @Override
            public String getVertexURL( Agency agency ) {
                Object[] args = {0, agency.getUid()};
                return MessageFormat.format( AGENCY_VERTEX_URL_FORMAT, args );
            }

            @Override
            public String getEdgeURL( RequirementRelationship reqRel ) {
                // Plan id = 0 for now since there is only one plan
                Object[] args = {0, reqRel.getRelationshipId()};
                return MessageFormat.format( REQUIREMENT_EDGE_URL_FORMAT, args );
            }
        };
    }

    @Override
    public EdgeNameProvider<RequirementRelationship> getEdgeLabelProvider() {
        return new EdgeNameProvider<RequirementRelationship>() {
            @Override
            public String getEdgeName( RequirementRelationship reqRel ) {
                int count = reqRel.getRequirements().size();
                return Integer.toString( count );
            }
        };
    }

    @Override
    public VertexNameProvider<Agency> getVertexLabelProvider() {
        return new VertexNameProvider<Agency>() {
            @Override
            public String getVertexName( Agency agency ) {
                String label = getIdentifiableLabel( agency ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider<Agency> getVertexIDProvider() {
        return new VertexNameProvider<Agency>() {
            @Override
            public String getVertexName( Agency agency ) {
                return "_" + agency.getUid() ; // make sure it does not begin with a digit
            }
        };
    }

    @Override
    public DOTAttributeProvider<Agency, RequirementRelationship> getDOTAttributeProvider() {
        return new RequirementNetworkingDOTAttributeProvider();
    }

    private class RequirementNetworkingDOTAttributeProvider implements DOTAttributeProvider<Agency, RequirementRelationship> {
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
        public List<DOTAttribute> getVertexAttributes( CommunityService communityService, Agency vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( communityService, getAnalyst().getImagingService(), vertex ) ) );
            list.add( new DOTAttribute( "labelloc", "b" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "shape", "box" ) );
                list.add( new DOTAttribute( "style", "solid" ) );
                list.add( new DOTAttribute( "color", "gray" ) );
            } else
                list.add( new DOTAttribute( "shape", "none" ) );
            list.add( new DOTAttribute( "fontsize", ORG_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", ORG_FONT ) );
            if ( communityService.getParticipationAnalyst().hasIssues( vertex, communityService ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "tooltip",
                        sanitize( communityService.getParticipationAnalyst().getIssuesOverview(
                                vertex,
                                communityService ) ) ) );
            } else {
                list.add( new DOTAttribute( "tooltip", vertex.getDescription() ) );
            }
            return list;
        }

        private String getIcon( CommunityService communityService, ImagingService service, Agency agency ) {
            String iconName;
            String label = getIdentifiableLabel( agency );
            String[] lines = label.split( "\\|" );
            int numLines = Math.min( lines.length, 3 );
            iconName = service.findIconName(
                    communityService,
                    agency.getPlanOrganization() == null
                            ? agency
                            : agency.getPlanOrganization() );
            return iconName + ( numLines > 0 ? numLines : "" ) + ".png";
        }


        @Override
        public List<DOTAttribute> getEdgeAttributes( CommunityService communityService,
                                                     RequirementRelationship edge,
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
            if ( edge.hasUnfulfilledRequirements( timing, event, communityService ) ) {
                list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                list.add( new DOTAttribute( "color", COLOR_ERROR ) );
            }
            int count = edge.getRequirements().size();
            list.add( new DOTAttribute(
                    "tooltip",
                    count + ( count > 1 ? " requirements apply" : " requirement applies" ) ) );
            return list;
        }
    }
}
