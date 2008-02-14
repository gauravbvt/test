package com.mindalliance.channels.modeler

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 14, 2008
 * Time: 1:38:24 PM
 * To change this template use File | Settings | File Templates.
 */
class ScriptRegistry {

    Context context
    static private final String SCRIPTS_PATH = 'com.mindalliance.channels.modeler.scripts'

    ScriptRegistry(Context context) {
        this.context = context
    }

    // For now just instantiate eponymous script class. We'll get fancy later if needed.
    IScript getStcript(String name) {
        IScript script
        script = (IScript)Class.forName("$SCRIPTS_PATH.name").newInstance(context)
        return script
    }

}