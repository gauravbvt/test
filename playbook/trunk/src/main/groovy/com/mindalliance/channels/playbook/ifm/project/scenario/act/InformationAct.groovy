package com.mindalliance.channels.playbook.ifm.project.scenario.act

import com.mindalliance.channels.playbook.ifm.project.scenario.Occurrence
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:48:26 AM
*/
/* abstract */class InformationAct extends Occurrence {

    Ref agent
    List<Ref> respectedAgreements = [] // list of Agreement
    List<Ref> brokenAgreements = [] // list of Agreement
}