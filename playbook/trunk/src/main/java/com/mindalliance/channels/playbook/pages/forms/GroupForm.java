package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.group.GroupWhoTab;
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

    private static final long serialVersionUID = 4220328369697624789L;

    public GroupForm(String id, Ref element) {
        super(id, element);
    }

    @Override
    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Who")) {
            private static final long serialVersionUID = 8209308066914876871L;

            @Override
            public Panel getPanel(String panelId) {
                 return new GroupWhoTab(panelId, GroupForm.this);
             }
         });
    }
}
