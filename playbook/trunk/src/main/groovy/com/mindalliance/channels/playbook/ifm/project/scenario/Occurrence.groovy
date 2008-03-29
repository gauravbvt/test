package com.mindalliance.channels.playbook.ifm.project.scenario

import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:06:03 PM
*/
class Occurrence extends ScenarioElement {

    List<Ref> triggers = [] // list of causes that, when combined (ANDed, cause this Occurrence
    Duration delay = new Duration(0) // default is no delay between
    Duration maxDuration  // if unspecified, until "end of scenario" or until terminated (event, activity)

}