package com.mindalliance.channels.model;

/**
 * A reference to another scenario.
 */
public class ScenarioNode extends Node {

    /** The scenario referred to by this node. */
    private Scenario embeddedScenario;

    public ScenarioNode() {
    }

    /**
     * Utility constructor for tests.
     * @param scenario the imported scenario
     */
    public ScenarioNode( Scenario scenario ) {
        super( scenario.getName() );
        setEmbeddedScenario( scenario );
    }

    public final Scenario getEmbeddedScenario() {
        return embeddedScenario;
    }

    public final void setEmbeddedScenario( Scenario embeddedScenario ) {
        this.embeddedScenario = embeddedScenario;
        setName( embeddedScenario.getName() );
    }

    @Override
    public boolean isScenarioNode() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return embeddedScenario.toString();
    }
}
