package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.Describable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 9:24:42 AM
*/
class Event extends BeanImpl implements Describable {

    String description = ''
    List<Ref> eventTypes = []
    Location where = new Location()

}