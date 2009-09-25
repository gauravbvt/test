package com.mindalliance.channels.pages.components.plan;


import com.mindalliance.channels.analysis.graph.ScenarioRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ExternalFlowsPanel;
import com.mindalliance.channels.pages.components.ScenarioCausesPanel;
import com.mindalliance.channels.pages.components.diagrams.PlanMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


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
     * Whether to group scenarios by phase.
     */
    private boolean groupByPhase;
    /**
     * Whether to group by event.
     */
    private boolean groupByEvent = true;
    /**
     * Selected phase or event in plan.
     */
    private ModelObject selectedGroup;
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

    public PlanMapPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addGroupingChoices();
        addPlanSizing();
        addPlanMapDiagramPanel();
        addFlowsTitleLabel();
        addExternalFlowsPanel();
        addCausesTitleLabel();
        addCausesPanel();
    }

    private void addGroupingChoices() {
        CheckBox groupByPhaseCheckBox = new CheckBox(
                "groupByPhase",
                new PropertyModel<Boolean>( this, "groupByPhase" ) );
        groupByPhaseCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
            }
        } );
        add( groupByPhaseCheckBox );
        CheckBox groupByEventCheckBox = new CheckBox(
                "groupByEvent",
                new PropertyModel<Boolean>( this, "groupByEvent" ) );
        groupByEventCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                refresh( target );
            }
        } );
        add( groupByEventCheckBox );
    }

    public boolean isGroupByPhase() {
        return groupByPhase;
    }

    public void setGroupByPhase( boolean groupByPhase ) {
        this.groupByPhase = groupByPhase;
        groupByEvent = !groupByPhase;
        selectedGroup = null;
    }

    public boolean isGroupByEvent() {
        return groupByEvent;
    }

    public void setGroupByEvent( boolean groupByEvent ) {
        this.groupByEvent = groupByEvent;
        groupByPhase = !groupByEvent;
        selectedGroup = null;
    }

    private void addPlanSizing() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = ".plan .picture";
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            @Override
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
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                diagramSize = new double[2];
                addPlanMapDiagramPanel();
                target.addComponent( planMapDiagramPanel );
            }
        } );
        add( fullSize );
    }


    private void addPlanMapDiagramPanel() {
        Settings settings = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ? new Settings(
                ".plan .picture", null, null, true, true )
                : new Settings( ".plan .picture", null, diagramSize, true, true );
        planMapDiagramPanel = new PlanMapDiagramPanel(
                "plan-map",
                new PropertyModel<ArrayList<Scenario>>( this, "allScenarios" ),
                groupByPhase,
                groupByEvent,
                selectedGroup,
                selectedScenario,
                selectedScRel,
                settings );
        planMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( planMapDiagramPanel );
    }

    private void addFlowsTitleLabel() {
        Label flowsTitleLabel = new Label( "flows-title",
                new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        addOrReplace( flowsTitleLabel );
    }

    private void addCausesTitleLabel() {
        Label causesTitleLabel = new Label( "causes-title",
                new PropertyModel<String>( this, "causesTitle" ) );
        causesTitleLabel.setOutputMarkupId( true );
        addOrReplace( causesTitleLabel );
    }

    private void addExternalFlowsPanel() {
        ExternalFlowsPanel externalFlowsPanel = new ExternalFlowsPanel(
                "flows",
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
                getScenarioRelationships(),
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
    public List<Scenario> getAllScenarios() {
        return getQueryService().list( Scenario.class );
    }

    /**
     * Get flows title.
     *
     * @return a string
     */
    public String getFlowsTitle() {
        if ( selectedGroup != null ) {
            if ( groupByPhase ) {
                return "Flows connecting scenarios in phase \""
                        + selectedGroup.getName()
                        + "\"";
            } else {
                return "Flows connecting scenarios about event \""
                        + selectedGroup.getName()
                        + "\"";
            }
        } else if ( selectedScenario != null ) {
            return "Flows connecting \""
                    + selectedScenario.getName()
                    + "\" to other scenarios";
        } else if ( selectedScRel != null ) {
            Scenario fromScenario = selectedScRel.getFromScenario( getQueryService() );
            Scenario toScenario = selectedScRel.getToScenario( getQueryService() );
            if ( fromScenario == null || toScenario == null ) {
                return "*** You need to refresh ***";
            } else {
                return "Flows connecting \""
                        + fromScenario.getName()
                        + "\" to \""
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
        if ( selectedGroup != null ) {
            if ( groupByPhase ) {
                return "Causations for scenarios in phase \""
                        + selectedGroup.getName()
                        + "\"";
            } else {
                return "Causations for scenarios about event \""
                        + selectedGroup.getName()
                        + "\"";
            }
        } else if ( selectedScenario != null ) {
            return "Causations for scenario \""
                    + selectedScenario.getName()
                    + "\"";
        } else if ( selectedScRel != null ) {
            Scenario fromScenario = selectedScRel.getFromScenario( getQueryService() );
            Scenario toScenario = selectedScRel.getToScenario( getQueryService() );
            if ( fromScenario == null || toScenario == null ) {
                return "*** You need to refresh ***";
            } else {
                return "How \""
                        + fromScenario.getName()
                        + "\" impacts \""
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
        } else if ( selectedGroup != null ) {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Scenario> scenariosInGroup = getScenariosInGroup();
            List<Scenario> allScenarios = getAllScenarios();
            for ( Scenario scenario : allScenarios ) {
                for ( Scenario other : allScenarios ) {
                    if ( !scenario.equals( other )
                            &&
                            ( scenariosInGroup.contains( scenario )
                                    || scenariosInGroup.contains( other ) ) ) {
                        ScenarioRelationship scRel = getQueryService().findScenarioRelationship( scenario, other );
                        if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                    }
                }
            }
            return externalFlows;
        } else if ( selectedScenario != null ) {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Scenario> allScenarios = getAllScenarios();
            for ( Scenario other : allScenarios ) {
                if ( !selectedScenario.equals( other ) ) {
                    ScenarioRelationship scRel = getQueryService().findScenarioRelationship( selectedScenario, other );
                    if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                    scRel = getQueryService().findScenarioRelationship( other, selectedScenario );
                    if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                }
            }
            return externalFlows;
        } else {
            List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
            List<Scenario> allScenarios = getAllScenarios();
            for ( Scenario scenario : allScenarios ) {
                for ( Scenario other : allScenarios ) {
                    if ( !scenario.equals( other ) ) {
                        ScenarioRelationship scRel = getQueryService().findScenarioRelationship( scenario, other );
                        if ( scRel != null ) externalFlows.addAll( scRel.getExternalFlows() );
                    }
                }
            }
            return externalFlows;
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<Scenario> getScenariosInGroup() {
        return (List<Scenario>) CollectionUtils.select(
                getAllScenarios(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        if ( selectedGroup != null ) {
                            Scenario scenario = (Scenario) obj;
                            if ( groupByPhase ) {
                                return scenario.getPhase().equals( selectedGroup );
                            } else {
                                return scenario.getEvent().equals( selectedGroup );
                            }
                        } else {
                            return true;
                        }
                    }
                }
        );
    }

    /**
     * Get scenario relationships.
     *
     * @return a list of scenario relationships
     */
    public List<ScenarioRelationship> getScenarioRelationships() {
        List<ScenarioRelationship> scRels = new ArrayList<ScenarioRelationship>();
        if ( selectedScRel != null ) {
            scRels.add( selectedScRel );
        } else if ( selectedGroup != null ) {
            List<Scenario> scenariosInGroup = getScenariosInGroup();
            List<Scenario> allScenarios = getAllScenarios();
            for ( Scenario scenario : allScenarios ) {
                for ( Scenario other : allScenarios ) {
                    if ( !scenario.equals( other )
                            &&
                            ( scenariosInGroup.contains( scenario )
                                    || scenariosInGroup.contains( other ) ) ) {
                        ScenarioRelationship scRel =
                                getQueryService().findScenarioRelationship( scenario, other );
                        if ( scRel != null ) scRels.add( scRel );
                    }
                }
            }
        } else if ( selectedScenario != null ) {
            List<Scenario> allScenarios = getAllScenarios();
            for ( Scenario other : allScenarios ) {
                if ( !selectedScenario.equals( other ) ) {
                    ScenarioRelationship scRel = getQueryService().
                            findScenarioRelationship( selectedScenario, other );
                    if ( scRel != null ) scRels.add( scRel );
                    scRel = getQueryService().findScenarioRelationship( other, selectedScenario );
                    if ( scRel != null ) scRels.add( scRel );
                }
            }
        } else {
            List<Scenario> allScenarios = getAllScenarios();
            for ( Scenario scenario : allScenarios ) {
                for ( Scenario other : allScenarios ) {
                    if ( !scenario.equals( other ) ) {
                        ScenarioRelationship scRel = getQueryService().findScenarioRelationship( scenario, other );
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
            return selectedScRel.getToScenario( getQueryService() );
        } else {
            return null;
        }
    }

    public void refresh( AjaxRequestTarget target ) {
        addPlanMapDiagramPanel();
        addFlowsTitleLabel();
        addExternalFlowsPanel();
        addCausesTitleLabel();
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
            if ( changed instanceof Plan ) {
                selectedGroup = null;
                selectedScenario = null;
                selectedScRel = null;
            } else if ( changed instanceof Phase || changed instanceof Event ) {
                selectedGroup = (ModelObject) changed;
                selectedScenario = null;
                selectedScRel = null;
            } else if ( changed instanceof Scenario ) {
                selectedGroup = null;
                selectedScenario = (Scenario) changed;
                selectedScRel = null;
            } else if ( changed instanceof ScenarioRelationship ) {
                selectedGroup = null;
                selectedScenario = null;
                selectedScRel = (ScenarioRelationship) changed;
            }
            // Don't percolate chane on selection of app, scenario or scenario relationship.
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected Plan getPlan() {
        return (Plan) getModel().getObject();
    }

}
