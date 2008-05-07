package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:05:53 AM
*/
// Assignment of a Responsibility to any agent with a given role, possibly within a given type of organization
// and possiblylimited to some location
class Assignment extends BeanImpl {

    List<InformationTemplate> informationTemplates // what must be known if knowable -- required
    List<Ref> taskTypes // what to do, if anything, when all the above is known (any of)
    Timing timing = new Timing(amount:0) // maximum reaction time -- defaults to "zero"

    String toString() {
        "${this.informationTemplatesSummary()} ${this.taskTypesSummary()}"
    }

    private String informationTemplatesSummary() {
        String summary = ""
        if (informationTemplates) {
            summary += "Must know of "
            informationTemplates.each {info ->
                info.eventSpec.eventTypes.each {et -> summary += "${et.name} " }
            }
        }
        return "${summary}. "
    }

    private String taskTypesSummary() {
        String summary = ""
        if (taskTypes) {
            summary += " Must do "
            taskTypes.each {tt -> summary += "${tt.name} " }
        }
        return "${summary}. "
    }
}