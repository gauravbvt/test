package com.mindalliance.channels.playbook.pages.forms.tests;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.pages.forms.SystemPanel;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2008
 * Time: 11:42:45 PM
 */
public class SystemTest extends WebPage {

    PlaybookSession session;
    Label systemLabel;
    Label organizationLabel;
    Ref system;
    WebMarkupContainer debugDiv;

    public SystemTest(PageParameters parms) {
        super(parms);
        load();
    }

    private void load() {
        session = (PlaybookSession) Session.get();
        session.authenticate("admin", "admin");
        Project project = (Project)Project.currentProject().deref();
        List<Ref> systems = project.findAllResourcesOfType("System");
        final Ref system = (Ref)systems.get(0);
        final SystemPanel systemPanel = new SystemPanel("system", system);
        add(systemPanel);
        debugDiv = new WebMarkupContainer("debug");
        add(debugDiv);
        systemLabel = new Label("systemString", new RefPropertyModel(system, "name"));
        systemLabel.setOutputMarkupId(true);
        debugDiv.add(systemLabel);
        organizationLabel = new Label("organizationString", new RefPropertyModel(system, "organization.name"));
        organizationLabel.setOutputMarkupId(true);
        debugDiv.add(organizationLabel);
        AjaxLink saveLink = new AjaxLink("save") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.commit();
                systemPanel.refresh(target);
                System.out.print("COMMIT: " + RefUtils.get(system, "name") + "\n");
                target.addComponent(systemLabel);
                target.addComponent(organizationLabel);
            }
        };
        AjaxLink resetLink = new AjaxLink("reset") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.abort();
                systemPanel.refresh(target);
                System.out.print("ABORT: " +  RefUtils.get(system, "name") + "\n");
                systemPanel.setOutputMarkupId(true);
                target.addComponent(systemPanel);
            }
        };
        //      resetLink.add(new SimpleAttributeModifier("onclick", "return confirm('Abandon your changes?"));
        add(saveLink);
        add(resetLink);
    }
}
