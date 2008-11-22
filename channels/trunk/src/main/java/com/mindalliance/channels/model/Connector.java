package com.mindalliance.channels.model;

/**
 * A connector to an unspecified node outside of the scenario.
 */
public class Connector extends Node {

    public Connector() {
    }

    @Override
    public boolean isConnector() {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Connector";
    }
}
