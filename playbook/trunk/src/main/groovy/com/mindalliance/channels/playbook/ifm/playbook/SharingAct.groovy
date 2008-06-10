package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:37:38 PM
*/
/* abstract */ class SharingAct extends FlowAct {

    Information information = new Information()

    String toString() {
        return "${this.type} of $information"
    }

    @Override
    boolean isSharingAct() {
        return true
    }

    boolean hasInformation() {
        return true
    }


}