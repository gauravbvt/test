package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Location
import com.mindalliance.channels.playbook.ifm.context.model.Responsibility

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:05:53 AM
*/
// Assignment of a Responsibility to any agent with a given role, possibly within a given type of organization
// and possiblylimited to some location
class Assignment extends InfoElement {

    Ref role // required
    Ref organizationType
    Location location = new Location()
    Responsibility responsibility // required
}