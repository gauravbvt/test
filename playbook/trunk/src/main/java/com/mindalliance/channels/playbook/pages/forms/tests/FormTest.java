package com.mindalliance.channels.playbook.pages.forms.tests;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.Application;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.pages.FormPanel;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.project.resources.Person;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.project.environment.Agreement;
import com.mindalliance.channels.playbook.query.Query;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
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

    public Ref getSelected() {
        return selected;
    }

    public void setSelected(Ref selected) {
        this.selected = selected;
    }

    private void load() throws Exception {
        session = (PlaybookSession) Session.get();
        if (!session.authenticate("admin", "admin")) {
            throw new Exception ("User not authenticated");
        }
        final FormPanel formPanel = new FormPanel("content-form", new PropertyModel(this, "selected"));
        add(formPanel);
        final DropDownChoice typesDropDown = new DropDownChoice("types", new Model(), new ArrayList());
        typesDropDown.setChoices(getTypeChoices());
        typesDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                Class type = (Class) typesDropDown.getModel().getObject();
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
                target.addComponent( formPanel );
                System.out.print("COMMIT: " + RefUtils.get(selected, "name") + "\n");
                target.addComponent(elementLabel);
            }
        };
        AjaxLink resetLink = new AjaxLink("reset") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession) Session.get();
                session.abort();
                formPanel.resetForm();
                target.addComponent( formPanel );
                System.out.print("ABORT: " + RefUtils.get(selected, "name") + "\n");
            }
        };
        add(saveLink);
        add(resetLink);
    }

    private List getTypeChoices() {
        List choices = new ArrayList();
        choices.add(Agreement.class);
        choices.add(Person.class);
        choices.add(Project.class);
        choices.add(Tab.class);
        choices.add(User.class);
        return choices;
    }

    private Ref getElementOfType(Class type) throws Exception {
        PlaybookApplication app = (PlaybookApplication) Application.get();
        Ref channels = app.getChannels();
        Project project = (Project)Project.current().deref();
        List<Ref> results = new ArrayList<Ref>();
        if (type.equals(User.class)) {
            results.add( ((Channels)channels.deref()).findUser("admin") );
        }
        else if (type.equals(Project.class)) {
            results.add(project.getReference());
        }
        else if (type.equals(Person.class)) {
            results.add((Ref)Query.execute(project.getReference(), "findAResource", ((Referenceable)type.newInstance()).getType()));
        } else if (type.equals(Agreement.class)) {
            results = project.getAgreements();
       /* } else if (type.equals("System")) {
            results = qh.executeQuery(channels, "findAResource", args);
        */
        }
        if (results.size() > 0) {
            return results.get(0);
        } else {
            Ref ref = ((Referenceable)type.newInstance()).persist();  // Resetting the form will cause a null pointer exception
            return ref;
        }
    }

}