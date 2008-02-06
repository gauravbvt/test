package com.mindalliance.channels.nk.bean

import com.mindalliance.channels.nk.bean.IPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 16, 2008
* Time: 1:12:16 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractPersistentBean extends AbstractBean implements IPersistentBean {

    String db
    String id
    Date createdOn = new Date()
    String version
    private boolean rooted = false

    private static final String DEFAULT_VERSION = "1.0.0"

    boolean isPersistent() {
        return true
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

    // TODO - implement deepCopy using visitor
    IBean deepCopy() {
        IPersistentBean copy = (IPersistentBean) super.deepCopy()
        copy.db = db
        copy.id = id
        copy.createdOn = new Date(createdOn.toString())
        copy.setRooted(isRooted())
        copy.version = version
        return copy
    }


    // Make the bean ready for use
    void activate() {
        initialize()
        getBeanProperties().each {key, val ->
            String xpath = '/'
            val.accept([propName:key, parentPath:xpath], { propKey, propPath, propValue ->
                    propValue.initContextBean(this)
                    propValue.initMetadata(propKey, propPath, this.defaultMetadata)
                 })
        }
    }

}