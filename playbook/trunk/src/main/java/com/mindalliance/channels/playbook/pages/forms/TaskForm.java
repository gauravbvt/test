package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.task.TaskBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.task.TaskInfoNeedsTab;
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
 * Time: 9:18:46 PM
 */
public class TaskForm extends AbstractInformationActForm {

    public TaskForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new TaskBasicTab(panelId, TaskForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Cause")) {
             public Panel getPanel(String panelId) {
                 return new EventCauseTab(panelId, TaskForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Information needs")) {
             public Panel getPanel(String panelId) {
                 return new TaskInfoNeedsTab(panelId, TaskForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Risk")) {
             public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, TaskForm.this);
             }
         });
    }
}
