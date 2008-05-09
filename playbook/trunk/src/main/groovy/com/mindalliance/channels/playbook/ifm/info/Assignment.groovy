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

    List<InformationTemplate> informationTemplates = [] // what must be known if knowable -- required
    List<Ref> taskTypes = [] // what to do, if anything, when all the above is known (any of)
    Timing timing = new Timing(amount:0) // maximum reaction time -- defaults to "zero"

    String toString() {
        "${this.informationTemplatesSummary()}. ${this.taskTypesSummary()}"
    }

    private String informationTemplatesSummary() {
        String summary = "Must know of "
        if (informationTemplates) {
            informationTemplates.each {info ->
                info.eventSpec.eventTypes.each {et -> summary += "${et.name}," }
            }
        }
        else {
            summary += 'nothing '
        }
        return summary.substring(0, summary.size()-1)
    }

    private String taskTypesSummary() {
        String summary = "Must do "
        if (taskTypes) {
            taskTypes.each {tt -> summary += "${tt.name}," }
        }
        else {
            summary += 'nothing '
        }
        return summary.substring(0, summary.size()-1)
    }
}