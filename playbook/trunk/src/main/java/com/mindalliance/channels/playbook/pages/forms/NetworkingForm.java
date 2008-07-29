package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.networking.NetworkingBasicTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 29, 2008
 * Time: 10:55:58 AM
 */
public class NetworkingForm extends AbstractElementForm {

    public NetworkingForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Networking")) {
            public Panel getPanel(String panelId) {
                return new NetworkingBasicTab(panelId, NetworkingForm.this);
            }
        });
    }
}
