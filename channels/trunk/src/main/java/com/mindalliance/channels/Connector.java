package com.mindalliance.channels;

/**
 * A connector to unspecified node(s) outside of the scenario.
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
