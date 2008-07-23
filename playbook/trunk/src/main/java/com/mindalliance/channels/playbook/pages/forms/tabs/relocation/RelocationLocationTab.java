package com.mindalliance.channels.playbook.pages.forms.tabs.relocation;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 18, 2008
 * Time: 12:55:00 PM
 */
public class RelocationLocationTab  extends AbstractFormTab {

    protected LocationPanel locationPanel;

    public RelocationLocationTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        locationPanel = new LocationPanel("location", this, "location");
        addReplaceable(locationPanel);
    }
}
