package com.mindalliance.channels.playbook.analysis.compliance

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.environment.Policy

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:45:50 PM
*/
class PolicyCompliance extends Compliance {

    Policy policy

        PolicyCompliance(boolean compliant, Ref agent, Referenceable complying, String tag, Policy policy) {
        super(compliant, agent, complying, tag)
        this.policy = policy
    }

}