package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.util.NameRange;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * That which has a settable range defined as a string lower bound and upper bound.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2009
 * Time: 3:52:57 PM
 */
public interface NameRangeable {
    /**
     * Set a name range.
     * @param target an ajax request target
     * @param range a name range
     */
    void setNameRange( AjaxRequestTarget target, NameRange range );
}
