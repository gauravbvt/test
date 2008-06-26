package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ref.Bean

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2008
 * Time: 9:49:14 PM
 */
class TaskSpec extends SpecImpl {

    List<Ref> taskTypes = []
    Timing timing = new Timing(amount:0) // maximum response time -- defaults to "zero"

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['defined'])
    }

    String taskTypesSummary() {
        String summary = "Must do "
        if (taskTypes) {
            taskTypes.each {tt -> summary += "${tt.name}," }
        }
        else {
            summary += 'nothing '
        }
        return summary.substring(0, summary.size()-1)
    }


    public boolean doesMatch(Bean bean) {
         // TODO
    }

    public boolean narrows(Spec spec) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isDefined() {
        return !taskTypes.isEmpty()
    }
}