package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventLocationTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventBasicTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 11:38:34 AM
 */
public class EventForm extends AbstractPlaybookElementForm {

    public EventForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new EventBasicTab(panelId, EventForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new EventLocationTab(panelId, EventForm.this);
             }
         });
    }
}
