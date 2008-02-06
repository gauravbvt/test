package com.mindalliance.channels.nk.bean;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 5, 2008
 * Time: 9:09:40 PM
 */
public interface IBeanDomain {

    String getId();
    String getDb();
    Map getArgs();
    String getQuery();
}
