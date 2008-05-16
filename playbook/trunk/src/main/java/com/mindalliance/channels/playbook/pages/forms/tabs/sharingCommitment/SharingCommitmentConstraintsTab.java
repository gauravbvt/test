package com.mindalliance.channels.playbook.pages.forms.tabs.sharingCommitment;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.SharingConstraintsPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 2:26:49 PM
 */
public class SharingCommitmentConstraintsTab  extends AbstractFormTab {

    SharingConstraintsPanel constraintsPanel;

    public SharingCommitmentConstraintsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        constraintsPanel = new SharingConstraintsPanel("constraints", this, "constraints", EDITABLE, feedback);
        addReplaceable(constraintsPanel);
    }
}
