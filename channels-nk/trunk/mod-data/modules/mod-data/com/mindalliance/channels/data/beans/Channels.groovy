package com.mindalliance.channels.data.beans

import com.mindalliance.channels.data.BeanList
import com.mindalliance.channels.data.AbstractPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 15, 2008
* Time: 7:51:20 PM
* To change this template use File | Settings | File Templates.
*/
class Channels extends AbstractPersistentBean {

    public static final String GUID = "CHANNELS"

    Environment environment
    BeanList projects = new BeanList(list: 'Project')

// OVERRIDE
    public String getId() {
        return GUID
    }

}