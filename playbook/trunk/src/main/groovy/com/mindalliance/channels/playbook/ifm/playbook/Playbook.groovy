package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:26:15 PM
*/
class Playbook extends IfmElement {

    String name = ''
    String description = ''
    List<Ref> informationActs = []

    List<Ref> findInformationActsOfResource(Ref resource) {
        List<Ref> ias = informationActs.findall {ia ->
            (ia.actor == resource) || (ia.isFlowAct() && ia.targetResource == resource)
        }
    }
}