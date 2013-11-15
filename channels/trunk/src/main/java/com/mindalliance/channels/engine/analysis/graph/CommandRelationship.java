package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.Agent;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 10:54 AM
 */
public class CommandRelationship<T extends Agent> extends Relationship {

    private final Contact supervisor;
    private final Contact supervised;

    public CommandRelationship( Contact supervisor, Contact supervised ) {
        super( supervisor.getAgent(), supervised.getAgent() );
        this.supervisor = supervisor;
        this.supervised = supervised;
    }

    public Contact getSupervised() {
        return supervised;
    }

    public Contact getSupervisor() {
        return supervisor;
    }
}
