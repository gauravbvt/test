package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.Identified

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:41:33 PM
*/
class AnalysisElement implements Identified {

    String id
    String rationale = ''

    AnalysisElement() {
       id = "${UUID.randomUUID()}"
    }


}