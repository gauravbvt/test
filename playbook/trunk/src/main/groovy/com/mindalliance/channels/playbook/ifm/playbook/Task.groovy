package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:44:43 PM
*/
class Task extends InformationAct {

    Ref TaskType
    // List<Information> newInformations = []   // -- redundant: replaceable by caused Observations
    List<InformationTemplate> neededInformations = []

}