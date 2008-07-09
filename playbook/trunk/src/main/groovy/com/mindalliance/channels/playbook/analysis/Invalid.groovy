package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2008
 * Time: 7:36:21 AM
 */
class Invalid extends AnalysisElement {

    Ref element
    String problem = ''

    Invalid(IfmElement el, String problem) {
        this.element = el.reference
        this.problem = problem
    }

    String toString() {
        return "Invalid $element : $problem"
    }

}