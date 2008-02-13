package com.mindalliance.channels.nk.bean;

import com.mindalliance.channels.nk.Action;

import java.util.Date;
import java.util.List;
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

    // Get list of actions that can be taken on the persistent bean
    public List getActions();

    // Execute an action given arguments
    public void executeAction(Action action, Map args);

    public boolean isWritable();

    public boolean isDeletable();

}
