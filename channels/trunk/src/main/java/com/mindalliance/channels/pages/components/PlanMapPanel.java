package com.mindalliance.channels.pages.components;


import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.analysis.network.ScenarioRelationship;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.apache.wicket.Component;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Scenarios map panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 1:18:49 PM
 */
public class PlanMapPanel extends AbstractUpdatablePanel {

    //     public PlanMapPanel( String id, new IModel<Plan> model, Set<Long> expansions ) {
    //          super( id, modelm expansions );
    //          init();
    //      }

    private Long fromId;
    private Long toId;
    private static final int PAGE_SIZE = 10;

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanMapPanel.class );

    public PlanMapPanel( String id, Set<Long> expansions ) {
        super( id, null, expansions );
        init( expansions );
    }

    private void init( Set<Long> expansions ) {
        // PlanMapDiagramPanel planMapDiagramPanel = new PlanMapDiagramPanel( "plan-map", new Model<Plan>(Project.plan()));
        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
        scenarios.addAll(getDqo().list( Scenario.class ));
        PlanMapDiagramPanel planMapDiagramPanel = new PlanMapDiagramPanel(
                "plan-map",
                new Model<ArrayList<Scenario>>(scenarios));
        add( planMapDiagramPanel );
        addFlowPanel( expansions );
    }

    private void addFlowPanel( Set<Long> expansions ) {
        ScenarioRelationship scenarioRelationship = findSelectedScenaroRelationship();
        Component externalFlowsPanel;
        if ( scenarioRelationship == null ) {
            externalFlowsPanel = new Label( "flows", "" );
        } else {
            externalFlowsPanel = new ExternalFlowsPanel(
                    "flows",
                    new PropertyModel<Scenario>( this, "fromScenario" ),
                    new PropertyModel<Scenario>( this, "toScenario" ),
                    PAGE_SIZE,
                    expansions
            );
        }
        externalFlowsPanel.setOutputMarkupId( true );
        addOrReplace( externalFlowsPanel );
    }

    private ScenarioRelationship findSelectedScenaroRelationship() {
        ScenarioRelationship scenarioRelationship = null;
        if ( fromId != null && toId != null ) {
            try {
                Scenario fromScenario = getDqo().find( Scenario.class, fromId );
                Scenario toScenario = getDqo().find( Scenario.class, toId );
                scenarioRelationship = getDqo().findScenarioRelationship( fromScenario, toScenario );
            } catch ( NotFoundException e ) {
                LOG.warn( "Failed to find scenario link.", e );
            }
        }
        return scenarioRelationship;
    }
}
