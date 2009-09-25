package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Scenario;
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
public class PlanMapDOTExporter extends AbstractDOTExporter<Scenario, ScenarioRelationship> {

    public PlanMapDOTExporter( MetaProvider<Scenario, ScenarioRelationship> metaProvider ) {
        super( metaProvider );
    }

    /**
     * {@inheritDoc}
     */
    protected void exportVertices( PrintWriter out, Graph<Scenario, ScenarioRelationship> g ) {
        PlanMapMetaProvider metaProvider = (PlanMapMetaProvider) getMetaProvider();
        if ( metaProvider.isGroupByPhase() || metaProvider.isGroupByEvent() ) {
            Map<ModelObject, Set<Scenario>> groupedScenarios = new HashMap<ModelObject, Set<Scenario>>();
            for ( Scenario scenario : g.vertexSet() ) {
                ModelObject group = getGroup( scenario );
                Set<Scenario> scenariosInGroup = groupedScenarios.get( group );
                if ( scenariosInGroup == null ) {
                    scenariosInGroup = new HashSet<Scenario>();
                    groupedScenarios.put( group, scenariosInGroup );
                }
                scenariosInGroup.add( scenario );
            }
            List<ModelObject> groups = new ArrayList<ModelObject>( groupedScenarios.keySet() );
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
                            getGraphURL( groupedScenarios.get( group ).iterator().next() );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( asGraphAttributes( attributes ) );
                out.println();
                printoutVertices( out, groupedScenarios.get( group ) );
                out.println( "}" );
            }
        } else {
            super.exportVertices( out, g );
        }
    }

    private ModelObject getGroup( Scenario scenario ) {
        PlanMapMetaProvider metaProvider = (PlanMapMetaProvider) getMetaProvider();
        if ( metaProvider.isGroupByPhase() ) {
            return scenario.getPhase();
        } else if ( metaProvider.isGroupByEvent() ) {
            return scenario.getEvent();
        } else {
            return null;
        }
    }
}

