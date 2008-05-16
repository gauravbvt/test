package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceLocationTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceRelationshipsTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceAgreementsTab;
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
        tabs.add(new AbstractTab(new Model("Who-what")) {
            public Panel getPanel(String panelId) {
                return new PersonIdentityTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Location")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Responsibilities")) {
            public Panel getPanel(String panelId) {
                return new PersonResponsibilitiesTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Realationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Realationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceAgreementsTab(panelId, PersonForm.this);
            }
        });
    }

}
