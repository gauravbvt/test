package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventLocationTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventCauseTab;
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

    private static final long serialVersionUID = 7914823630521873790L;

    public EventForm(String id, Ref element) {
        super(id, element);
    }

    @Override
    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
            private static final long serialVersionUID = 5008540484586918053L;

            @Override
            public Panel getPanel(String panelId) {
                 return new EventBasicTab(panelId, EventForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model<String>("Cause")) {
            private static final long serialVersionUID = 2094012212437657674L;

            @Override
            public Panel getPanel(String panelId) {
                 return new EventCauseTab(panelId, EventForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model<String>("Location")) {
            private static final long serialVersionUID = 4512666886503892719L;

            @Override
            public Panel getPanel(String panelId) {
                 return new EventLocationTab(panelId, EventForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model<String>("Risk")) {
            private static final long serialVersionUID = 2269346597721301307L;

            @Override
            public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, EventForm.this);
             }
         });
    }
}
