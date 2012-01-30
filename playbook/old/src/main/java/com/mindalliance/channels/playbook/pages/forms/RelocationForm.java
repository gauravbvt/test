package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventCauseTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.relocation.RelocationLocationTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 18, 2008
 * Time: 12:52:46 PM
 */
public class RelocationForm  extends AbstractInformationActForm {

    public RelocationForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
            public Panel getPanel(String panelId) {
                return new InformationActBasicTab(panelId, RelocationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Cause")) {
             public Panel getPanel(String panelId) {
                 return new EventCauseTab(panelId, RelocationForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Location")) {
            public Panel getPanel(String panelId) {
                return new RelocationLocationTab(panelId, RelocationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Risk")) {
             public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, RelocationForm.this);
             }
         });
    }
}

