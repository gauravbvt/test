package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 1:45:08 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractComponentBean implements IBean {

    public boolean isComponent() {
        return true;
    }

    public boolean isPersistent() {
        return false;
    }

    IBean deepCopy() {
        IBean copy
        copy = (IBean)clone()
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

    void initContextBean(IPersistentBean bean)  {
        getBeanProperties().each { propKey, propValue ->
            switch(propValue) {
                case IBeanReference: propValue.initContextBean(bean); break;
                case IBeanList: propValue.initContextBean(bean); break;
                case IBean: propValue.initContextBean(bean); break;
                default: break;
            }
        }

    }

}