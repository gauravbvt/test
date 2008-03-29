package com.mindalliance.channels.playbook.ifm.project.scenario

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.context.model.Role

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:03:14 PM
*/
class Agent extends ScenarioElement  implements Comparable {

    String tag ='' // e.g. 'Blue team'
    boolean individual = true // is thi Agent meant to represent an individual, as opposed to a team?
    List<Ref> identities = [] // a list of Resource
    List<Role> roles =[] // roles in addition to those implied by identities

    int compareTo(Object o) {
        if ( o != null && o instanceof Agent )
            return name.compareTo( ((Agent) o).getName() )
        else
            throw new IllegalArgumentException()
    }

}