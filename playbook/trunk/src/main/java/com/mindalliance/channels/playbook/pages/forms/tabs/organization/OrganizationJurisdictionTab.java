package com.mindalliance.channels.playbook.pages.forms.tabs.organization;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.LocationPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.ifm.project.resources.Organization;
import com.mindalliance.channels.playbook.ifm.info.Location;

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
        Organization organization = (Organization)getElement().deref();
        if (organization.getJurisdiction() == null) {
            organization.setJurisdiction(new Location());
        }
        jurisdictionPanel = new LocationPanel("jurisdiction", this, "jurisdiction", false, feedback);
        addReplaceable(jurisdictionPanel);
    }
}
