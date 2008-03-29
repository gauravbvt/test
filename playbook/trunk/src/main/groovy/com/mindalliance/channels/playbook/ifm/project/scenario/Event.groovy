package com.mindalliance.channels.playbook.ifm.project.scenario

import com.mindalliance.channels.playbook.ifm.Level
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:48:01 AM
*/
class Event extends Occurrence {

   Level severity = Level.LEVEL_NONE // default
   List<Ref> terminators = [] // Causes that, when combined, terminate the Event

}