package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ifm.info.Timing
import org.joda.time.Duration

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:27:48 PM
*/
/* abstract*/ class InformationAct extends PlaybookElement  implements Describable {

    String description = ''
    Ref resource // a resource
    Ref causeInformationAct // another information act, if any 
    Timing delay = new Timing(amount:0) // from start of the information act it follows, else from T0 in playbook
    Timing duration = new Timing(amount:0)

    boolean isFlowAct() {
        return false
    }

    boolean isSharingAct() {
        return false
    }

    boolean isAfter(InformationAct act) {
        if (hasTransitiveCause(act)) return true
        return (act.startTime() > this.startTime())
    }

    boolean hasTransitiveCause(InformationAct act) {     // is act a direct or indirect cause of this information act
        if (!causeInformationAct) return false
        if (causeInformationAct == act) return true
        if (causeInformationAct && causeInformationAct.hasTransitiveCause(act)) return true
        return false
    }

    Duration startTime() {
       Duration startTime = Duration.ZERO;
       if (causeInformationAct) {
          startTime = causeInformationAct.startTime() + delay.getDuration() 
       }
       else {
          startTime = delay.getDuration();
       }
       return startTime;
    }

}