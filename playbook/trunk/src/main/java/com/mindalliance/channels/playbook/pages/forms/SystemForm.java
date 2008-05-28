package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceLocationTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceResponsibilitiesTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceRelationshipsTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceAgreementsTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.system.SystemIdentityTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 9:55:25 AM
 */
public class SystemForm extends AbstractResourceForm {

    public SystemForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Identity")) {
            public Panel getPanel(String panelId) {
                return new SystemIdentityTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Location")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Responsibilities")) {
            public Panel getPanel(String panelId) {
                return new ResourceResponsibilitiesTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Relationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Agreements")) {
            public Panel getPanel(String panelId) {
                return new ResourceAgreementsTab(panelId, SystemForm.this);
            }
        });
    }
}

