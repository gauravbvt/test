package com.mindalliance.channels.pages.components;


import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.RequestCycle;

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
     * Plan map diagram panel.
     */
    private PlanMapDiagramPanel planMapDiagramPanel;
    /**
     * Width, height dimension contraints on the plan map diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];

    public PlanMapPanel( String id, Set<Long> expansions ) {
        super( id, null, expansions );
        init();
    }

    private void init() {
        AjaxFallbackLink<?> closeLink = new AjaxFallbackLink( "close" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, Project.getProject() );
                update( target, change );
            }
        };
        add( closeLink );
        addPlanSizing();
        addPlanMapDiagramPanel();
        addFlowsTitleLabel();
        addExternalFlowsPanel();
        addCausesTitleLabel();
        addCausesPanel();
    }

   private void addPlanSizing() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = "#plan-map .picture";
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getParameter( "width" );
                String sheight = requestCycle.getRequest().getParameter( "height" );
                diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / 96.0;
                diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / 96.0;
                addPlanMapDiagramPanel();
                target.addComponent( planMapDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer fullSize = new WebMarkupContainer( "full" );
        fullSize.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                diagramSize = new double[2];
                addPlanMapDiagramPanel();
                target.addComponent( planMapDiagramPanel );
            }
        } );
        add( fullSize );
    }


    private void addPlanMapDiagramPanel() {
        if ( diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ) {
            planMapDiagramPanel = new PlanMapDiagramPanel(
                "plan-map",
                new PropertyModel<ArrayList<Scenario>>( this, "scenarios" ),
                selectedScenario,
                selectedScRel,
                null,
                "#plan-map .picture");            
        } else {
            planMapDiagramPanel = new PlanMapDiagramPanel(
                "plan-map",
                new PropertyModel<ArrayList<Scenario>>( this, "scenarios" ),
                selectedScenario,
                selectedScRel,
                    diagramSize,
                "#plan-map .picture");
        }
        planMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( planMapDiagramPanel );
    }

    private void addFlowsTitleLabel() {
        Label flowsTitleLabel = new Label( "flows-title",
                                           new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        add( flowsTitleLabel );
    }

    private void addCausesTitleLabel() {
        Label causesTitleLabel = new Label( "causes-title",
                                            new PropertyModel<String>( this, "causesTitle" ) );
        causesTitleLabel.setOutputMarkupId( true );
        add( causesTitleLabel );
    }

    private void addExternalFlowsPanel() {
        ExternalFlowsPanel externalFlowsPanel = new ExternalFlowsPanel(
                "flows",
                new Model<Project>( Project.getProject() ),
                new PropertyModel<ArrayList<ExternalFlow>>( this, "externalFlows" ),
                PAGE_SIZE,
                getExpansions()
        );
        externalFlowsPanel.setOutputMarkupId( true );
        addOrReplace( externalFlowsPanel );
    }

    private void addCausesPanel() {
        ScenarioCausesPanel scenarioCausesPanel = new ScenarioCausesPanel(
                "causes",
                new Model<Project>( Project.getProject() ),
                new PropertyModel<ArrayList<ScenarioRelationship>>( this, "scenarioRelationships" ),
                PAGE_SIZE,
                getExpansions()
        );
        scenarioCausesPanel.setOutputMarkupId( true );
        addOrReplace( scenarioCausesPanel );
    }

    /**
     * Get scenarios to map.
     *
     * @return an array list of scenarios
     */
    public List<Scenario> getScenarios() {
        return getDqo().list( Scenario.class );
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
     * Get flows title.
     *
     * @return a string
     */
    public String getCausesTitle() {
        if ( selectedScenario != null ) {
            return "What \""
                    + selectedScenario.getName()
                    + " causes";
        } else if ( selectedScRel != null ) {
            Scenario fromScenario = selectedScRel.getFromScenario( getDqo() );
            Scenario toScenario = selectedScRel.getToScenario( getDqo() );
            if ( fromScenario == null || toScenario == null ) {
                return "*** You need to refresh ***";
            } else {
                return "How \""
                        + fromScenario.getName()
                        + "\" causes \""
                        + toScenario.getName()
                        + "\"";
            }
        } else {
            return "All causations";
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
            List<Scenario> allScenarios = getScenarios();
            for ( Scenario other : allScenarios ) {
                if ( selectedScenario != other ) {
                    ScenarioRelationship scRel = getDqo().findScenarioRelationship( selectedScenario, other );
                    if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                }
            }
            return externalFlows;
        } else {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Scenario> allScenarios = getScenarios();
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
     * Get scenario relationships.
     * @return a list of scenario relationships
     */
    public List<ScenarioRelationship> getScenarioRelationships() {
        List<ScenarioRelationship> scRels = new ArrayList<ScenarioRelationship>();
        if ( selectedScRel != null ) {
            scRels.add(selectedScRel);
        } else if ( selectedScenario != null ) {
            List<Scenario> allScenarios = getScenarios();
            for ( Scenario other : allScenarios ) {
                if ( selectedScenario != other ) {
                    ScenarioRelationship scRel = getDqo().findScenarioRelationship( selectedScenario, other );
                    if ( scRel != null ) scRels.add( scRel );
                }
            }
        } else {
            List<Scenario> allScenarios = getScenarios();
            for ( Scenario scenario : allScenarios ) {
                for ( Scenario other : allScenarios ) {
                    if ( scenario != other ) {
                        ScenarioRelationship scRel = getDqo().findScenarioRelationship( scenario, other );
                        if ( scRel != null ) scRels.add( scRel );
                    }
                }
            }
        }
        return scRels;
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

    public void refresh( AjaxRequestTarget target ) {
        addPlanMapDiagramPanel();
        addExternalFlowsPanel();
        addCausesPanel();
        target.addComponent( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            Identifiable changed = change.getSubject();
            if ( changed instanceof Project ) {
                selectedScenario = null;
                selectedScRel = null;
            } else if ( changed instanceof Scenario ) {
                selectedScenario = (Scenario) changed;
                selectedScRel = null;
            } else if ( changed instanceof ScenarioRelationship ) {
                selectedScenario = null;
                selectedScRel = (ScenarioRelationship) changed;
            }
            // Don't percolate chane on selection of project, scenario or scenario relationship.
            else {
                super.changed( change );
            }
        } else {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.isSelected() ) {
            refresh( target );
            // Don't percolate update on selection unless a part was selected.
            if ( change.getSubject() instanceof Part ) {
                super.updateWith( target, change );
            } else {
                if ( change.getScript() != null ) {
                    target.appendJavascript( change.getScript() );
                }
            }
        } else {
            super.updateWith( target, change );
        }
    }
}
