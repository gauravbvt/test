package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 14, 2008
 * Time: 8:21:07 PM
 * To change this template use File | Settings | File Templates.
 */

// A List with an attribute naming the class of its items
class BeanList extends AbstractBeanPropertyValue implements IBeanList {

    private List list = new ArrayList()
    String itemClass // class name of bean items

    IBeanList deepCopy() {
        IBeanList copy = new BeanList(itemClass: itemClass)
        list.each {item ->
            copy.add(item.deepCopy())
        }
        return copy
    }

    void accept(Closure action) {
        action(this)
        list.each {item -> item.accept(action)}
    }

    Iterator iterator() {
        return this.@list.iterator()
    }

    Object get(String name) {
        (name == 'itemClass') ? this.@itemClass : this.@list."$name"
    }

    void set(String name, Object value) {
        if (name == 'itemClass')
            this.@itemClass = (String)value
        else
            this.@list."$name" = value
    }

    Object invokeMethod(String name, Object args) {
        return this.@list.invokeMethod(name, args)
    }



}