package com.mindalliance.channels.playbook.ifm.participation

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:09:09 AM
*/
class RecommendationAssessment extends ParticipationElement {

    Ref recommendation
    boolean accepted = false
    String comment = ''
    boolean recommendationExecuted = false
}