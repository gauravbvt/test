package com.mindalliance.channels.playbook.pages.forms.tests;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementPanel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 8, 2008
 * Time: 1:58:50 PM
 */
abstract public class FormTest  extends WebPage {

    PlaybookSession session;
    Label elementLabel;
    Ref element;
    AbstractElementPanel form;
    WebMarkupContainer debugDiv;

    public FormTest(PageParameters parms) {
        super(parms);
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load() throws Exception {
        session = (PlaybookSession) Session.get();
        session.authenticate("admin", "admin");
        element = getFormElement();
        Class formClass = element.deref().formClass();
        Class[] args = new Class[2];
        args[0] = String.class;
        args[1] = Ref.class;
        form = (AbstractElementPanel)formClass.getConstructor(args).newInstance("element", element);
        add(form);
        debugDiv = new WebMarkupContainer("debug");
        add(debugDiv);
        elementLabel = new Label("elementString", new RefPropertyModel(element, "name"));
        elementLabel.setOutputMarkupId(true);
        debugDiv.add(elementLabel);
        AjaxLink saveLink = new AjaxLink("save") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.commit();
                form.refresh(target);
                System.out.print("COMMIT: " + RefUtils.get(element, "name") + "\n");
                target.addComponent(elementLabel);
            }
        };
        AjaxLink resetLink = new AjaxLink("reset") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.abort();
                form.refresh(target);
                System.out.print("ABORT: " +  RefUtils.get(element, "name") + "\n");
                form.setOutputMarkupId(true);
                target.addComponent(form);
            }
        };
        add(saveLink);
        add(resetLink);
    }

    protected Ref getFormElement() {
        PageParameters params = getPageParameters();
        String query = params.getString("query");
        Map<String, Object> args = new HashMap<String,Object>();
        for (Object key : params.keySet()) {
           String name = (String)key;
           if (!name.equalsIgnoreCase("query")) {
               args.put(name, params.getString(name));
           }
        }
        PlaybookApplication app = (PlaybookApplication)Application.get();
        Ref channels = app.getChannels();
        List<Ref> results = channels.executeQuery(query, args);
        return results.get(0);
    }
}