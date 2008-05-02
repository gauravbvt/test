package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 1, 2008
 * Time: 9:43:43 PM
 */
public interface ElementPanel {

    Ref getElement();
    void elementChanged(String propPath, AjaxRequestTarget target);
    void addOtherElement(Ref otherElement);
}
