package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.association.AssociationRelationshipTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActCauseTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 9:16:17 PM
 */
public class AssociationForm extends AbstractInformationActForm {

    public AssociationForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new InformationActBasicTab(panelId, AssociationForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Relationship")) {
             public Panel getPanel(String panelId) {
                 return new AssociationRelationshipTab(panelId, AssociationForm.this);
             }
         });
         tabs.add(new AbstractTab(new Model("Cause")) {
              public Panel getPanel(String panelId) {
                  return new InformationActCauseTab(panelId, AssociationForm.this);
              }
          });
        tabs.add(new AbstractTab(new Model("Risk")) {
             public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, AssociationForm.this);
             }
         });
    }
}
