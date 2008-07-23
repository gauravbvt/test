package com.mindalliance.channels.playbook.pages.forms.tabs.event;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.CausePanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 1:17:13 PM
 */
public class EventCauseTab extends AbstractFormTab {

    protected CausePanel causePanel;

    public EventCauseTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        causePanel = new CausePanel("cause", this, "cause");
        addReplaceable(causePanel);

    }
}
