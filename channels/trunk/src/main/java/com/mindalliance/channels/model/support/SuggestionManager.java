// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model.support;

import com.mindalliance.channels.JavaBean;

/**
 * Support for handling suggestions for properties of a java bean.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class SuggestionManager {

    private JavaBean bean;

    /**
     * Default constructor.
     * @param bean the managed bean
     */
    public SuggestionManager( JavaBean bean ) {
        this.bean = bean ;
    }

    /**
     * Return the managed object.
     */
    public final JavaBean getBean() {
        return this.bean;
    }

}
