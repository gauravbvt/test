package com.mindalliance.channels.nk.bean

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

    IBean deepCopy() {
        IPersistentBean copy
        copy = (IPersistentBean)clone()
        copy.db = db
        copy.id = id
        copy.createdOn = new Date(createdOn.toString())
        copy.setRooted(isRooted())
        copy.version = version
        getBeanProperties().each { propKey, propValue ->
            switch(propValue) {
                case IBeanReference:  this."$propKey" = propValue.deepCopy(); break;
                case IBeanList: this."$propKey" = propValue.deepCopy(); break;
                case IBean: this."$propKey" = propValue.deepCopy(); break;
                default: this."$propKey" = propValue;   // TODO - clone this?
            }
        }
        return copy
    }

    // Make the bean ready for use
    void activate() {
        getBeanProperties().each { propKey, propValue ->
            switch(propValue) {
                case IBeanReference: propValue.initContextBean(this); break;
                case IBeanList: propValue.initContextBean(this); break;
                case IBean: propValue.initContextBean(this); break;
                default: break;  
            }
        }
    }

    void initContextBean(IPersistentBean bean) {
        contextBean = bean
    }

}