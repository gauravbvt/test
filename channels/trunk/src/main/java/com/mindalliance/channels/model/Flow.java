package com.mindalliance.channels.model;

/**
 * An arrow between two nodes in the information flow graph.
 */
public class Flow extends NamedObject {

    /** The source of the flow. */
    private Node source;

    /** The target of the flow. */
    private Node target;

    /** A string describing the channel of communication involved. */
    private String channel;

    /** If this flow is critical to either source or target. */
    private boolean critical;

    /** If this flow only happens on request from either the source or target. */
    private boolean askedFor;

    /** A string describing how much lag time is expected for this flow. */
    private String maxDelay;

    public Flow() {
    }

    public boolean isAskedFor() {
        return askedFor;
    }

    public void setAskedFor( boolean askedFor ) {
        this.askedFor = askedFor;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel( String channel ) {
        this.channel = channel;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical( boolean critical ) {
        this.critical = critical;
    }

    public String getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay( String maxDelay ) {
        this.maxDelay = maxDelay;
    }

    public Node getSource() {
        return source;
    }

    public void setSource( Node source ) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget( Node target ) {
        this.target = target;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        // TODO implement flow print strings.
        return super.toString();
    }
}
