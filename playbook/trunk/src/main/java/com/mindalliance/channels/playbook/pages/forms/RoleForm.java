package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.elementType.ElementTypeBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.role.RoleResponsibilitiesTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 7:06:43 PM
 */
public class RoleForm  extends AbstractCategoryForm {

    public RoleForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new ElementTypeBasicTab(panelId, RoleForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Responsibilities")) {
              public Panel getPanel(String panelId) {
                  return new RoleResponsibilitiesTab(panelId, RoleForm.this);
              }
          });
    }
}
