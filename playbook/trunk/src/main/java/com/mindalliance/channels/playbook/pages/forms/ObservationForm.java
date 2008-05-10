package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActInfoTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 1:36:56 PM
 */
public class ObservationForm extends AbstractInformationActForm {

    public ObservationForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
            public Panel getPanel(String panelId) {
                return new InformationActBasicTab(panelId, ObservationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Information")) {
            public Panel getPanel(String panelId) {
                return new InformationActInfoTab(panelId, ObservationForm.this);
            }
        });
    }
}
