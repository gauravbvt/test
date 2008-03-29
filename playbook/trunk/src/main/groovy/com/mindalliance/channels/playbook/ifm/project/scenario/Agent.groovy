package com.mindalliance.channels.playbook.ifm.project.scenario

import com.mindalliance.channels.playbook.ifm.IfmElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:03:14 PM
*/
class Agent extends ScenarioElement  implements Comparable {

    String id
    String name = "No name"
    String description = ''

    int compareTo(Object o) {
        if ( o != null && o instanceof Agent )
            return name.compareTo( ((Agent) o).getName() )
        else
            throw new IllegalArgumentException()
    }

}