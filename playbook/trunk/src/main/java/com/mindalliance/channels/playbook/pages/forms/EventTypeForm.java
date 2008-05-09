package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.elementType.ElementTypeBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.eventType.EventTypeTopicsTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 3:51:43 PM
 */
public class EventTypeForm  extends AbstractModelElementForm {

    public EventTypeForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new ElementTypeBasicTab(panelId, EventTypeForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Topics")) {
             public Panel getPanel(String panelId) {
                 return new EventTypeTopicsTab(panelId, EventTypeForm.this);
             }
         });
    }
}
