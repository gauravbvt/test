package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.ChecklistElement;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.engine.analysis.graph.ChecklistElementHolder;
import com.mindalliance.channels.engine.analysis.graph.ChecklistElementRelationship;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jgrapht.Graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 9:18 PM
 */
public class ChecklistFlowDOTExporter extends AbstractDOTExporter<ChecklistElement, ChecklistElementRelationship> {

    /**
     * Start vertex.
     */
    private static final String START = "__start__";

    /**
     * Stop vertex.
     */
    private static final String STOP = "__stop__";
    private final Checklist checklist;

    private Set<ChecklistElement> starters = new HashSet<ChecklistElement>();
    private Set<ChecklistElement> enders = new HashSet<ChecklistElement>();


    public ChecklistFlowDOTExporter( MetaProvider<ChecklistElement,
            ChecklistElementRelationship> metaProvider,
                                     Checklist checklist ) {
        super( metaProvider );
        this.checklist = checklist;
    }

    @Override
    protected void beforeExport( CommunityService communityService, Graph<ChecklistElement, ChecklistElementRelationship> g ) {
        for ( ChecklistElement cle : g.vertexSet() ) {
            if ( cle.isStep() ) {
                final Step step = cle.getStep();
                if ( checklist.listPrerequisiteStepsFor( step ).isEmpty() ) {
                    List<Condition> conditions = checklist.listConditionsFor( step );
                    if ( conditions.isEmpty() ) {
                        starters.add( cle );
                    } else {
                        starters.add( new ChecklistElementHolder( conditions.get( 0 ), 1000 + step.getId() ) ); // todo: remove this dependency on implementation of ChecklistFlowGraphBuilder::populateGraph
                    }
                }
                if ( !CollectionUtils.exists(   // step is not a prerequisite for another step
                        checklist.listEffectiveSteps(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                Step otherStep = (Step) object;
                                return !step.equals( otherStep )
                                        && checklist.listPrerequisiteStepsFor( otherStep ).contains( step );
                            }
                        }
                ) ) {
                    enders.add( cle );
                }

            }
        }
    }

    @Override
    protected void exportVertices(
            CommunityService communityService,
            PrintWriter out,
            Graph<ChecklistElement, ChecklistElementRelationship> g ) {
        exportStart( out );
        super.exportVertices( communityService, out, g );
        exportEnd( out );
    }

    private void exportStart( PrintWriter out ) {
        ChecklistFlowMetaProvider metaProvider = (ChecklistFlowMetaProvider) getMetaProvider();
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", "Start" ) );
        attributes.add( new DOTAttribute( "label", "" ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + "step_start.png" ) );
        out.print( getIndent() );
        out.print( START );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }


    private void exportEnd( PrintWriter out ) {
        ChecklistFlowMetaProvider metaProvider = (ChecklistFlowMetaProvider) getMetaProvider();
        List<DOTAttribute> attributes = DOTAttribute.emptyList();
        attributes.add( new DOTAttribute( "shape", "none" ) );
        attributes.add( new DOTAttribute( "tooltip", "Stop" ) );
        attributes.add( new DOTAttribute( "label", "" ) );
        String dirName;
        try {
            dirName = metaProvider.getImageDirectory().getFile().getAbsolutePath();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get image directory location", e );
        }
        attributes.add( new DOTAttribute( "image", dirName + "/" + "step_stop.png" ) );
        out.print( getIndent() );
        out.print( STOP );
        out.print( "[" );
        out.print( asElementAttributes( attributes ) );
        out.println( "];" );
    }

    @Override
    protected void exportEdges( CommunityService communityService,
                                PrintWriter out,
                                Graph<ChecklistElement, ChecklistElementRelationship> g ) throws InterruptedException {
        exportStarterEdges( communityService, out, g );
        super.exportEdges( communityService, out, g );
        exportEnderEdges( communityService, out, g );
    }

    private void exportStarterEdges( CommunityService communityService,
                                     PrintWriter out,
                                     Graph<ChecklistElement,
                                             ChecklistElementRelationship> g ) {
        List<DOTAttribute> attributes = getStartStopAttributes();
        for ( ChecklistElement checklistElement : starters ) {
            String autoStarterId = getVertexID( checklistElement );
            out.print( getIndent() + START + getArrow( g ) + autoStarterId );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
    }

    private void exportEnderEdges( CommunityService communityService,
                                   PrintWriter out,
                                   Graph<ChecklistElement, ChecklistElementRelationship> g ) {
        List<DOTAttribute> attributes = getStartStopAttributes();
        for ( ChecklistElement checklistElement : enders ) {
            String enderStarterId = getVertexID( checklistElement );
            out.print( getIndent() + enderStarterId + getArrow( g ) + STOP );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );
        }
        if ( g.vertexSet().isEmpty() ) {
            out.print( getIndent() + START + getArrow( g ) + STOP );
            out.print( "[" );
            if ( !attributes.isEmpty() ) {
                out.print( asElementAttributes( attributes ) );
            }
            out.println( "];" );

        }
    }

    private List<DOTAttribute> getStartStopAttributes() {
        List<DOTAttribute> list = DOTAttribute.emptyList();
        list.add( new DOTAttribute( "color", "gray" ) );
        list.add( new DOTAttribute( "arrowhead", "none" ) );
        list.add( new DOTAttribute( "len", "1.5" ) );
        list.add( new DOTAttribute( "weight", "2.0" ) );
        return list;
    }


    // Assumes vertex name is DOT-compliant
    protected String getVertexID( ChecklistElement v ) {
        return getMetaProvider().getVertexIDProvider().getVertexName( v );
    }

}
