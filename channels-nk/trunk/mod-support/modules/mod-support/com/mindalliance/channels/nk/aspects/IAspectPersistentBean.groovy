package com.mindalliance.channels.nk.aspects

import com.mindalliance.channels.nk.bean.IPersistentBean
import com.ten60.netkernel.urii.IURAspect
import com.mindalliance.channels.nk.bean.IPersistentBean

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