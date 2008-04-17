package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:27:48 PM
*/
/* abstract*/ class InformationAct extends IfmElement {

    Ref actor // a resource
    Ref cause // another information act, if any
    Duration delay = Duration.ZERO // from start of the information act it follows, else from T0 in playbook
    Duration duration = Duration.ZERO
    boolean suggested = false  // true if in NeedToAct (will be deleted when NeedToAct retracted)

    boolean isFlowAct() {
        return false
    }

    boolean isSharingAct() {
        return false
    }

}