package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.place.PlaceAboutTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.place.PlaceLocationTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 7:35:44 PM
 */
public class PlaceForm extends AbstractProjectElementForm {

    public PlaceForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("About")) {
             public Panel getPanel(String panelId) {
                 return new PlaceAboutTab(panelId, PlaceForm.this);
             }
         });
         tabs.add(new AbstractTab(new Model("Location")) {
             public Panel getPanel(String panelId) {
                 return new PlaceLocationTab(panelId, PlaceForm.this);
             }
         });
    }
}
