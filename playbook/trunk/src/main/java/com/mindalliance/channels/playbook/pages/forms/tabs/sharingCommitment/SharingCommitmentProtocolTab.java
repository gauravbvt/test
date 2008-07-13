package com.mindalliance.channels.playbook.pages.forms.tabs.sharingCommitment;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.OutgoingSharingProtocolPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 2:26:23 PM
 */
public class SharingCommitmentProtocolTab  extends AbstractFormTab {

    OutgoingSharingProtocolPanel protocolPanel;

    public SharingCommitmentProtocolTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }
        
    protected void load() {
        super.load();
        protocolPanel = new OutgoingSharingProtocolPanel("protocol", this, "protocol", EDITABLE, feedback);
        addReplaceable(protocolPanel);
    }

}
