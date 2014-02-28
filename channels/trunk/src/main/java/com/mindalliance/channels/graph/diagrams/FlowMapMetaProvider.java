/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.springframework.core.io.Resource;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provider of providers for segments. A provider of graph attribute providers needed for rendering a segment.
 */
public class FlowMapMetaProvider extends AbstractFlowMetaProvider<Node, Flow> {

    private Map<String, Serializable> graphProperties;

    public FlowMapMetaProvider( ModelObject modelObject, String outputFormat, Resource imageDirectory, Analyst analyst,
                                CommunityService communityService ) {
        this( modelObject, outputFormat, imageDirectory, analyst, false, false, false, false, communityService );
    }

    public FlowMapMetaProvider( ModelObject modelObject, String outputFormat, Resource imageDirectory, Analyst analyst,
                                boolean showingGoals, boolean showingConnectors, boolean hidingNoop, boolean simplified,
                                CommunityService communityService ) {
        super( modelObject,
                outputFormat,
                imageDirectory,
                analyst,
                showingGoals,
                showingConnectors,
                hidingNoop,
                simplified,
                communityService );
    }

    @Override
    public URLProvider<Node, Flow> getURLProvider() {
        return new URLProvider<Node, Flow>() {
            /**
             * The URL for the graph that contains the vertex
             *
             * @param node -- a vertex
             * @return a URL string
             */
            @Override
            public String getGraphURL( Node node ) {
                Object[] args = {node.getSegment().getId()};
                return MessageFormat.format( GRAPH_URL_FORMAT, args );
            }

            /**
             * The vertex's URL. Returns null if none.
             *
             * @param node -- a vertex
             * @return a URL string
             */
            @Override
            public String getVertexURL( Node node ) {
                if ( node.isPart() ) {
                    if ( isHidingNoop() && getAnalyst().isEffectivelyConceptualInPlan( getCommunityService(), (Part) node ) )
                        return null;
                    else {
                        Object[] args = {node.getSegment().getId(), node.getId()};
                        return MessageFormat.format( VERTEX_URL_FORMAT, args );
                    }
                } else {
                    return null;
                }
            }

            /**
             * The edges's URL. Returns null if none.
             *
             * @param edge -- an edge
             * @return a URL string
             */
            @Override
            public String getEdgeURL( Flow edge ) {
                // Plan id = 0 for now sice there is only one plan
                if ( isHidingNoop() && getAnalyst().isEffectivelyConceptualInPlan( getCommunityService(), edge ) )
                    return null;
                else {
                    Object[] args = {0, edge.getId()};
                    return MessageFormat.format( EDGE_URL_FORMAT, args );
                }
            }
        };
    }

    @Override
    protected String getNodeLabel( Node node ) {
        return super.getNodeLabel( node ) + ( node.isPart() && ( (Part) node ).isProhibited() ? " -PROHIBITED-" : "" );
    }

    @Override
    public EdgeNameProvider<Flow> getEdgeLabelProvider() {
        return new EdgeNameProvider<Flow>() {
            @Override
            public String getEdgeName( Flow flow ) {
                if ( isSimplified() ) {
                    return "";
                } else {
                    String flowName = flow.getName();
                    if ( flow.isAskedFor() && !flowName.endsWith( "?" ) )
                        flowName += "?";
                    if ( flow.isProhibited() )
                        flowName += " -PROHIBITED-";
                    if ( !flow.getRestrictions().isEmpty() )
                        flowName += " (if " + flow.getRestrictionString( !flow.isNeed() ) + ")";
                    String label = AbstractMetaProvider.separate( flowName, LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );
                    return sanitize( label );
                }
            }
        };
    }

    protected String getEdgeLabel( Flow flow, boolean highlighted ) {
        String flowName = flow.getName();
 /*       if ( flow.isAskedFor() && !flowName.endsWith( "?" ) )
            flowName += "?";
*/
        if ( isSimplified() ) {
            return "";
        } else {
            if ( flow.isProhibited() )
                flowName += " -PROHIBITED-";
            if ( !flow.getRestrictions().isEmpty() ) {
                if ( highlighted )
                    flowName += " (if " + flow.getRestrictionString( !flow.isNeed() ) + ")";
                else
                    flowName += "*";
            }
            String label = AbstractMetaProvider.separate( flowName, LINE_WRAP_SIZE ).replaceAll( "\\|", "\\\\n" );
            return sanitize( label );
        }
    }

    @Override
    public DOTAttributeProvider<Node, Flow> getDOTAttributeProvider() {
        return new SegmentDOTAttributeProvider();
    }

    public void setGraphProperties( Map<String, Serializable> graphProperties ) {
        this.graphProperties = graphProperties;
    }

    /**
     * A DOTAttributeProvider for segments.
     */
    private class SegmentDOTAttributeProvider implements DOTAttributeProvider<Node, Flow> {

        public SegmentDOTAttributeProvider() {
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
            list.add( new DOTAttribute( "overlap", "false" ) );
            // list.add( new DOTAttribute( "splines", "true" ) );
            // list.add( new DOTAttribute( "sep", ".1" ) );
            return list;
        }

        /**
         * Gets semi-colon-separated style declarations for subgraphs.
         *
         * @return the style declarations
         */
        @Override
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            list.add( new DOTAttribute( "fontsize", SUBGRAPH_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", SUBGRAPH_FONT ) );
            list.add( new DOTAttribute( "color", SUBGRAPH_COLOR ) );
            list.add( new DOTAttribute( "style", "filled" ) );
            return list;
        }

        @Override
        public List<DOTAttribute> getVertexAttributes( CommunityService communityService, Node vertex, boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( communityService, getAnalyst().getImagingService(), vertex ) ) );
            list.add( new DOTAttribute( "labelloc", "b" ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "shape", "ellipse" ) );
                list.add( new DOTAttribute( "style", "solid" ) );
                list.add( new DOTAttribute( "color", getHighlightColor( vertex ) ) );
                list.add( new DOTAttribute( "penwidth", HIGHLIGHT_PENWIDTH ) );
                list.add( new DOTAttribute( "fontname", HIGHLIGHT_NODE_FONT ) );
                list.add( new DOTAttribute( "margin", VERTEX_HIGHLIGHTED_LABEL_MARGIN ) );
            } else {
                list.add( new DOTAttribute( "shape", "none" ) );
                list.add( new DOTAttribute( "fontname", NODE_FONT ) );
                list.add( new DOTAttribute( "margin", VERTEX_LABEL_MARGIN ) );
            }
            list.add( new DOTAttribute( "fontcolor", getFontColor( vertex ) ) );
            list.add( new DOTAttribute( "fontsize", NODE_FONT_SIZE ) );
            if ( !isInvisible( vertex ) ) {
                if ( !isSimplified() && indicateError( vertex, communityService ) ) {
                    list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                    list.add( new DOTAttribute( "tooltip",
                            sanitize( getCommunityService().getDoctor().getIssuesOverview( communityService,
                                    vertex,
                                    Doctor.INCLUDE_PROPERTY_SPECIFIC ) ) ) );
                } else {
                    String tooltip;
                    if ( vertex.isPart() ) {
                        tooltip = isSimplified()
                                ? getQueryService().getTooltip( (Part) vertex )
                                : vertex.getTitle();
                        if ( !isSimplified() ) {
                            List<Actor> partActors = communityService.getModelService().findAllActualActors( ( (Part) vertex ).resourceSpec() );
                            if ( partActors.size() > 1 ) {
                                tooltip = "Executed by " + listActors( partActors );
                            }
                        }
                    } else {
                        tooltip = vertex.getTitle();
                    }
                    list.add( new DOTAttribute( "tooltip", sanitize( tooltip ) ) );
                }
            }
            if ( vertex.isConnector() ) {
                Connector connector = (Connector) vertex;
                Iterator<ExternalFlow> externalFlows = connector.externalFlows();
                list.add( new DOTAttribute( "fontcolor", "white" ) );
                if ( !isHidingNoop() || !getAnalyst().isEffectivelyConceptualInPlan( communityService,
                        connector.getInnerFlow() ) ) {
                    if ( externalFlows.hasNext() )
                        list.add( new DOTAttribute( "tooltip",
                                "Connected to: " + summarizeExternalFlows( externalFlows ) ) );
                    else if ( connector.isSource() && !connector.getInnerFlow().isSatisfied() )
                        list.add( new DOTAttribute( "tooltip", "Need completely unsatisfied" ) );
                    else if ( connector.isTarget() && !connector.getInnerFlow().isSatisfying() )
                        list.add( new DOTAttribute( "tooltip", "Capability unused" ) );
                    else
                        list.add( new DOTAttribute( "tooltip", connector.isTarget() ? "Capability" : "Need" ) );
                }
            }
            return list;
        }

        private boolean indicateError( Node vertex, CommunityService communityService ) {
            return communityService.getDoctor().hasUnwaivedIssues( communityService, vertex, Doctor.INCLUDE_PROPERTY_SPECIFIC );
        }

        private boolean indicateError( Flow edge, CommunityService communityService ) {
            return communityService.getDoctor().hasUnwaivedIssues( communityService, edge, Doctor.INCLUDE_PROPERTY_SPECIFIC );
        }

        private boolean isInvisible( Node vertex ) {
            return vertex.isPart() && isHidingNoop() && getAnalyst().isEffectivelyConceptualInPlan( getCommunityService(),
                    (Part) vertex );
        }

        private String getHighlightColor( Node vertex ) {
            return isInvisible( vertex ) ? INVISIBLE_COLOR : HIGHLIGHT_COLOR;
        }

        private String getFontColor( Node vertex ) {
            return isInvisible( vertex )
                    ? vertex.getSegment().equals( getSegment() )
                    ? INVISIBLE_COLOR
                    : SUBGRAPH_COLOR
                    : isOverridden( vertex )
                    ? OVERRIDDEN_COLOR
                    : FONTCOLOR;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( CommunityService communityService, Flow edge, boolean highlighted ) {
            boolean conceptual = getAnalyst().isEffectivelyConceptualInPlan( communityService, edge );
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "label", getEdgeLabel( edge, highlighted ) ) );
            list.add( new DOTAttribute( "color",
                    colorIfVisible( edge, isOverridden( edge ) ? OVERRIDDEN_COLOR : EDGE_COLOR ) ) );
            list.add( new DOTAttribute( "arrowsize", "0.75" ) );
            // list.add( new DOTAttribute( "fontcolor", FONTCOLOR ) );
            if ( highlighted ) {
                list.add( new DOTAttribute( "fontname", EDGE_FONT_BOLD ) );
                list.add( new DOTAttribute( "penwidth", HIGHLIGHT_PENWIDTH ) );
            } else {
                list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
                list.add( new DOTAttribute( "penwidth", EDGE_PENWIDTH ) );
            }
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "arrowsize", ARROW_SIZE ) );
            list.add( new DOTAttribute( "fontcolor",
                    colorIfVisible( edge,
                            isOverridden( edge ) ? OVERRIDDEN_COLOR : _EDGE_FONT_COLOR ) ) );
            list.add( new DOTAttribute( "len", EDGE_LEN ) );
            list.add( new DOTAttribute( "weight", EDGE_WEIGHT ) );
            addTailArrowHead( edge, list );
            list.add( new DOTAttribute( "style",
                    isSimplified()
                            ? "normal"
                            : conceptual
                            ? edge.isCritical()
                            ? "dashed"
                            : "dotted"
                            : edge.isCritical()
                            ? "bold"
                            : "normal"
            ) );
            if ( edge.isCritical() ) {
                list.add( new DOTAttribute(
                        "fontcolor",
                        colorIfVisible(
                                edge,
                                isOverridden( edge ) ? OVERRIDDEN_COLOR : EDGE_CRITICAL_COLOR ) ) );
            }
            // head and tail labels
            if ( !isSimplified() ) {
                String headLabel = null;
                String tailLabel = null;
                if ( edge.isAll() ) {
                    if ( edge.isTerminatingToTarget() )
                        headLabel = "(stop all)";
                    else if ( edge.isTriggeringToTarget() )
                        headLabel = "(start all)";
                    else {
                        headLabel = "(all)";
                    }
                } else {
                    if ( edge.isTerminatingToTarget() )
                        headLabel = "(stop)";
                    else if ( edge.isTriggeringToTarget() )
                        headLabel = "(start)";
                }
                if ( edge.isTerminatingToSource() ) {
                    tailLabel = "(stop)";
                } else if ( edge.isTriggeringToSource() ) {
                    tailLabel = "(start)";
                }
                if ( headLabel != null )
                    list.add( new DOTAttribute( "headlabel", headLabel ) );
                if ( tailLabel != null )
                    list.add( new DOTAttribute( "taillabel", tailLabel ) );
                if ( headLabel != null || tailLabel != null ) {
                    list.add( new DOTAttribute( "labeldistance", LABEL_DISTANCE ) );
                    list.add( new DOTAttribute( "labelangle", LABEL_ANGLE ) );
                }
            }
            // Issue coloring
            if ( !isInvisible( edge ) ) {
                boolean hasErrors = indicateError( edge, communityService );
                if ( !isSimplified() && hasErrors ) {
                    list.add( new DOTAttribute( "fontcolor", COLOR_ERROR ) );
                    list.add( new DOTAttribute( "color", COLOR_ERROR ) );
                }
                String labelTooltip;
                if ( !edge.getRestrictions().isEmpty() ) {
                    labelTooltip = "Only if " + edge.getRestrictionString( !edge.isNeed() );
                } else {
                    labelTooltip = sanitize( edge.getName() );
                }
                list.add( new DOTAttribute( "labeltooltip", labelTooltip ) );
                String edgeTooltip;
                if ( !isSimplified() && hasErrors ) {
                    edgeTooltip =
                            sanitize( communityService.getDoctor().getIssuesOverview( communityService,
                                    edge,
                                    Doctor.INCLUDE_PROPERTY_SPECIFIC ) );
                } else {
                    edgeTooltip = edge.isAskedFor()
                            ? "Answer with "
                            : "Notify of ";
                    edgeTooltip += edge.getName();
                    if ( edge.isIfTaskFails() ) {
                        edgeTooltip += " - if task fails";
                    }
                    if ( edge.isRestricted() ) {
                        edgeTooltip += " - only if " + edge.getRestrictionString( !edge.isNeed() );
                    }
                    if ( edge.isReceiptConfirmationRequested() ) {
                        edgeTooltip += " - request receipt confirmation ";
                    }
                    edgeTooltip = sanitize( edgeTooltip );
                }
                list.add( new DOTAttribute( "edgetooltip", edgeTooltip ) );
            }
            return list;
        }

        private String colorIfVisible( Flow edge, String color ) {
            return isInvisible( edge ) ? INVISIBLE_COLOR : color;
        }

        private boolean isInvisible( Flow edge ) {
            return isHidingNoop() && getAnalyst().isEffectivelyConceptualInPlan( getCommunityService(), edge );
        }
    }

    public void addTailArrowHead( Flow edge, List<DOTAttribute> list ) {
        if ( edge.isIfTaskFails() ) {
            list.add( new DOTAttribute(
                    "arrowtail",
                    edge.isReceiptConfirmationRequested()
                            ? "boxlvee"
                            : "box" ) );
            list.add( new DOTAttribute( "dir", "both" ) );
        }
        if ( edge.isAskedFor() ) {
            list.add( new DOTAttribute(
                    "arrowtail",
                    edge.isReceiptConfirmationRequested()
                            ? "onormallvee"
                            : "onormal" ) );
            list.add( new DOTAttribute( "dir", "both" ) );
        }
        if ( !edge.isIfTaskFails() && !edge.isAskedFor() ) {
            if ( edge.isReceiptConfirmationRequested() ) {
                list.add( new DOTAttribute( "arrowtail", "lvee" ) );
                list.add( new DOTAttribute( "dir", "both" ) );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isOverridden( Flow flow ) {
        if ( graphProperties != null && graphProperties.get( "overriddenFlows" ) != null ) {
            List<Flow> impliedFlows = (List<Flow>) graphProperties.get( "overriddenFlows" );
            return impliedFlows.contains( flow );
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isOverridden( Node node ) {
        if ( node.isPart() && graphProperties != null && graphProperties.get( "overriddenParts" ) != null ) {
            List<Part> impliedParts = (List<Part>) graphProperties.get( "overriddenParts" );
            return impliedParts.contains( (Part) node );
        } else {
            return false;
        }
    }

    private String summarizeExternalFlows( Iterator<ExternalFlow> externalFlows ) {
        StringBuilder sb = new StringBuilder();
        while ( externalFlows.hasNext() ) {
            ExternalFlow flow = externalFlows.next();
            sb.append( flow.getTitle() );
            if ( externalFlows.hasNext() )
                sb.append( " -- " );
        }
        return sanitize( sb.toString() );
    }

    private Segment getSegment() {
        return (Segment) getContext();
    }
}
