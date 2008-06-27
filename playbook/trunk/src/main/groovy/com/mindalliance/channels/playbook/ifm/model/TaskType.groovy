package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.spec.InformationSpec

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:52:55 PM
*/
class TaskType extends ElementType {

    List<String> purposes = [] // purposes of the task
    List<InformationSpec> informationSpecs = [] // information needs
    List<EventType> eventTypes = [] // types of events that can be caused (and thus become observable)

}