package com.mindalliance.channels.model;

/**
 * An arrow between two nodes in the information flow graph.
 */
public class Flow extends NamedObject {

    private Node source;

    private Node target;

    public Flow() {
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

}
