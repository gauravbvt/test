package com.mindalliance.channels.playbook.pages.forms.tabs.agreement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationTemplatePanel;
import com.mindalliance.channels.playbook.ref.Ref;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 2:06:38 PM
 */
public class AgreementInfoTab extends AbstractFormTab {

    InformationTemplatePanel infoTemplatePanel;

    public AgreementInfoTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        infoTemplatePanel = new InformationTemplatePanel("informationTemplate", element, "informationCovered", EDITABLE, feedback);
        addReplaceable(infoTemplatePanel);
    }
}
