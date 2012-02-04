package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActInfoTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventCauseTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationTransfer.InformationTransferMediaTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.flowAct.FlowActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:52:16 PM
 */
public class InformationTransferForm extends AbstractInformationActForm {

    public InformationTransferForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
            public Panel getPanel(String panelId) {
                return new FlowActBasicTab(panelId, InformationTransferForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Cause")) {
             public Panel getPanel(String panelId) {
                 return new EventCauseTab(panelId, InformationTransferForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model<String>("Information")) {
            public Panel getPanel(String panelId) {
                return new InformationActInfoTab(panelId, InformationTransferForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Medium")) {
            public Panel getPanel(String panelId) {
                return new InformationTransferMediaTab(panelId, InformationTransferForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Risk")) {
             public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, InformationTransferForm.this);
             }
         });
    }
}