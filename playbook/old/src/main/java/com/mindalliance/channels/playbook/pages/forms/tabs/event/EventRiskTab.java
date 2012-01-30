package com.mindalliance.channels.playbook.pages.forms.tabs.event;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.RiskPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 17, 2008
 * Time: 10:25:19 PM
 */
public class EventRiskTab extends AbstractFormTab {

    protected RiskPanel riskPanel;

    public EventRiskTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }
    
    protected void load() {
        super.load();
        riskPanel = new RiskPanel("risk", this, "risk");
        addReplaceable(riskPanel);
    }
}
