package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.group.GroupWhoTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.group.GroupWhereTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.group.GroupLinksTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 4:04:55 PM
 */
public class GroupForm extends AbstractPlaybookElementForm {

    public GroupForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Who")) {
             public Panel getPanel(String panelId) {
                 return new GroupWhoTab(panelId, GroupForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Where")) {
             public Panel getPanel(String panelId) {
                 return new GroupWhereTab(panelId, GroupForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Links")) {
             public Panel getPanel(String panelId) {
                 return new GroupLinksTab(panelId, GroupForm.this);
             }
         });
    }
}
