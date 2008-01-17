package com.mindalliance.channels.nk.channels;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 14, 2008
 * Time: 9:18:48 AM
 */
public interface IPersistentBean extends Serializable {

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

}
