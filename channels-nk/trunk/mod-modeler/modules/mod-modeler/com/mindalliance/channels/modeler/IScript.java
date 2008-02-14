package com.mindalliance.channels.modeler;

import com.mindalliance.channels.nk.bean.IPersistentBean;
import com.mindalliance.channels.nk.Action;
import com.mindalliance.channels.c10n.util.IContinuation;
import com.ten60.netkernel.urii.IURAspect;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 14, 2008
 * Time: 1:31:06 PM
 */
public interface IScript {

    IURAspect start(IContinuation c10n, Map args); 

    IURAspect commit(IContinuation c10n, Map args);

    IURAspect abort(IContinuation c10n, Map args);

    IURAspect doStep(String step, IContinuation followUp, Map args);

}
