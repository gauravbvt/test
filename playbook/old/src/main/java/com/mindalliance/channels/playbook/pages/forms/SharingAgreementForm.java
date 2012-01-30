package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.sharingAgreement.*;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 1:58:59 PM
 */
public class SharingAgreementForm extends AbstractProjectElementForm {

    public SharingAgreementForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Parties")) {
            public Panel getPanel(String panelId) {
               return new SharingAgreementPartiesTab(panelId, SharingAgreementForm.this);
            }
        });        
        tabs.add(new AbstractTab(new Model("Protocol")) {
            public Panel getPanel(String panelId) {
               return new SharingAgreementProtocolTab(panelId, SharingAgreementForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Restrictions")) {
            public Panel getPanel(String panelId) {
               return new SharingAgreementConstraintsTab(panelId, SharingAgreementForm.this);
            }
        });
    }
}
