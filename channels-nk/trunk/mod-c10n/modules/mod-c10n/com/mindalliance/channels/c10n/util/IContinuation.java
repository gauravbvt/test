package com.mindalliance.channels.c10n.util;

import java.util.Map;
import java.util.Date;
import java.util.List;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 11, 2008
 * Time: 4:36:04 PM
 */
public interface IContinuation {

    String getId();
    Date getDate();
    Map getState();
    boolean isAborted();
    boolean isCommitted();
    String getPrevious();   // Id of continuation that lead to this one
    List getFollowUps(); // Ids of follow-up continuations (created from this one)
    String toXml(INKFConvenienceHelper context);
}