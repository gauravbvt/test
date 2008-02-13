package com.mindalliance.channels.c10n.aspects

import com.mindalliance.channels.c10n.util.IContinuation

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 11, 2008
* Time: 7:39:19 PM
* To change this template use File | Settings | File Templates.
*/
class ContinuationAspect implements IAspectContinuation {

    IContinuation continuation

    ContinuationAspect (IContinuation continuation) {
        this.continuation = continuation
    }

    IContinuation getContinuation() {
        return continuation
    }

}