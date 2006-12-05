// Copyright (C) 2006 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.cap.remoting;

/**
 * A local JavaBean initially cloned from a RemoteJavaBean.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public interface CopiedJavaBean extends JavaBean {

    /**
     * Return a proxy to the original object on which this copy is based.
     * @return the proxy on the original object
     */
    RemoteJavaBean getOriginal();

}
