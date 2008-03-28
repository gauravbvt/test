package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.Session;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.LocationPanel;
import com.mindalliance.channels.playbook.ifm.Person;
import com.mindalliance.channels.playbook.ifm.Location;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 25, 2008
 * Time: 8:23:28 AM
 */
public class PersonPanel extends Panel {

    Ref person;
    FeedbackPanel feedback;
    TextField firstNameField;
    TextField middleNameField;
    TextField lastNameField;
    LocationPanel locationPanel;

    public PersonPanel(String id, final Ref person) {
        super(id);
        this.person = person;
        load();
    }

    private void load() {
        // feedback panel
        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
        // firstName
        firstNameField = new TextField("firstName", new RefPropertyModel(person, "firstName"));
        System.out.print((String) "First name = " + (String) RefUtils.get(person, "firstName"));
        firstNameField.setRequired(true);
        firstNameField.setPersistent(false);
        firstNameField.setOutputMarkupId(true);
        firstNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(feedback);
            }
        });
        // middleName
        middleNameField = new TextField("middleName", new RefPropertyModel(person, "middleName"));
        System.out.print((String) "Middle name = " + (String) RefUtils.get(person, "middleName"));
        middleNameField.setPersistent(false);
        middleNameField.setRequired(true);
        middleNameField.setOutputMarkupId(true);
        middleNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(feedback);
            }
        });
        // lastName
        lastNameField = new TextField("lastName", new RefPropertyModel(person, "lastName"));
        System.out.print((String) "Last name = " + (String) RefUtils.get(person, "lastName"));
        lastNameField.setPersistent(false);
        lastNameField.setRequired(true);
        lastNameField.setOutputMarkupId(true);
        lastNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(feedback);
            }
        });
        // form
        Form form = new Form("person");
        form.add(firstNameField);
        form.add(middleNameField);
        form.add(lastNameField);
        form.removePersistentFormComponentValues(true);
        add(form);
        // Location panel
        locationPanel = new LocationPanel("location", (Location) person.deref("address"));
        locationPanel.setOutputMarkupId(true);
        add(locationPanel);
    }

    public void refresh(Ref person, AjaxRequestTarget target) {
        this.person.become(person);
        locationPanel.refresh((Location)person.deref("address"), target);
        target.addComponent(firstNameField);
        target.addComponent(middleNameField);
        target.addComponent(lastNameField);
        target.addComponent(locationPanel);
    }

}
