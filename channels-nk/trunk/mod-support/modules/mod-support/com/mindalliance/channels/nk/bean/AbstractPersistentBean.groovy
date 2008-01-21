package com.mindalliance.channels.nk.bean

import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.bean.IPersistentBean

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
    private boolean rooted = false

    private static final String DEFAULT_VERSION = "1.0.0"

    boolean isPersistent() {
        return true
    }

    boolean isComponent() {
        return false
    }

    // Default
    String getVersion() {
        return version ?: DEFAULT_VERSION
    }

    boolean isRooted() {
        return rooted
    }

    boolean setRooted(boolean val) {
        rooted = val
    }

}