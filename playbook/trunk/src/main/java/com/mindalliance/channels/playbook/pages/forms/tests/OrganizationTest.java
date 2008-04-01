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
import com.mindalliance.channels.playbook.pages.forms.OrganizationPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2008
 * Time: 12:03:23 PM
 */
public class OrganizationTest  extends WebPage {

    PlaybookSession session;
    Label orgLabel;
    Label addressLabel;
    Label jurisdictionLabel;
    Ref org;
    WebMarkupContainer debugDiv;

    public OrganizationTest(PageParameters parms) {
        super(parms);
        load();
    }

    private void load() {
        session = (PlaybookSession) Session.get();
        session.authenticate("admin", "admin");
        Project project = (Project)Project.currentProject().deref();
        org = project.findResourceNamed("Organization", "ACME Inc.");
        final OrganizationPanel orgPanel = new OrganizationPanel("organization", org);
        add(orgPanel);
        debugDiv = new WebMarkupContainer("debug");
        add(debugDiv);
        orgLabel = new Label("organizationString", new RefPropertyModel(org, "name"));
        orgLabel.setOutputMarkupId(true);
        debugDiv.add(orgLabel);
        addressLabel = new Label("addressString", new RefPropertyModel(org, "address.name"));
        addressLabel.setOutputMarkupId(true);
        debugDiv.add(addressLabel);
        jurisdictionLabel = new Label("jurisdictionString", new RefPropertyModel(org, "jurisdiction.name"));
        jurisdictionLabel.setOutputMarkupId(true);
        debugDiv.add(jurisdictionLabel);
        AjaxLink saveLink = new AjaxLink("save") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.commit();
                orgPanel.refresh(target);
                System.out.print("COMMIT: " + RefUtils.get(org, "name") + "\n");
                System.out.print("COMMIT: (address) " + RefUtils.get(org, "address.name") + "\n");
                System.out.print("COMMIT: (jurisdiction)" + RefUtils.get(org, "jurisdiction.name") + "\n");
                target.addComponent(orgLabel);
                target.addComponent(addressLabel);
                target.addComponent(jurisdictionLabel);
            }
        };
        AjaxLink resetLink = new AjaxLink("reset") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.abort();
                orgPanel.refresh(target);
                System.out.print("ABORT: " + RefUtils.get(org, "name") + "\n");
                System.out.print("ABORT: (address)" + RefUtils.get(org, "address.name") + "\n");
                System.out.print("ABORT: (jurisdiction)" + RefUtils.get(org, "jurisdiction.name") + "\n");
                orgPanel.setOutputMarkupId(true);
                target.addComponent(orgLabel);
                target.addComponent(addressLabel);
                target.addComponent(jurisdictionLabel);
            }
        };
        //      resetLink.add(new SimpleAttributeModifier("onclick", "return confirm('Abandon your changes?"));
        add(saveLink);
        add(resetLink);
    }


}
