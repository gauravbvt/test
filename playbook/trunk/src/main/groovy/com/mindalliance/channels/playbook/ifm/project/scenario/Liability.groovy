package com.mindalliance.channels.playbook.ifm.project.scenario

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:00:54 AM
*/
class Liability extends Event {

    Ref agent
    Double cost = 0.0 // in US dollars
    Ref policy

}