package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import com.mindalliance.channels.playbook.ifm.info.Assignment

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:52:55 PM
*/
class TaskType extends ElementType {

    List<Ref> purposeTypes = [] // categorizing the purpose(s) of the task
    List<InformationTemplate> newInformations = [] // kinds of information usually needed
    List<InformationTemplate> neededInformations = [] // kinds of information usually produced
    List<EventType> causedEventTypes = [] // types of events that is expected to be caused by this type of task and thus become observable
    List<Assignment> newAssignments = []// kinds of assignments usually produced

}