package com.mindalliance.channels.nk.bean;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 23, 2008
 * Time: 10:59:56 AM
 */
// Marker interface
public interface IBeanList extends Cloneable, IBeanPropertyValue {

    public IBeanList deepCopy();

    public IBeanPropertyValue getItemPrototype();

    public void initContextBean(IPersistentBean bean);

    public void addItem(Object initializer);

}
