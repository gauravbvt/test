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
import com.mindalliance.channels.playbook.support.QueryHandler;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementPanel;
import com.mindalliance.channels.playbook.pages.FormPanel;

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
                String type = (String) typesDropDown.getModel().getObject();
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
        choices.add("Person");
        choices.add("Organization");
        choices.add("System");
        return choices;
    }

    private Ref getElementOfType(String type) throws Exception {
        PlaybookApplication app = (PlaybookApplication) Application.get();
        QueryHandler qh = app.getQueryHandler();
        Map<String, Object> args = new HashMap<String, Object>();
        Ref channels = app.getChannels();
        List<Ref> results = null;
        args.put("type", type);

        if (type.equals("Person")) {
            results = qh.executeQuery(channels, "findAResource", args);
        } else if (type.equals("Organization")) {
            results = qh.executeQuery(channels, "findAResource", args);
        } else if (type.equals("System")) {
            results = qh.executeQuery(channels, "findAResource", args);
        }
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return app.createNewElement(type);
        }
    }

}