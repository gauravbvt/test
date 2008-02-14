package com.mindalliance.channels.modeler

import com.ten60.netkernel.urii.IURAspect
import com.mindalliance.channels.c10n.util.IContinuation

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 14, 2008
* Time: 4:48:15 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractScript implements IScript {

    abstract IURAspect abort(IContinuation c10n, Map args);

    abstract IURAspect commit(IContinuation c10n, Map args);

    abstract IURAspect start(IContinuation c10n, Map args);

    public IURAspect doStep(String step, IContinuation followUp, Map args) {
        return this."$step"(followUp, args)
    }

}