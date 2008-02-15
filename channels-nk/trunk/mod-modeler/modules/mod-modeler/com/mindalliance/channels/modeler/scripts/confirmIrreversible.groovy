package com.mindalliance.channels.modeler.scripts

import com.mindalliance.channels.modeler.AbstractScript
import com.ten60.netkernel.urii.IURAspect
import com.mindalliance.channels.c10n.util.IContinuation
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 14, 2008
* Time: 8:46:55 PM
* To change this template use File | Settings | File Templates.
*/
class confirmIrreversible  extends AbstractScript {


    IURAspect start(IContinuation c10n, Map args, INKFConvenienceHelper context) {
       throw new Exception("Not implemented yet")
    }

    IURAspect abort(IContinuation c10n, Map args, INKFConvenienceHelper context) {
       throw new Exception("Not implemented yet")
    }

    IURAspect commit(IContinuation c10n, Map args, INKFConvenienceHelper context) {
       throw new Exception("Not implemented yet")
    }



}