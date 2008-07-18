package com.mindalliance.channels.playbook.analysis.problem

import com.mindalliance.channels.playbook.ref.Referenceable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:46:30 PM
*/
class Issue extends Problem {

    private static final Map ISSUE_TAGS = [
            noContact:"No contact information is given.",
            missingProtocol: "There is no access protocol through which to satisfy the information need.",
            sharingWithoutCommitment: "Information sharing without standing agreement or prior commitment.",
            criticalNeedUnsatisfied: "An information need critical to the successof the task is unsatisfied."
            ]

    Referenceable cause

    Issue(Referenceable element, Referenceable cause, String tag) {
        super(element, tag)
        this.cause = cause
    }

    public String textForTag(String tag) {
        return (String)ISSUE_TAGS[tag]
    }
}