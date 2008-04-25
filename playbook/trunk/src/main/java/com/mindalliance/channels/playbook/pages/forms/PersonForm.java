package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceLocalityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceNetworkTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.person.PersonIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.person.PersonResponsibilitiesTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 3:20:23 PM
 */
public class PersonForm extends AbstractResourceForm {

    public PersonForm(String id, Ref element) {
        super(id, element);
    }

    @Override
    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Who|what")) {
            public Panel getPanel(String panelId) {
                return new PersonIdentityTab(panelId, element);
            }
        });
        tabs.add(new AbstractTab(new Model("Where")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocalityTab(panelId, element);
            }
        });
        tabs.add(new AbstractTab(new Model("Responsibilities")) {
            public Panel getPanel(String panelId) {
                return new PersonResponsibilitiesTab(panelId, element);
            }
        });
        tabs.add(new AbstractTab(new Model("Network")) {
            public Panel getPanel(String panelId) {
                return new ResourceNetworkTab(panelId, element);
            }
        });
    }

}
