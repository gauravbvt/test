package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.*;
import com.mindalliance.channels.playbook.pages.forms.tabs.position.PositionJurisdictionTab;
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
        tabs.add(new AbstractTab(new Model("Basic")) {
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
                return new ResourceResponsibilitiesTab(panelId, PositionForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Relationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, PositionForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Agreements")) {
            public Panel getPanel(String panelId) {
                return new ResourceAgreementsTab(panelId, PositionForm.this);
            }
        });
    }
}
