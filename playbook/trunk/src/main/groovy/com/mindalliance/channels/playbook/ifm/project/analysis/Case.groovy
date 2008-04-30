package com.mindalliance.channels.playbook.ifm.project.analysis

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:44:19 PM
*/
/* abstract */ class Case extends AnalysisElement {

    Ref informationAct
    boolean mayViolate = false
    boolean mayObligate = false

}