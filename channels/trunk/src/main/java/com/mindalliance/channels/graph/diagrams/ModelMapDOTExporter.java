package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.analysis.graph.SegmentRelationship;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.apache.commons.lang.StringUtils;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Plan DOT exporter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 1, 2009
 * Time: 8:03:37 PM
 */
public class ModelMapDOTExporter extends AbstractDOTExporter<Segment, SegmentRelationship> {

    public ModelMapDOTExporter( MetaProvider<Segment, SegmentRelationship> metaProvider ) {
        super( metaProvider );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void exportVertices( CommunityService communityService, PrintWriter out, Graph<Segment, SegmentRelationship> g ) {
        ModelMapMetaProvider metaProvider = (ModelMapMetaProvider) getMetaProvider();
        if ( metaProvider.isGroupByPhase() || metaProvider.isGroupByEvent() ) {
            Map<ModelObject, Set<Segment>> groupedSegments = new HashMap<ModelObject, Set<Segment>>();
            for ( Segment segment : g.vertexSet() ) {
                ModelObject group = getGroup( segment );
                Set<Segment> segmentsInGroup = groupedSegments.get( group );
                if ( segmentsInGroup == null ) {
                    segmentsInGroup = new HashSet<Segment>();
                    groupedSegments.put( group, segmentsInGroup );
                }
                segmentsInGroup.add( segment );
            }
            List<ModelObject> groups = new ArrayList<ModelObject>( groupedSegments.keySet() );
            Collections.sort( groups );
            for ( ModelObject group : groups ) {
                out.println( "subgraph cluster_"
                        + group.getName().replaceAll( "[^a-zA-Z0-9_]", "_" )
                        + " {" );
                String typeName = StringUtils.capitalize( group.getTypeName() );
                List<DOTAttribute> attributes = new DOTAttribute( "label",
                        typeName + ": " + group.getName() ).asList();
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll(
                            metaProvider.getDOTAttributeProvider().getSubgraphAttributes(
                                    metaProvider.getSelectedGroup() != null
                                            && metaProvider.getSelectedGroup().equals( group ) ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().
                            getGraphURL( groupedSegments.get( group ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( communityService, out, groupedSegments.get( group ) );
                out.println( "}" );
            }
        } else {
            super.exportVertices( communityService, out, g );
        }
    }

    private ModelObject getGroup( Segment segment ) {
        ModelMapMetaProvider metaProvider = (ModelMapMetaProvider) getMetaProvider();
        if ( metaProvider.isGroupByPhase() ) {
            return segment.getPhase();
        } else if ( metaProvider.isGroupByEvent() ) {
            return segment.getEvent();
        } else {
            return null;
        }
    }
}

