package com.mindalliance.channels.playbook.analysis.problem

import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.ifm.Named

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 18, 2008
 * Time: 2:56:43 PM
 */
abstract class Problem extends AnalysisElement implements Named {

    Referenceable element
    String tag

    Problem(Referenceable element,String tag) {
        this.element = element
        this.tag = tag
    }

    String labelText() {
        return textForTag(tag) ?: this.name
    }

    public String getName() {
        return RefUtils.deCamelCase(tag)
    }

    abstract String textForTag(String tag);
}