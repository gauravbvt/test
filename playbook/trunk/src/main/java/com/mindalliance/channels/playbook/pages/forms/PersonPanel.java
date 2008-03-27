package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
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

    final Ref person;

    public PersonPanel(String id, final Ref person) {
        super(id);
        this.person = person;
        load();
    }

    private void load() {
        final Person copy = (Person) person.deref().copy();
        final LocationPanel locationPanel = new LocationPanel("location", (Location) copy.getAddress());
        add(new FeedbackPanel("feedback"));
        Form form = new Form("person") {
            public void onSubmit() {
                Location editedLocation = locationPanel.getLocation();
                copy.setAddress(editedLocation);
                person.deref().setFrom(copy);
            }
        };
        form.add(new TextField("firstName", new RefPropertyModel(copy, "firstName")).setRequired(true));
        form.add(new TextField("middleName", new RefPropertyModel(copy, "middleName")));
        form.add(new TextField("lastName", new RefPropertyModel(copy, "lastName")).setRequired(true));
        form.add(locationPanel);
        form.add(new Button("cancel").add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
               /// TODO reset, reload
            }
        }));
        add(form);
    }

}
