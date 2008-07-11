package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition
import com.mindalliance.channels.playbook.ifm.definition.TaskDefinition
import com.mindalliance.channels.playbook.support.RefUtils

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:05:53 AM
*/
// Responsibility of a Responsibility to any agent with a given role, possibly within a given type of organization
// and possiblylimited to some location
class Responsibility extends BeanImpl implements Defineable {

    InformationDefinition informationSpec = new InformationDefinition() // what must be known if knowable -- required
    boolean affirmed = true // when matching, known information is true (or false if not affirmed) ...
    TaskDefinition taskSpec = new TaskDefinition()  // do a matching task

    @Override
    protected List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['negated', 'defined'])
    }

    boolean isNegated() {
        return !affirmed
    }

    boolean getNegated() {
        return !affirmed
    }

    void setNegated(boolean negated) {
        affirmed = !negated
    }

    String toString() {
        "${this.summary()}"
    }

    boolean isDefined() {
        return !informationSpec.matchesAll() // Task can be unspecified
    }

    String summary() {
        String mustKnow = RefUtils.summarize(informationSpec.description,20) ?: 'N/A'
        String mustDo = RefUtils.summarize(taskSpec.description, 20) ?: 'N/A'
        return "Must know: $mustKnow; must do: $mustDo"
    }

}