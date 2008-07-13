package com.mindalliance.channels.playbook.analysis.compliance

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ref.Referenceable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:44:19 PM
*/
abstract class Compliance extends AnalysisElement {

    boolean compliant            // compliance or not
    Referenceable complying      // what's in compliance
    Ref agent // who's complying
    String tag // tagging the compliance or lack thereof


    Compliance(boolean compliant, Ref agent, Referenceable complying, String tag) {
        super()
        this.compliant = compliant
        this.agent = agent
        this.complying = complying
        this.tag = tag
    }

}