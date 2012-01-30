package com.mindalliance.channels.playbook.pages.forms.tests;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.log4j.Logger;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.FormPanel;
import com.mindalliance.channels.playbook.ifm.*;
import com.mindalliance.channels.playbook.ifm.playbook.*;
import com.mindalliance.channels.playbook.ifm.taxonomy.*;
import com.mindalliance.channels.playbook.ifm.project.resources.*;
import com.mindalliance.channels.playbook.ifm.project.resources.System;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.project.environment.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 8, 2008
 * Time: 1:58:50 PM
 */
public class FormTest extends WebPage {

    PlaybookSession session;
    Label elementLabel;
    Ref selected;
    WebMarkupContainer debugDiv;

    public FormTest(PageParameters parms) {
        super(parms);
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see org.apache.wicket.Page#configureResponse()
     */
    protected void configureResponse() {
        super.configureResponse();
        WebResponse response = getWebRequestCycle().getWebResponse();
        response.setContentType("application/xhtml+xml");
    }


    public Ref getSelected() {
        return selected;
    }

    public void setSelected(Ref selected) {
        this.selected = selected;
    }

    private void load() throws Exception {
        session = (PlaybookSession) Session.get();
        if (!session.authenticate("admin", "admin")) {
            throw new Exception("User not authenticated");
        }
        final FormPanel formPanel = new FormPanel("content-form", new PropertyModel(this, "selected"));
        add(formPanel);
        final DropDownChoice typesDropDown = new DropDownChoice("types", new Model(), new ArrayList());
        typesDropDown.setChoices(getTypeChoices());
        typesDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                Class type = (Class) typesDropDown.getModel().getObject();
                if (type != null) {
                    try {
                        setSelected(getElementOfType(type));
                        formPanel.modelChanged();
                        target.addComponent(formPanel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (selected != null) {
                        elementLabel = new Label("elementString", new RefPropertyModel(selected, "name"));
                        elementLabel.setOutputMarkupId(true);
                        debugDiv.addOrReplace(elementLabel);
                        debugDiv.setOutputMarkupId(true);
                        target.addComponent(debugDiv);
                    }
                }
            }
        });
        add(typesDropDown);
        debugDiv = new WebMarkupContainer("debug");
        elementLabel = new Label("elementString", "");
        elementLabel.setOutputMarkupId(true);
        debugDiv.add(elementLabel);
        add(debugDiv);
        AjaxLink saveLink = new AjaxLink("save") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession) Session.get();
                session.commit();
                formPanel.resetForm();
                target.addComponent(formPanel);
                Logger.getLogger(this.getClass()).info("COMMIT: " + (String) RefUtils.get(selected, "name"));
                target.addComponent(elementLabel);
            }
        };
        AjaxLink resetLink = new AjaxLink("reset") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession) Session.get();
                session.abort();
                formPanel.resetForm();
                target.addComponent(formPanel);
                Logger.getLogger(this.getClass()).info("ABORT: " + RefUtils.get(selected, "name") + "\n");
            }
        };
        add(saveLink);
        add(resetLink);
    }

    private List getTypeChoices() {
        List choices = new ArrayList();
        // Environment
        choices.add(SharingAgreement.class);
        choices.add(Place.class);
        choices.add(Policy.class);
        // Resources
        choices.add(Organization.class);
        choices.add(Person.class);
        choices.add(Position.class);
        choices.add(System.class);
        // Taxonomy
        choices.add(AreaType.class);
        choices.add(EventType.class);
        choices.add(MediumType.class);
        choices.add(OrganizationType.class);
        choices.add(PlaceType.class);
        choices.add(Role.class);
        choices.add(TaskType.class);
        // Playbook
        choices.add(Assignation.class);
        choices.add(Association.class);
        choices.add(ConfirmationRequest.class);
        choices.add(Detection.class);
        choices.add(Event.class);
        choices.add(Group.class);
        choices.add(InformationRequest.class);
        choices.add(InformationTransfer.class);
        choices.add(Relocation.class);
        choices.add(SharingCommitment.class);
        choices.add(SharingRequest.class);
        choices.add(Task.class);
        // Project elements
        choices.add(Taxonomy.class);
        choices.add(Playbook.class);
        // application
        choices.add(Project.class);
        choices.add(Tab.class);
        choices.add(User.class);
        return choices;
    }

    private Ref getElementOfType(Class type) throws Exception {
        PlaybookApplication app = (PlaybookApplication) Application.get();
        Ref channels = app.getChannels();
        Project project = (Project) Project.current().deref();
        List<Ref> results = new ArrayList<Ref>();
        if (type.equals(User.class)) {
            results.add(((Channels) channels.deref()).findUser("admin"));
        } else if (type.equals(Project.class)) {
            results.add(project.getReference());
        } else if (type.equals(SharingAgreement.class)) {
            results.add((Ref) project.getSharingAgreements().get(0));
        } else if (type.equals(Taxonomy.class)) {
            results.add((Ref) project.getTaxonomies().get(0));
        } else if (type.equals(Playbook.class)) {
            results.add((Ref) project.findPlaybookNamed("default"));
        }
        if (results.size() > 0) {
            return results.get(0);
        } else {   // Project, model and playbook elements (assumes at least one pre-defined project, model and playbook
            ModelElement element = (ModelElement) type.newInstance();
            element.persist();
            Ref ref = element.getReference();
            if (element.isPlaybookElement()) {
                Playbook playbook = (Playbook) ((Ref) project.getPlaybooks().get(0)).deref();
                playbook.getReference().begin();
                playbook.addElement(element);
            }
            else if (element.isTaxonomyElement()) {
            Taxonomy taxonomy = (Taxonomy) ((Ref) project.getTaxonomies().get(0)).deref();
            taxonomy.getReference().begin();
            taxonomy.addElement(element);
            }
            else if (element.isProjectElement()) {
                InProject projectElement = (InProject) element;
                project.getReference().begin();
                if (projectElement instanceof Resource) {
                    Resource resource = (Resource) projectElement;
                    if (resource.isOrganizationResource()) {
                        Ref org = (Ref) project.getOrganizations().get(0); // assumes at least one organization pre-defined
                        org.begin();
                        ((Organization) org.deref()).addElement(element);
                    } else {
                        project.addElement(element);
                    }
                } else {
                    project.addElement(element);
                }
            }
            return ref;
        }
    }

}