package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:41:27 PM
*/
class FlowAct extends InformationAct {

    Ref targetResource

    @Override
    boolean isFlowAct() {
        return true
    }



}