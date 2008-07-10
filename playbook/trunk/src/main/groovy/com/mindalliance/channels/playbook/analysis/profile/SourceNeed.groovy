package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 8, 2008
 * Time: 4:42:39 PM
 */
class SourceNeed extends ProfileElement {

    AgentSpecification sourceSpec
    Information information

    SourceNeed(InformationAct act, Ref agent, AgentSpecification sourceSpec, Information information) {
        super(act, agent)
        this.sourceSpec = sourceSpec
        this.information = information
        if (information.timeToLive.isDefined()) {
            end = start.plus(information.timeToLive.duration)
        }
    }

}