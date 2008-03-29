package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ifm.Level

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:05:28 AM
*/
class InformationNeed extends InfoElement {

    InformationSpecification informationSpecification
    Level criticality = Level.LEVEL_HIGH
    
}