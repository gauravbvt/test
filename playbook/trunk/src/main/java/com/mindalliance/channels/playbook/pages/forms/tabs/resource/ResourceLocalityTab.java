package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 8:41:45 AM
 */
public class ResourceLocalityTab extends AbstractFormTab {

    protected LocationPanel locationPanel;

    public ResourceLocalityTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        locationPanel = new LocationPanel("location", element, "location");
        add(locationPanel);
    }
}
