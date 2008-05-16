package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import com.mindalliance.channels.playbook.ifm.info.Timing

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:44:43 PM
*/
class Task extends InformationAct {

    Timing duration = new Timing(amount:0)
    Ref taskType
    List<InformationTemplate> informationNeeds = []

}