package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.RefUtils

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:46:30 PM
*/
class Issue extends AnalysisElement {

    static final Map TAGS = [
            noContact:"No contact information is given.",
            missingProtocol: "There is no access protocol through which to satisfy the information need.",
            sharingWithoutCommitment: "Information sharing without standing agreement or prior commitment.",
            criticalNeedUnsatisfied: "An information need critical to the successof the task is unsatisfied."
            ]

    Referenceable element
    Referenceable cause
    String tag   // TODO -- use issue type

    Issue(Referenceable element, Referenceable cause, String tag) {
        this.element = element
        this.cause = cause
        this.tag = (String)TAGS[tag] ?: tag
    }

    String labelText() {
        return tag
    }

}