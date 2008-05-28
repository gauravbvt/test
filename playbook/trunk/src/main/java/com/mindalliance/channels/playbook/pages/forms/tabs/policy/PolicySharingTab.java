package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationSpecPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:27:17 PM
 */
public class PolicySharingTab extends AbstractFormTab {

    protected InformationSpecPanel infoSpecPanel;

    public PolicySharingTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        infoSpecPanel = new InformationSpecPanel("informationSpec", this, "informationSpec", EDITABLE, feedback);
        addReplaceable(infoSpecPanel);
    }


}