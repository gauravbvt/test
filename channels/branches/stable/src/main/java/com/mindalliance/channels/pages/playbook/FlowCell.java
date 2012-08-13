package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;

/**
 * A flow from/to one actor.
 */
final class FlowCell {

    /** The actor for this cell (maybe null). */
    private final Actor actor;

    /** The flow. */
    private final Flow flow;

    /** If the flow is incoming. */
    private boolean incoming;

    FlowCell( Actor actor, Flow flow, boolean incoming ) {
        this.incoming = incoming;
        this.actor = actor;
        this.flow = flow;
    }

    public Actor getActor() {
        return actor;
    }

    /**
     * @return if this is not an empty cell
     */
    public boolean hasFlow() {
        return flow != null;
    }

    public String getDescription() {
        return flow == null ? "" : ensurePeriod( flow.getDescription() );
    }

    private static String ensurePeriod( String text ) {
        return !text.isEmpty() && text.charAt( text.length() - 1 ) != '.' ?
               text + '.' : text;
    }

    public String getCriticality() {
        return flow != null && flow.isCritical() ? "Critical: " : "";
    }

    public String getDelayString() {
        return flow == null ? "" : flow.getMaxDelay().toString();
    }

    public Flow getFlow() {
        return flow;
    }

    /**
     * @return the ID of the underlying flow (or 0)
     */
    public long getFlowId() {
        return flow == null ? 0L : flow.getId();
    }

    boolean hasActor() {
        return actor != null;
    }

    long getActorId() {
        return actor.getId();
    }

    public boolean isIncoming() {
        return incoming;
    }

    public boolean isAskedFor() {
        return flow != null && flow.isAskedFor();
    }
}
