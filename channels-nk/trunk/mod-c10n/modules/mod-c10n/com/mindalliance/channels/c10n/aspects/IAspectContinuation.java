package com.mindalliance.channels.c10n.aspects;

import com.mindalliance.channels.c10n.util.IContinuation;
import com.ten60.netkernel.urii.IURAspect;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 11, 2008
 * Time: 7:38:53 PM
 */
public interface IAspectContinuation  extends IURAspect {

    IContinuation getContinuation();
}