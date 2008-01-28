package com.mindalliance.channels.nk.bean;

import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 14, 2008
 * Time: 9:18:48 AM
 */
public interface IPersistentBean extends IBean {

    // Must implement a constructor with args (String db, String id)

    public String getVersion();

    public Date getCreatedOn();

    // Bean unique identifier
    public String getId();

    // Store unique identifier
    public String getDb();

    // Is a root bean
    public boolean isRooted();

    // public IPersistentBean migrateTo(String version)

    // Make the bean ready for use
    public void activate();

}
