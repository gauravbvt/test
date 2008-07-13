package com.mindalliance.channels.playbook.pages.forms.tabs.sharingAgreement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.pages.forms.panels.OutgoingSharingProtocolPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 2:01:36 PM
 */
public class SharingAgreementProtocolTab extends AbstractFormTab {

    protected OutgoingSharingProtocolPanel protocolPanel;
    protected TimingPanel maxDelayPanel;

    public SharingAgreementProtocolTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }


    protected void load() {
        super.load();
        protocolPanel = new OutgoingSharingProtocolPanel("protocol", this, "protocol", EDITABLE, feedback);
        addReplaceable(protocolPanel);
        maxDelayPanel = new TimingPanel("maxDelay", this, "maxDelay", EDITABLE, feedback);
        addReplaceable(maxDelayPanel);
    }

}
