package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.spec.TaskSpec
import com.mindalliance.channels.playbook.ifm.spec.InformationSpec

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:05:53 AM
*/
// Responsibility of a Responsibility to any agent with a given role, possibly within a given type of organization
// and possiblylimited to some location
class Responsibility extends BeanImpl {

    InformationSpec informationSpec = new InformationSpec() // what must be known if knowable -- required
    TaskSpec taskSpec = new TaskSpec()

    String toString() {
        "${this.informationSpecSummary()}. ${taskSpec.taskTypesSummary()}"
    }

    private String informationSpecSummary() {
        String summary = "Must know of "
        if (informationSpec.isDefined()) {
            informationSpec.eventSpec.eventTypes.each {et -> summary += "${et.name}," }
        }
        else {
            summary += 'nothing '
        }
        return summary.substring(0, summary.size()-1)
    }

}