package com.mindalliance.channels.nk.aspects

import com.mindalliance.channels.nk.IPersistentBean
import com.ten60.netkernel.urii.IURAspect

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 8:50:29 PM
* To change this template use File | Settings | File Templates.
*/
interface IAspectPersistentBean  extends IURAspect {

    IPersistentBean getPersistentBean()

}