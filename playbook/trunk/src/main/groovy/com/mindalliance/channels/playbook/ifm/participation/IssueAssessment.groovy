package com.mindalliance.channels.playbook.ifm.participation

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:08:32 AM
*/
class IssueAssessment extends ParticipationElement {

    Ref issue
    boolean valid = false // is issue valid?
    String comment = ''
    boolean issueClosed = false

}