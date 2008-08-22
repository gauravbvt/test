package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 11, 2008
 * Time: 11:32:14 AM
 */
class Trust extends ProfileElement {

    AgentSpecification sourceSpec // if null then agent trusts anyone about information that is matched by info spec
    InformationDefinition informationSpec

    Trust(NeedToKnow n2k, Agent agent, InformationDefinition infoSpec, AgentSpecification sourceSpec) {
        super(n2k, agent)
        this.informationSpec = infoSpec
        this.sourceSpec = sourceSpec
    }

    Trust(NeedToKnow n2k, Agent agent, InformationDefinition infoSpec) {
        super(n2k, agent)
        this.informationSpec = infoSpec
    }

    boolean isAnySourceTrusted() {
        return sourceSpec == null
    }

}