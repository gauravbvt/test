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

    private static final long serialVersionUID = -5161450765013774592L;

    public EventLocationTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    @Override
    protected void load() {
        super.load();
        addReplaceable( new LocationPanel( "location", this, "location" ) );
    }
}
