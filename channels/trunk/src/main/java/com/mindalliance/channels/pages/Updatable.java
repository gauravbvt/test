package com.mindalliance.channels.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.command.Change;

/**
 * An updatable page or component.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 25, 2009
 * Time: 3:53:54 PM
 */
public interface Updatable {

    /**
     * An identifiable object  was changed; change state accordingly.
     * This is in anticipation of receiving an "updateWith" message.
     *
     * @param change       the nature of the change
     */
    void changed( Change change );

    /**
     * An identifiable object  was changed; update UI components.
     * Always follows a corresponding "changed" message.
     *
     * @param target       the ajax target
     * @param change       the nature of the change
     */
    void updateWith( AjaxRequestTarget target, Change change );
}
