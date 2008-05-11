package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel;
import com.mindalliance.channels.playbook.pages.forms.tabs.playbookModel.PlaybookModelAboutTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.playbookModel.PlaybookModelParticipationTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 3:57:34 PM
 */
public class PlaybookModelForm extends AbstractProjectElementForm {

    public PlaybookModelForm(String id, Ref element) {
        super(id, element);
    }

    public PlaybookModel getPlaybookModel() {
        return (PlaybookModel)element.deref();
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("About")) {
        public Panel getPanel(String panelId) {
            return new PlaybookModelAboutTab(panelId, PlaybookModelForm.this);
        }
    });
    tabs.add(new AbstractTab(new Model("Participations")) {
        public Panel getPanel(String panelId) {
            return new PlaybookModelParticipationTab(panelId, PlaybookModelForm.this);
        }
    });
    }
}
