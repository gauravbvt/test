package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 14, 2008
 * Time: 8:21:07 PM
 * To change this template use File | Settings | File Templates.
 */

// A List with an attribute naming the class of its items
class BeanList {

    private List list = new ArrayList()
    String itemClass // class name of bean items

    public Iterator iterator() {
        return list.iterator()
    }

    public Object get(String name) {
        (name == 'itemClass') ? this.@itemClass : list."$name"
    }

    public void set(String name, Object value) {
        if (name == 'itemClass')
            this.@itemClass = (String)value
        else
            list."$name" = value
    }

    public Object invokeMethod(String name, Object args) {
        return list.invokeMethod(name, args)
    }

}