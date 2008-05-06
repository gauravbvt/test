package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.modelElement.ModelElementBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.relationshipType.RelationshipTypeSpecsTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 4:40:38 PM
 */
public class RelationshipTypeForm  extends AbstractModelElementForm {

    public RelationshipTypeForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new ModelElementBasicTab(panelId, RelationshipTypeForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Specifications")) {
             public Panel getPanel(String panelId) {
                 return new RelationshipTypeSpecsTab(panelId, RelationshipTypeForm.this);
             }
         });
    }
}
