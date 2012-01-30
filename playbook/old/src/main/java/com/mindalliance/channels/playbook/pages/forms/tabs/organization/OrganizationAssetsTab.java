package com.mindalliance.channels.playbook.pages.forms.tabs.organization;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.RefListPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 27, 2008
 * Time: 3:53:10 PM
 */
public class OrganizationAssetsTab extends AbstractFormTab {

    private RefListPanel positionsPanel;
    private RefListPanel systemsPanel;

    public OrganizationAssetsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        positionsPanel = new RefListPanel("positions", this, new RefPropertyModel(getElement(), "positions"));
        addReplaceable(positionsPanel);
        systemsPanel = new RefListPanel("systems", this, new RefPropertyModel(getElement(), "systems"));
        addReplaceable(systemsPanel);
    }
}
