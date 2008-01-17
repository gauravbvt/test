package com.mindalliance.channels.data

import com.mindalliance.channels.nk.channels.IPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 16, 2008
* Time: 1:12:16 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractPersistentBean implements IPersistentBean {

    String db
    String id
    Date createdOn = new Date()
    String version
    private boolean rooted

    private static final String DEFAULT_VERSION = "1.0.0"

    // Default
    String getVersion() {
        return version ?: DEFAULT_VERSION
    }

    boolean isRooted() {
        return rooted
    }

    void setRooted(boolean val)  {
       rooted = val
    }

    // Intercept access to a property containing a BeanReference (dereference it if possible)
    def getProperty(String name) {
        def value = this.@"$name" // access field directly
        if (value instanceof BeanReference) {
            def beanReference = value
            IPersistentBean bean = beanReference.dereference()
            return bean
        }
        else {
            return value
        }
    }

    static IPersistentBean newPersistentBean(String db, String id, String beanClass) {
        return (IPersistentBean)Eval.me("${beanClass}.newInstance(\'${db}\', \'${id}\')")
    }



}