package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceLocationTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceResponsibilitiesTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceRelationshipsTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceAgreementsTab;
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
        tabs.add(new AbstractTab(new Model("Who-what")) {
            public Panel getPanel(String panelId) {
                return new OrganizationIdentityTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Location")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Jurisdiction")) {
            public Panel getPanel(String panelId) {
                return new OrganizationJurisdictionTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Responsibilities")) {
            public Panel getPanel(String panelId) {
                return new ResourceResponsibilitiesTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Relationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, OrganizationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Agreements")) {
            public Panel getPanel(String panelId) {
                return new ResourceAgreementsTab(panelId, OrganizationForm.this);
            }
        });
    }

}
