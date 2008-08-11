package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.*;
import com.mindalliance.channels.playbook.pages.forms.tabs.organization.OrganizationIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.organization.OrganizationJurisdictionTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 1:08:48 PM
 */
public class OrganizationForm extends AbstractResourceForm {

    public OrganizationForm(String id, Ref element) {
        super(id, element);
    }

    @Override
    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
            public Panel getPanel(String panelId) {
                return new OrganizationIdentityTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Access")) {
            public Panel getPanel(String panelId) {
                return new ResourceAccessTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Location")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Jurisdiction")) {
            public Panel getPanel(String panelId) {
                return new OrganizationJurisdictionTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Responsibilities")) {
            public Panel getPanel(String panelId) {
                return new ResourceResponsibilitiesTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Relationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Agreements")) {
            public Panel getPanel(String panelId) {
                return new ResourceAgreementsTab(panelId, OrganizationForm.this);
            }
        });
    }

}
