package com.mindalliance.channels.c10n.util;

import java.util.Map;
import java.util.Date;
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
    String toXml(INKFConvenienceHelper context);
}