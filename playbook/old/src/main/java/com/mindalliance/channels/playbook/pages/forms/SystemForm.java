package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.resource.*;
import com.mindalliance.channels.playbook.pages.forms.tabs.system.SystemIdentityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.position.PositionRolesTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.individual.IndividualJobsTab;
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

    private static final long serialVersionUID = 8067319437147455996L;

    public SystemForm(String id, Ref element) {
        super(id, element);
    }

    @Override
    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
            private static final long serialVersionUID = -6540476106153770917L;

            @Override
            public Panel getPanel(String panelId) {
                return new SystemIdentityTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Access")) {
            private static final long serialVersionUID = 828826989096318176L;

            @Override
            public Panel getPanel(String panelId) {
                return new ResourceAccessTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Location")) {
            private static final long serialVersionUID = -8188670975795388813L;

            @Override
            public Panel getPanel(String panelId) {
                return new ResourceLocationTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Jobs")) {
            public Panel getPanel(String panelId) {
                return new IndividualJobsTab(panelId, SystemForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model<String>("Relationships")) {
            private static final long serialVersionUID = -2951722505083449747L;

            @Override
            public Panel getPanel(String panelId) {
                return new ResourceRelationshipsTab(panelId, SystemForm.this);
            }
        });
    }
}

