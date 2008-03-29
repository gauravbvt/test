package com.mindalliance.channels.playbook.ifm.project.scenario.act

import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:49:11 AM
*/
class Activity extends InformationAct {

    List<Ref> terminators = []  // list of Causes that when combined terminate the activity
    Duration endDelay = new Duration(0) // delay between termination causes and termination
    List<Ref> informationNeeds = []
    List<Ref> acquiredInformations = []
    Double cost = 0.0 // in US dollars
    List<Ref> tasks // tasks to be achieved by this Activity

}