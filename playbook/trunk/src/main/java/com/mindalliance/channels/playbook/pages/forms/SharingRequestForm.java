package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.flowAct.FlowActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.sharingRequest.SharingRequestProtocolTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 2:05:18 PM
 */
public class SharingRequestForm extends AbstractInformationActForm {
    
    public SharingRequestForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new FlowActBasicTab(panelId, SharingRequestForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Protocol")) {
             public Panel getPanel(String panelId) {
                 return new SharingRequestProtocolTab(panelId, SharingRequestForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Risk")) {
             public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, SharingRequestForm.this);
             }
         });
    }
}
