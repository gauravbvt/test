package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ifm.info.Timing

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:27:48 PM
*/
/* abstract*/ class InformationAct extends PlaybookElement  implements Describable {

    Ref playbook
    String description = ''
    Ref actor // a resource
    Ref cause // another information act, if any
    Timing delay = new Timing(amount:0) // from start of the information act it follows, else from T0 in playbook
    Timing duration = new Timing(amount:0)
    boolean suggested = false  // true if in NeedToAct (will be deleted when NeedToAct retracted)

    boolean isFlowAct() {
        return false
    }

    boolean isSharingAct() {
        return false
    }

}