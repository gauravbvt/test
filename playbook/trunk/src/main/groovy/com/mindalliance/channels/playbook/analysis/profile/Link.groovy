package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:09:34 PM
 */
class Link extends ProfileElement {

    String relationshipName
    Ref toAgent

    Link(InformationAct act, Ref agent, String relationshipName, Ref toAgent) {
        super(act, agent)
        this.relationshipName = relationshipName
        this.toAgent = toAgent
    }

}