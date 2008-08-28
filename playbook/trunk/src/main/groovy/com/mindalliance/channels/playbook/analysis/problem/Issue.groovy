package com.mindalliance.channels.playbook.analysis.problem

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.InProject
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:46:30 PM
*/
class Issue extends Problem implements InProject {

    private static final Map ISSUE_TAGS = [
            noContact:"No contact information is given.",
            missingProtocol: "There is no access protocol through which to satisfy an information need.",
            sharingWithoutCommitment: "Information sharing without standing agreement or prior commitment.",
            criticalNeedUnsatisfied: "An information need critical to the success of the task is unsatisfied."
            ]

    Referenceable cause

    Issue(Referenceable element, Referenceable cause, String tag) {
        super(element, tag)
        this.cause = cause
    }

    String textForTag(String tag) {
        return (String)ISSUE_TAGS[tag]
    }

    Ref getProject() {
        if (element instanceof InProject) {
            return ((InProject)element).getProject()
        }
        else {
            return null
        }
    }
}