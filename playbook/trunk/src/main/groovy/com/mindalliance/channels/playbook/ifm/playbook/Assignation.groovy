package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Assignment
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:44:12 PM
*/
class Assignation extends FlowAct {  // communication of an assignment (the target may not be the assignee)

    Assignment assignment = new Assignment()
    Ref assigneeAgent

}