package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.playbook.PlaybookAboutTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.playbook.PlaybookGraphTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 5:06:11 PM
 */
public class PlaybookForm extends AbstractProjectElementForm {

    public PlaybookForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("About")) {
             public Panel getPanel(String panelId) {
                 return new PlaybookAboutTab(panelId, PlaybookForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Graph")) {
             public Panel getPanel(String panelId) {
                 return new PlaybookGraphTab(panelId, PlaybookForm.this);
             }
         });
    }
}
