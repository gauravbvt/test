package com.mindalliance.channels.model;

import java.text.MessageFormat;

/**
 * An arrow between two nodes in the information flow graph.
 */
public class Flow extends ModelObject {

    // TODO Should we annotate a flow as primary vs seconday (when multiple flows are mutually redundant)?

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

    /**
     * Get a title for an out-of-context flow.
     * @return a short description of the flow, for titles and lists.
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
     * Get a title for this flow, when used in the context of a requirement.
     * @return a short description of the flow, for titles and lists.
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
     * Get a title for this flow, when used in the context of an outcome.
     * @return a short description of the flow, for titles and lists.
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
}
