package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.elementType.ElementTypeBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.taskType.TaskTypeIntentTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.taskType.TaskTypeInfoNeedsTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 8:13:22 PM
 */
public class TaskTypeForm extends AbstractModelElementForm {

    public TaskTypeForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new ElementTypeBasicTab(panelId, TaskTypeForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Intent")) {
             public Panel getPanel(String panelId) {
                 return new TaskTypeIntentTab(panelId, TaskTypeForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Information needs")) {
             public Panel getPanel(String panelId) {
                 return new TaskTypeInfoNeedsTab(panelId, TaskTypeForm.this);
             }
         });
    }
}
