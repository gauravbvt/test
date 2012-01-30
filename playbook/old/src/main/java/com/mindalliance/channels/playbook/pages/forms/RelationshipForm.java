package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.relationship.RelationshipBasicTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 20, 2008
 * Time: 11:58:34 AM
 */
public class RelationshipForm extends AbstractProjectElementForm {

    public RelationshipForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
            public Panel getPanel(String panelId) {
                return new RelationshipBasicTab(panelId, RelationshipForm.this);
            }
        });
    }
}
