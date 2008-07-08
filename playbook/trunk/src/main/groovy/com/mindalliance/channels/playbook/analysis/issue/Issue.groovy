package com.mindalliance.channels.playbook.analysis.issue

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.analysis.AnalysisElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:46:30 PM
*/
class Issue extends AnalysisElement {

    Ref element
    Ref issueType
    String recommendation = ''

}