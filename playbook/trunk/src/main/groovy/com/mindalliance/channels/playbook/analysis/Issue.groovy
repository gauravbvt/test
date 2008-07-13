package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ref.Referenceable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:46:30 PM
*/
class Issue extends AnalysisElement {

    Referenceable element
    Referenceable cause
    String tag

    Issue(Referenceable element, Referenceable cause, String tag) {
        this.element = element
        this.cause = cause
        this.tag = tag
    }

}