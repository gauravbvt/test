package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.spec.Spec

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 20, 2008
 * Time: 5:07:44 PM
 */
class RelationshipSpec extends BeanImpl implements Spec {

    String relationshipName = ''
    Ref agent // an agent defined within scope, or with any agent if null
    boolean relationshipFromSpecified = true // else to agent specified by this relationshipSpec

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['defined']
    }

    boolean isDefined() {
        return relationshipName != null && !relationshipName.isEmpty()
    }

    // Queries

    // an agent has a relationship that matches relationshipSpec at start of event
    boolean matchedBy(Ref matchedAgent, Event event) {
        if (!isDefined()) return true
        Ref fromAgent
        Ref toAgent
        if (relationshipFromSpecified) {
            fromAgent = matchedAgent
            toAgent = agent
        }
        else {
            fromAgent = agent
            toAgent =  matchedAgent
        }
        // look at permanent relationships (project scope)
        if (event.getProject().relationships.any {rel ->
            rel.name == relationshipName &&
            event.playbook.agentImplied(fromAgent, rel.fromAgent) &&
            event.playbook.agentImplied(toAgent, rel.toAgent)
        }) return true
        // look at transient relationships  (playbook scope, created by Associations) -- for resource as agent, team member or implied group member (cycles?)
        return event.playbook.createsRelationshipBefore(new Relationship(fromAgent: fromAgent, name: relationshipName, toAgent: toAgent), event)
    }

    // End queries

    boolean matchesRelationship(Relationship relationship) {

    }

    public boolean matches(Ref element) {
        return false;  //Todo
    }

    public boolean narrows(Spec spec) {
        return false;  //Todo
    }
}