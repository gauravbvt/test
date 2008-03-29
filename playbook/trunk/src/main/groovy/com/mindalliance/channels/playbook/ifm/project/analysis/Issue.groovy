package com.mindalliance.channels.playbook.ifm.project.analysis

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Level

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:07:17 AM
*/
class Issue extends AnalysisElement {

    Ref about // some element
    String description = ''
    String explanation = ''
    Level severity = Level.LEVEL_NONE
}