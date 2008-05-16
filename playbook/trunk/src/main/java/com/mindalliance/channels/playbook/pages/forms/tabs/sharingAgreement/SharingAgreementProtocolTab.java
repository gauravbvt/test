package com.mindalliance.channels.playbook.pages.forms.tabs.sharingAgreement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.SharingProtocolPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 2:01:36 PM
 */
public class SharingAgreementProtocolTab extends AbstractFormTab {

    protected SharingProtocolPanel protocolPanel;

    public SharingAgreementProtocolTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }


    protected void load() {
        super.load();
        protocolPanel = new SharingProtocolPanel("protocol", this, "protocol", EDITABLE, feedback);
        addReplaceable(protocolPanel);
    }

}
