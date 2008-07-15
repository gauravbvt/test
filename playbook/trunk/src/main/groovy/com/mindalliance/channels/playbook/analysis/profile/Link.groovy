package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Agent

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:09:34 PM
 */
class Link extends ProfileElement {

    String relationshipName
    Agent toAgent

    Link(InformationAct act, Agent agent, String relationshipName, Agent toAgent) {
        super(act, agent)
        this.relationshipName = relationshipName
        this.toAgent = toAgent
    }

    Link(Referenceable cause, Playbook playbook, Agent agent, String relationshipName, Agent toAgent) {
        super(playbook, cause, agent)
        this.relationshipName = relationshipName
        this.toAgent = toAgent
    }

}