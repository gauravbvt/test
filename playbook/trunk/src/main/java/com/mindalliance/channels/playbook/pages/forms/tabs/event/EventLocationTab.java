package com.mindalliance.channels.playbook.pages.forms.tabs.event;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 11:41:23 AM
 */
public class EventLocationTab extends AbstractFormTab {

    protected LocationPanel locationPanel;

    public EventLocationTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        locationPanel = new LocationPanel("location", this, "location");
        addReplaceable(locationPanel);
    }
}
