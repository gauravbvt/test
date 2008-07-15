package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2008
 * Time: 7:36:21 AM
 */
class Invalid extends AnalysisElement {

    Referenceable element

    Invalid(Referenceable element) {
        this.element = element
    }

    String toString() {
        return "${super.toString()} : $element"
    }

}