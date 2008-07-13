package com.mindalliance.channels.playbook.analysis.profile

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 8:13:10 PM
 */
class Belief extends ProfileElement {    // TODO -- belief should accumulate trustworthy knows instead

    Know know

    Belief(Know know) {  // what an agent knows AND believes
        super(know, know.agent)
        this.know = know
    }
}