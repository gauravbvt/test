package com.mindalliance.channels.pages.components;


import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.basic.Label;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;


/**
 * Plan map panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 1:18:49 PM
 */
public class PlanMapPanel extends AbstractUpdatablePanel {

    /**
     * Default page size for external flows panel.
     */
    private static final int PAGE_SIZE = 10;
    /**
     * Selected scenario in plan.
     */
    private Scenario selectedScenario;
    /**
     * Selected scenario relationship in plan.
     */
    private ScenarioRelationship selectedScRel;
    /**
     * Expansions.
     */
    private Set<Long> expansions;
    /**
     * Plan diagram panel
     */
    private PlanMapDiagramPanel planMapDiagramPanel;
    /**
     * Title for external flows panel.
     */
    private Label flowsTitleLabel;
    /**
     * External flows panel.
     */
    private ExternalFlowsPanel externalFlowsPanel;


    public PlanMapPanel( String id, Set<Long> expansions ) {
        super( id, null, expansions );
        this.expansions = expansions;
        init();
    }

    private void init() {
        addPlanMapDiagramPanel();
        addFlowsTitleLabel();
        addExternalFlowsPanel();
    }

    private void addPlanMapDiagramPanel() {
        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
        scenarios.addAll( getDqo().list( Scenario.class ) );
        planMapDiagramPanel = new PlanMapDiagramPanel(
                "plan-map",
                new Model<ArrayList<Scenario>>( scenarios ),
                selectedScenario,
                selectedScRel );
        planMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( planMapDiagramPanel );
    }

    private void addFlowsTitleLabel() {
        flowsTitleLabel = new Label( "flows-title", new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        add( flowsTitleLabel );
    }

    private void addExternalFlowsPanel() {
        externalFlowsPanel = new ExternalFlowsPanel(
                "flows",
                new Model<Project>( Project.getProject() ),
                new PropertyModel<ArrayList<ExternalFlow>>( this, "externalFlows" ),
                PAGE_SIZE,
                expansions
        );
        externalFlowsPanel.setOutputMarkupId( true );
        addOrReplace( externalFlowsPanel );
    }

    /**
     * Get flows title.
     *
     * @return a string
     */
    public String getFlowsTitle() {
        if ( selectedScenario != null ) {
            return "Flows in \""
                    + selectedScenario.getName()
                    + "\" connecting to other scenarios";
        } else if ( selectedScRel != null ) {
            Scenario fromScenario = selectedScRel.getFromScenario( getDqo() );
            Scenario toScenario = selectedScRel.getToScenario( getDqo() );
            if ( fromScenario == null || toScenario == null ) {
                return "*** You need to refresh ***";
            } else {
                return "Flows in \""
                        + fromScenario.getName()
                        + "\" connecting to \""
                        + toScenario.getName()
                        + "\"";
            }
        } else {
            return "All inter-scenario flows";
        }
    }

    /**
     * Get external flows.
     *
     * @return a list of external flows
     */
    public List<ExternalFlow> getExternalFlows() {
        if ( selectedScRel != null ) {
            return selectedScRel.getExternalFlows();
        } else if ( selectedScenario != null ) {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Scenario> allScenarios = getDqo().list( Scenario.class );
            for ( Scenario other : allScenarios ) {
                if ( selectedScenario != other ) {
                    ScenarioRelationship scRel = getDqo().findScenarioRelationship( selectedScenario, other );
                    if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                }
            }
            return externalFlows;
        } else {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Scenario> allScenarios = getDqo().list( Scenario.class );
            for ( Scenario scenario : allScenarios ) {
                for ( Scenario other : allScenarios ) {
                    if ( scenario != other ) {
                        ScenarioRelationship scRel = getDqo().findScenarioRelationship( scenario, other );
                        if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                    }
                }
            }
            return externalFlows;
        }
    }

    /**
     * Get to-scenario from selected scenario relationship.
     *
     * @return a scenario
     */
    public Scenario getToScenario() {
        if ( selectedScRel != null ) {
            return selectedScRel.getToScenario( getDqo() );
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        Identifiable changed = change.getSubject();
        if ( changed instanceof Scenario ) {
            if ( change.isSelected() ) {
                selectedScenario = (Scenario) changed;
                selectedScRel = null;
            }
        } else if ( changed instanceof ScenarioRelationship ) {
            if ( change.isSelected() ) {
                selectedScenario = null;
                selectedScRel = (ScenarioRelationship) changed;
            }
        }
        // Don't percolate change.
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        Identifiable changed = change.getSubject();
        if ( changed instanceof Scenario ) {
            if ( change.isSelected() ) {
                addPlanMapDiagramPanel();
                addExternalFlowsPanel();
                target.addComponent( planMapDiagramPanel );
                target.addComponent( flowsTitleLabel );
                target.addComponent( externalFlowsPanel );
            }
        } else if ( changed instanceof ScenarioRelationship ) {
            if ( change.isSelected() ) {
                addPlanMapDiagramPanel();
                addExternalFlowsPanel();
                target.addComponent( planMapDiagramPanel );
                target.addComponent( flowsTitleLabel );
                target.addComponent( externalFlowsPanel );
            }
        }
        // Don't percolate update.
    }
}
