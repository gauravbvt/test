package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 3:56:17 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractBeanPropertyValue implements IBeanPropertyValue {


    IPersistentBean contextBean;

    void initContextBean(IPersistentBean bean) {
        contextBean = bean
    }

    // Visitor pattern
     void accept(Closure action) {
         action(this)
     }

     abstract def deepCopy();

}