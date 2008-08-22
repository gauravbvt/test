package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.*;
import com.mindalliance.channels.playbook.pages.forms.tabs.person.PersonIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.position.PositionRolesTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.individual.IndividualJobsTab;
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
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
            public Panel getPanel(String panelId) {
                return new PersonIdentityTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Access")) {
            public Panel getPanel(String panelId) {
                return new ResourceAccessTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Location")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Jobs")) {
            public Panel getPanel(String panelId) {
                return new IndividualJobsTab(panelId, PersonForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Relationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, PersonForm.this);
            }
        });
    }

}
