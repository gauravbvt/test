package com.mindalliance.channels.model;

/**
 * A reference to another scenario.
 */
public class ScenarioNode extends Node {

    /** The scenario referred to by this node. */
    private Scenario scenario;

    public ScenarioNode() {
    }

    /**
     * Utility constructor for tests.
     * @param scenario the imported scenario
     */
    public ScenarioNode( Scenario scenario ) {
        super( scenario.getName() );
        setScenario( scenario );
    }

    public final Scenario getScenario() {
        return scenario;
    }

    public final void setScenario( Scenario scenario ) {
        this.scenario = scenario;
    }

    @Override
    public boolean isScenarioNode() {
        return true;
    }
}
