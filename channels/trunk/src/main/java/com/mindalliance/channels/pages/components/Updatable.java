package com.mindalliance.channels.pages.components;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 25, 2009
 * Time: 3:53:54 PM
 */
public interface Updatable {
    /**
     * A component was changed. An update signal is received.
     * @param target the ajax target
     * @param context an object indicating context of update
     */
    void updateWith( AjaxRequestTarget target, Object context );
}
