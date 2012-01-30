package com.mindalliance.channels.playbook.pages.forms.tabs.sharingAgreement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.SharingConstraintsPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 2:02:02 PM
 */
public class SharingAgreementConstraintsTab extends AbstractFormTab {

    protected SharingConstraintsPanel constraintsPanel;

    public SharingAgreementConstraintsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        constraintsPanel = new SharingConstraintsPanel("constraints", this, "constraints");
        addReplaceable(constraintsPanel);
    }

}
