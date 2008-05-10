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

    public InformationActInfoTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        informationPanel = new InformationPanel("information", this, "information", EDITABLE, feedback);
        addReplaceable(informationPanel);
    }
}
