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

}