package com.mindalliance.channels.modeler.accessors

import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 13, 2008
* Time: 2:30:03 PM
* To change this template use File | Settings | File Templates.
*/
class ActionAccessor extends AbstractAccessor {

    // Manages action and continuation requests
    // operator: getActions|start|continue|commit|abort
    // other args depend on operator
    // return depends on operator
    void source(Context context) {
         String operator = context.operator
         switch (operator) {
             case 'getActions': getActions(context); break
             case 'start': startAction(context); break
             case 'continue': continueAction(context); break
             case 'abort': abortAction(context); break
             case 'commit': commitAction(context); break
             default: throw new IllegalArgumentException("Invalid operator $operator")
         }
    }
    // Returns scripted actions available on selected persistent bean
    // beanId: a bean's id
    // beanDB: the bean's db name
    void getActions(Context context) {
       throw new Exception("Not implemented yet")
    }
    // Starts scripted action (that makes transient state changes)
    // script: the name of the script
    // action: the name of the action (that parameterizes the script)
    // beanId: a bean's id
    // beanDB: the bean's db name
    // session: the session id
    // param: posted arguments
    void startAction(Context context) {
       throw new Exception("Not implemented yet")
    }
    // Continues scripted action (picking up transient state changes)
    // continuation: continuation id
    // step: step name
    // session: the session id
    // param: posted params
    void continueAction(Context context) {
        throw new Exception("Not implemented yet")
    }
    // Terminates scripted action, abandoning transient state changes
    // continuation: continuation id
    // step: step name
    // session: the session id
    // param: posted params
    void abortAction(Context context) {
        throw new Exception("Not implemented yet")
    }
    // Terminates scripted action, committing transient state changes
    // continuation: continuation id
    // session: the session id
    // param: posted params
    void commitAction(Context context) {
        throw new Exception("Not implemented yet")
    }


}