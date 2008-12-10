package com.mindalliance.channels;

import java.text.MessageFormat;

/**
 * An arrow between two nodes in the information flow graph.
 */
public abstract class Flow extends ModelObject {

    // TODO Should we annotate a flow as primary vs seconday (when multiple flows are mutually redundant)?
    /** A string describing the channel of communication involved. */
    private String channel;

    /** If this flow is critical to either source or target. */
    private boolean critical;

    /** If this flow only happens on request from either the source or target. */
    private boolean askedFor;

    /** A string describing how much lag time is expected for this flow. */
    private String maxDelay;

    protected Flow() {
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

    /**
     * Provide a out-of-context description of the flow.
     * @return the description
     */
    public String getTitle() {
        String content = getName();
        if ( content == null || content.trim().isEmpty() )
            content = "something";
        return MessageFormat.format( isAskedFor() ? "{2} asking {1} about {0}"
                                                  : "{1} communicating {0} to {2}",

            content, getShortName( getSource() ), getShortName( getTarget() ) );
    }

    private static String getShortName( Node node ) {
        if ( node != null ) {
            final String sourceName = node.getName();
            if ( sourceName != null && !sourceName.trim().isEmpty() )
                return sourceName;
        }

        return "somebody";
    }

    /**
     * Provide a description of the flow, when viewed as a requirement.
     * @return the description
     */
    public String getRequirementTitle() {
        final boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                 noName       ? isAskedFor() ? "Questioning {1}, when needed"
                                             : "Responding to {1}"
               : isAskedFor() ? "Obtaining {0} from {1}, when needed"
                              : "Receiving {0} from {1}",
                getName(), getShortName( getSource() ) );
    }

    /**
     * Provide a description of the flow, when viewed as an outcome.
     * @return the description
     */
    public String getOutcomeTitle() {
        final boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                noName       ? isAskedFor() ? "Answering {1}, when asked"
                                            : "Notifying {1}"
              : isAskedFor() ? "Communicating {0} to {1}, when asked"
                             : "Notifying {1} of {0}",
                getName(), getShortName( getTarget() ) );
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getTitle();
    }

    /** @return Get the source of this flow. */
    public abstract Node getSource();

    /** @return the target of this flow. */
    public abstract Node getTarget();

    /**
     * Set the source of this flow.
     * Note: this method should not be called directly.
     * @see Scenario#connect( Node, Node )
     * @param source the source node.
     */
    abstract void setSource( Node source );

    /**
     * Set the target of this flow.
     * Note: this method should not be called directly.
     * @see Scenario#connect( Node, Node )
     * @param target the target node.
     */
    abstract void setTarget( Node target );

    /**
     * Disconnect from source and target.
     */
    public abstract void disconnect();

    /**
     * @return true for internal flows; false for external flows.
     */
    public abstract boolean isInternal();
}
