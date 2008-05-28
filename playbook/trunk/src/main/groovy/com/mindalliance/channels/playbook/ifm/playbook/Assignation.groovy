package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Responsibility
import com.mindalliance.channels.playbook.ifm.Responsibility

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:44:12 PM
*/
class Assignation extends FlowAct {  // communication of a responsibility (the target may not be the assignee)

    Responsibility responsibility = new Responsibility()
    Ref assigneeAgent

}