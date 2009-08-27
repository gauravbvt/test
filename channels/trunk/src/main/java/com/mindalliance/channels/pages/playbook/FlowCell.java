package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Actor;

/**
 * A flow from/to one actor.
 */
final class FlowCell {

    /** The actor for this cell (maybe null). */
    private Actor actor;

    /** The flow. */
    private final Flow flow;

    FlowCell( Actor actor, Flow flow ) {
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
        return flow == null;
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
}
