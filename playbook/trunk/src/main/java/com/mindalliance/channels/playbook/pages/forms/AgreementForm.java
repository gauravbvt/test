package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.agreement.AgreementAboutTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.agreement.AgreementInfoTab;
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
public class AgreementForm extends AbstractElementForm {

    public AgreementForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("About")) {
            public Panel getPanel(String panelId) {
               return new AgreementAboutTab(panelId, element);
            }
        });        
        tabs.add(new AbstractTab(new Model("About")) {
            public Panel getPanel(String panelId) {
               return new AgreementInfoTab(panelId, element);
            }
        });
    }
}
