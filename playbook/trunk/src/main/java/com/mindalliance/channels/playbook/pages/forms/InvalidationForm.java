package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.invalid.InvalidBasicTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 16, 2008
 * Time: 3:18:00 PM
 */
public class InvalidationForm extends AbstractAnalysisElementForm {

    public InvalidationForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model<String>("Basic")) {
             public Panel getPanel(String panelId) {
                 return new InvalidBasicTab(panelId, InvalidationForm.this);
             }
         });
    }
}
