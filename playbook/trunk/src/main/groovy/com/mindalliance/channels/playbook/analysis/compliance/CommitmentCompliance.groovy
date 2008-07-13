package com.mindalliance.channels.playbook.analysis.compliance

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.analysis.profile.Commitment
import com.mindalliance.channels.playbook.ref.Referenceable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 2:45:08 PM
 */
class CommitmentCompliance extends Compliance {

    Commitment commitment

    CommitmentCompliance(boolean compliant, Ref agent, Referenceable complying, String tag, Commitment commitment) {
        super(compliant, agent, complying, tag)
        this.commitment = commitment
    }

}