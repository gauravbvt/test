package com.mindalliance.channels.nk.bean;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 23, 2008
 * Time: 11:00:24 AM
 */
// Marker interface
public interface IBeanReference  extends Cloneable, IBeanPropertyValue {

    public IBeanReference deepCopy();

    public void initContextBean(IPersistentBean bean);

    public boolean isDomainBound();

    public boolean isOwned();

}
