package com.mindalliance.channels.playbook.pages.forms.tabs.informationAct;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 1:40:33 PM
 */
public class InformationActInfoTab extends AbstractFormTab {

    protected InformationPanel informationPanel;
    private static final long serialVersionUID = -8978642630095376133L;

    public InformationActInfoTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {   // TODO allow setting information from agent's profile (vs. from scratch)
        super.load();
        informationPanel = new InformationPanel("information", this, "information");
        addReplaceable(informationPanel);
    }
}
