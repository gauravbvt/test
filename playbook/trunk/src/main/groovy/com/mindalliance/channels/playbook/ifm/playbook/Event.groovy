package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Describable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:24:42 AM
*/
// Something of consequence becomes true somewhere at some point in time
// How it is classified and accounted is the point of views of individual agents
// that they may share with others fully or partially, dispute and confirm
class Event extends Occurrence implements Describable {

    String name = ''
    String description = ''
    Location location = new Location()

}