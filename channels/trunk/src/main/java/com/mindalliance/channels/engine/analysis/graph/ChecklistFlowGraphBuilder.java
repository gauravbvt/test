package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.core.model.checklist.ChecklistElement;
import com.mindalliance.channels.core.model.checklist.Condition;
import com.mindalliance.channels.core.model.checklist.Outcome;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 9:16 PM
 */
public class ChecklistFlowGraphBuilder implements GraphBuilder<ChecklistElement, ChecklistElementRelationship> {

    private final Part part;
    private final PlanService planService;
    private final Analyst analyst;

    public ChecklistFlowGraphBuilder( Part part, PlanService planService, Analyst analyst ) {
        this.part = part;
        this.planService = planService;
        this.analyst = analyst;
    }

    @Override
    public DirectedGraph<ChecklistElement, ChecklistElementRelationship> buildDirectedGraph() {
        DirectedGraph<ChecklistElement, ChecklistElementRelationship> digraph =
                new DirectedMultigraph<ChecklistElement, ChecklistElementRelationship>(
                        new EdgeFactory<ChecklistElement, ChecklistElementRelationship>() {
                            @Override
                            public ChecklistElementRelationship createEdge( ChecklistElement checklistElement,
                                                                            ChecklistElement otherChecklistElement ) {
                                return new ChecklistElementRelationship(
                                        checklistElement,
                                        otherChecklistElement,
                                        part.getChecklist() );
                            }
                        } );
        populateGraph( digraph );
        return digraph;
    }

    private void populateGraph( DirectedGraph<ChecklistElement, ChecklistElementRelationship> digraph ) {
        Checklist checklist = part.getChecklist();
        // add step vertices
        List<Step> steps = checklist.listEffectiveSteps();
        for ( Step step : steps ) {
            ChecklistElementHolder stepHolder = new ChecklistElementHolder( step, steps.indexOf( step ) );
            digraph.addVertex( stepHolder );
        }


        for ( Step toStep : steps ) {
            List<Step> priors = checklist.listStepsJustBefore( toStep );
            List<Condition> allConditions = checklist.listEffectiveConditions();
            List<Condition> stepConditions = checklist.listConditionsFor( toStep );
            List<Outcome> allOutcomes = checklist.listEffectiveOutcomes();
            List<Outcome> outcomes = checklist.listOutcomesFor( toStep );
            // add step-step flow edges, condition-flow edges, flow-condition edges, and in-flow condition vertices
            ChecklistElementHolder toStepHolder = new ChecklistElementHolder( toStep, steps.indexOf( toStep ) );
            for ( Step fromStep : priors ) {
                ChecklistElementHolder fromStepHolder = new ChecklistElementHolder( fromStep, steps.indexOf( fromStep ) );
                if ( stepConditions.isEmpty() ) {
                    ChecklistElementRelationship ceRel = new ChecklistElementRelationship(
                            fromStepHolder,
                            toStepHolder,
                            checklist );
                    if ( !digraph.containsEdge( fromStepHolder, toStepHolder ) )
                        digraph.addEdge( fromStepHolder, toStepHolder, ceRel );
                } else {
                    // fromStep to first condition
                    long idFirst = 1000 + toStepHolder.getId();
                    ChecklistElementHolder firstCond = new ChecklistElementHolder( stepConditions.get( 0 ), idFirst );
                    setContext( firstCond, toStepHolder, checklist );
                    digraph.addVertex( firstCond );
                    if ( !digraph.containsEdge( fromStepHolder, firstCond ) )
                        digraph.addEdge(
                                fromStepHolder,
                                firstCond,
                                new ChecklistElementRelationship( fromStepHolder, firstCond, checklist ) );
                    // chain of conditions
                    chainConditions( stepConditions, toStepHolder, checklist, digraph );
                }
            }
            if ( priors.isEmpty() ) {
                // add no-pre-req condition-to-flow edges, and no-prereq step conditions
                chainConditions( stepConditions, toStepHolder, checklist, digraph );
                for ( Condition condition : stepConditions ) {
                    ChecklistElementHolder conditionHolder = new ChecklistElementHolder(
                            condition,
                            allConditions.indexOf( condition ) );
                    setContext( conditionHolder, toStepHolder, checklist );
                    digraph.addVertex( conditionHolder );
                    if ( !digraph.containsEdge( conditionHolder, toStepHolder ) )
                        digraph.addEdge(
                                conditionHolder,
                                toStepHolder,
                                new ChecklistElementRelationship( conditionHolder, toStepHolder, checklist )
                        );
                }
            }
            // outcomes
            for ( Outcome outcome : outcomes ) {
                ChecklistElementHolder outcomeHolder = new ChecklistElementHolder(
                        outcome,
                        allOutcomes.indexOf( outcome )
                );
                digraph.addVertex( outcomeHolder );
                if ( !digraph.containsEdge( toStepHolder, outcomeHolder ) )
                    digraph.addEdge(
                            toStepHolder,
                            outcomeHolder,
                            new ChecklistElementRelationship( toStepHolder, outcomeHolder, checklist )
                    );
            }
        }
    }

    private void chainConditions( List<Condition> stepConditions,
                                  ChecklistElementHolder toStepHolder,
                                  Checklist checklist,
                                  DirectedGraph<ChecklistElement, ChecklistElementRelationship> digraph ) {
        for ( int n = 0; n < stepConditions.size() - 1; n++ ) {
            Condition cond1 = stepConditions.get( n );
            Condition cond2 = stepConditions.get( n + 1 );
            long id1 = ( 1000 * ( n + 1 ) ) + toStepHolder.getId();
            long id2 = ( 1000 * ( n + 2 ) ) + toStepHolder.getId();
            ChecklistElementHolder condition1 = new ChecklistElementHolder( cond1, id1 );
            setContext( condition1, toStepHolder, checklist );
            ChecklistElementHolder condition2 = new ChecklistElementHolder( cond2, id2 );
            setContext( condition2, toStepHolder, checklist );
            digraph.addVertex( condition1 );
            digraph.addVertex( condition2 );
            if ( !digraph.containsEdge( condition1, condition2 ) )
                digraph.addEdge(
                        condition1,
                        condition2,
                        new ChecklistElementRelationship( condition1, condition2, checklist ) );
        }
        if ( !stepConditions.isEmpty() ) {
            // last condition to toStep
            long idLast = ( 1000 * stepConditions.size() ) + toStepHolder.getId();
            ChecklistElementHolder lastCondition = new ChecklistElementHolder(
                    stepConditions.get( stepConditions.size() - 1 ),
                    idLast );
            digraph.addVertex( lastCondition );
            if ( !digraph.containsEdge( lastCondition, toStepHolder ) )
                digraph.addEdge(
                        lastCondition,
                        toStepHolder,
                        new ChecklistElementRelationship( lastCondition, toStepHolder, checklist ) );
        }

    }

    private void setContext( ChecklistElementHolder conditionHolder,
                             ChecklistElementHolder toStepHolder,
                             Checklist checklist ) {
        boolean isIf = checklist.listConditionsFor( toStepHolder.getStep(), true )
                .contains( conditionHolder.getCondition() );
        conditionHolder.setContext( isIf ? Condition.IF : Condition.UNLESS );
    }
}
