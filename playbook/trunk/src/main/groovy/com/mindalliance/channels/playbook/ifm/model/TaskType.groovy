package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:52:55 PM
*/
class TaskType extends ElementType {

    List<String> purposes = [] // usual purposes of tasks of this type
    List<InformationDefinition> inputs = [] // kind of information typically needed by tasks of this type
    List<EventType> eventTypes = [] // types of events that can be caused by tasks of this type (and thus become detectable)

}