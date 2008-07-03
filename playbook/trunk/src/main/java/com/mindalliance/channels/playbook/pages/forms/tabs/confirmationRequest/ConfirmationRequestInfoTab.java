package com.mindalliance.channels.playbook.pages.forms.tabs.confirmationRequest;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActInfoTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.AgentSpecificationPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 13, 2008
 * Time: 9:48:54 AM
 */
public class ConfirmationRequestInfoTab extends InformationActInfoTab {

    protected AgentSpecificationPanel agentSpecPanel;

    public ConfirmationRequestInfoTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        agentSpecPanel = new AgentSpecificationPanel("agentSpec", this, "sourceSpec", EDITABLE, feedback);
        addReplaceable(agentSpecPanel);
    }
}
