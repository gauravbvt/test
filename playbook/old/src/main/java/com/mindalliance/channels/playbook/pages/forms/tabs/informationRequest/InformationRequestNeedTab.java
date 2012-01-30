package com.mindalliance.channels.playbook.pages.forms.tabs.informationRequest;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationNeedPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:09:39 PM
 */
public class InformationRequestNeedTab extends AbstractFormTab {

    protected InformationNeedPanel infoNeedPanel;

    public InformationRequestNeedTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
         super.load();
        infoNeedPanel = new InformationNeedPanel("informationNeed", this, "informationNeed");
        addReplaceable(infoNeedPanel);
    }

}
