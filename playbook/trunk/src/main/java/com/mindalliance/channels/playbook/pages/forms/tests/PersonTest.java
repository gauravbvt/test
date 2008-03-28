package com.mindalliance.channels.playbook.pages.forms.tests;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import com.mindalliance.channels.playbook.pages.forms.PersonPanel;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.Person;
import com.mindalliance.channels.playbook.ifm.Location;
import com.mindalliance.channels.playbook.ifm.User;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2008
 * Time: 8:25:32 PM
 */
public class PersonTest extends WebPage {

    PlaybookSession session;
    Label personLabel;
    Label locationLabel;
    Ref person;
    WebMarkupContainer debugDiv;

    public PersonTest(PageParameters parms) {
        super(parms);
        load();
    }

    private void load() {
        session = (PlaybookSession) Session.get();
        session.authenticate("admin", "admin");
        commitUserPersonIfNeeded();
        final PersonPanel personPanel = new PersonPanel("person", person);
        add(personPanel);
        debugDiv = new WebMarkupContainer("debug");
        add(debugDiv);
        personLabel = new Label("personString", new RefPropertyModel(person, "name"));
        personLabel.setOutputMarkupId(true);
        debugDiv.add(personLabel);
        locationLabel = new Label("locationString", new RefPropertyModel(person, "address.name"));
        locationLabel.setOutputMarkupId(true);
        debugDiv.add(locationLabel);
        AjaxLink saveLink = new AjaxLink("save") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.commit();
                personPanel.refresh(target);
                System.out.print("COMMIT: " + (String) RefUtils.get(person, "name") + "\n");
                System.out.print("COMMIT: " + (String) RefUtils.get(person, "address.name") + "\n");
                target.addComponent(personLabel);
                target.addComponent(locationLabel);
            }
        };
        AjaxLink resetLink = new AjaxLink("reset") {
            public void onClick(AjaxRequestTarget target) {
                PlaybookSession session = (PlaybookSession)Session.get();
                session.abort();    
                personPanel.refresh(target);
                System.out.print("ABORT: " + (String) RefUtils.get(person, "name") + "\n");
                System.out.print("ABORT: " + (String) RefUtils.get(person, "address.name") + "\n");
                personPanel.setOutputMarkupId(true);
                target.addComponent(personLabel);
                target.addComponent(locationLabel);
            }
        };
        //      resetLink.add(new SimpleAttributeModifier("onclick", "return confirm('Abandon your changes?"));
        add(saveLink);
        add(resetLink);
    }

    private void commitUserPersonIfNeeded() {
        Ref user = session.getUser();
        person = (Ref)RefUtils.get(user, "person");
        if (person == null) {
            person = new Person().persist();
            RefUtils.set(user, "person", person);
            person.commit();
            user.commit();
        }
    }


}
