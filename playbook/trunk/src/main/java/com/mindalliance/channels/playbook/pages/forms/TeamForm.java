package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.team.TeamDefinitionTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.team.TeamJurisdictionTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.*;
import com.mindalliance.channels.playbook.pages.forms.tabs.position.PositionJurisdictionTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 10:00:06 AM
 */
public class TeamForm extends AbstractResourceForm {

    public TeamForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Members")) {
            public Panel getPanel(String panelId) {
                return new TeamDefinitionTab(panelId, TeamForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Access")) {
            public Panel getPanel(String panelId) {
                return new ResourceAccessTab(panelId, TeamForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Location")) {
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, TeamForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Jurisdiction")) {
             public Panel getPanel(String panelId) {
                 return new TeamJurisdictionTab(panelId, TeamForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Responsibilities")) {
            public Panel getPanel(String panelId) {
                return new ResourceResponsibilitiesTab(panelId, TeamForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Relationships")) {
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, TeamForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Agreements")) {
            public Panel getPanel(String panelId) {
                return new ResourceAgreementsTab(panelId, TeamForm.this);
            }
        });
    }
}
