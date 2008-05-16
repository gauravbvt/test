package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.flowAct.FlowActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActInfoTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActCauseTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 3:12:26 PM
 */
public class VerificationForm extends AbstractInformationActForm {

    public VerificationForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
            public Panel getPanel(String panelId) {
                return new FlowActBasicTab(panelId, VerificationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Cause")) {
             public Panel getPanel(String panelId) {
                 return new InformationActCauseTab(panelId, VerificationForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Information")) {
            public Panel getPanel(String panelId) {
                return new InformationActInfoTab(panelId, VerificationForm.this);
            }
        });
    }
}

