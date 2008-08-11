package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.category.CategoryBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.taskType.TaskTypeIntentTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.taskType.TaskTypeInputsTab;
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
public class TaskTypeForm extends AbstractCategoryForm {

    public TaskTypeForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
             public Panel getPanel(String panelId) {
                 return new CategoryBasicTab(panelId, TaskTypeForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model<String>("Intent")) {
             public Panel getPanel(String panelId) {
                 return new TaskTypeIntentTab(panelId, TaskTypeForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model<String>("Inputs")) {
             public Panel getPanel(String panelId) {
                 return new TaskTypeInputsTab(panelId, TaskTypeForm.this);
             }
         });
    }
}
