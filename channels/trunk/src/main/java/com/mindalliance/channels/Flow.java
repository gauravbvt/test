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

    /** If flow applies to all potential sources/targets. */
    private boolean all;

    protected Flow() {
    }

    public boolean isAskedFor() {
        return askedFor;
    }

    /**
     * Set if this flow is notified or asked for.
     * @param askedFor true is information is asked for
     */
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
                                                  : "{1} notifying {2} of {0}",

            content, getShortName( getSource() ), getShortName( getTarget() ) );
    }

    private String getShortName( Node node ) {
        String result = "somebody";

        if ( node != null ) {
            final String sourceName = node.getName();
            if ( sourceName != null && !sourceName.trim().isEmpty() ) {
                result = sourceName;
                if ( node.isPart() && ( (Part) node ).isRole() )
                    result = MessageFormat.format( isAll() ? "every {0}" : "any {0}", result );
            }
        }

        return result;
    }

    /**
     * Provide a description of the flow, when viewed as a requirement.
     * @return the description
     */
    public String getRequirementTitle() {
        final boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                 noName       ? isAskedFor() ? "Obtaining something from {1}, when needed"
                                             : "Notified of something by {1}"
               : isAskedFor() ? "Obtaining {0} from {1}, when needed"
                              : "Notified of {0} by {1}",
                getName(), getShortName( getSource() ) );
    }

    /**
     * Provide a description of the flow, when viewed as an outcome.
     * @return the description
     */
    public String getOutcomeTitle() {
        final boolean noName = getName() == null || getName().trim().isEmpty();
        return MessageFormat.format(
                noName       ? isAskedFor() ? "Providing {1} with something, when asked"
                                            : "Notifying {1} of something"
              : isAskedFor() ? "Providing {1} with {0}, when asked"
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

    public boolean isAll() {
        return all;
    }

    public void setAll( boolean all ) {
        this.all = all;
    }

    /**
     * Initialize relevant properties from another flow.
     * @param flow the other flow
     */
    public void initFrom( Flow flow ) {
        setName( flow.getName() );
        setDescription( flow.getDescription() );
        setMaxDelay( flow.getMaxDelay() );
        setAskedFor( flow.isAskedFor() );
        setChannel( flow.getChannel() );
        setCritical( flow.isCritical() );

    }
}
