package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.project.ProjectAboutTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.project.ProjectParticipationTab;
import com.mindalliance.channels.playbook.ifm.project.Project;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 1:08:29 PM
 */
public class ProjectForm extends AbstractProjectElementForm {

    public ProjectForm(String id, Ref element) {
        super(id, element);
    }

    @Override
    public Project getProject() {
        return (Project)element.deref();
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("About")) {
            public Panel getPanel(String panelId) {
                return new ProjectAboutTab(panelId, ProjectForm.this);
            }
        });
        tabs.add(new AbstractTab(new Model("Participations")) {
            public Panel getPanel(String panelId) {
                return new ProjectParticipationTab(panelId, ProjectForm.this);
            }
        });
    }
}
