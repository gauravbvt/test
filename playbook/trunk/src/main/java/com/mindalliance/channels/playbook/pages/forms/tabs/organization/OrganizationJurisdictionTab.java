package com.mindalliance.channels.playbook.pages.forms.tabs.organization;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 1:13:33 PM
 */
public class OrganizationJurisdictionTab  extends AbstractFormTab {

    protected LocationPanel jurisdictionPanel;

    public OrganizationJurisdictionTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        jurisdictionPanel = new LocationPanel("jurisdiction", this, "jurisdiction", false, feedback);
        addReplaceable(jurisdictionPanel);
    }
}
