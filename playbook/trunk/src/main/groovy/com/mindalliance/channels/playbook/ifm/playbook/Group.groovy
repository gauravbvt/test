package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.spec.AgentSpec

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 9:46:51 PM
 */
class Group extends PlaybookElement implements Agent {

    String name = ''
    String description = ''
    AgentSpec agentSpec = new AgentSpec()

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['resourceKinds'])
    }

    boolean isResource() {
         return false
     }

    boolean isTeam() {
         return false
     }

    boolean isGroup() {
         return true
     }

    // queries
    List<Ref> getResourcesAt(Event event) {
         return agentSpec.getResourcesAt(event)
     }
     // end queries


 }