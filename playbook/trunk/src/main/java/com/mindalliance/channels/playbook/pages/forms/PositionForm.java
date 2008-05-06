package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceNetworkTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.ResourceLocationTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.position.PositionJurisdictionTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.position.PositionResponsibilitiesTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 10:54:09 AM
 */
public class PositionForm extends AbstractResourceForm {

    public PositionForm(String id, Ref element) {
        super(id, element);
    }


    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Who-what")) {
            public Panel getPanel(String panelId) {
                return new ResourceIdentityTab(panelId, PositionForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Location")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, PositionForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Jurisdiction")) {
             public Panel getPanel(String panelId) {
                 return new PositionJurisdictionTab(panelId, PositionForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Responsibilities")) {
            public Panel getPanel(String panelId) {
                return new PositionResponsibilitiesTab(panelId, PositionForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Network")) {
            public Panel getPanel(String panelId) {
                return new ResourceNetworkTab(panelId, PositionForm.this);
            }
        });
    }
}
