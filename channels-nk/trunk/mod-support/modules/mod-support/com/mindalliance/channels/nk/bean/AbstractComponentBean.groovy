package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 1:45:08 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractComponentBean extends AbstractBean implements IComponentBean {

    IPersistentBean contextBean

    boolean isComponent() {
        return true;
    }

    void initContextBean(IPersistentBean bean)  {
        contextBean = bean
    }

    void accept(Closure action) {
        action(this)
        getBeanProperties().each { propKey, propValue -> propValue.accept(action) }
    }

}