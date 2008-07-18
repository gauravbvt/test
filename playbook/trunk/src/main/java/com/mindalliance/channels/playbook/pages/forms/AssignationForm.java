package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.flowAct.FlowActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.assignation.AssignationResponsibilityTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventCauseTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 8:33:54 PM
 */
public class AssignationForm  extends AbstractInformationActForm {

    public AssignationForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
            public Panel getPanel(String panelId) {
                return new FlowActBasicTab(panelId, AssignationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Cause")) {
             public Panel getPanel(String panelId) {
                 return new EventCauseTab(panelId, AssignationForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Responsibility")) {
            public Panel getPanel(String panelId) {
                return new AssignationResponsibilityTab(panelId, AssignationForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Risk")) {
             public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, AssignationForm.this);
             }
         });
    }
}
