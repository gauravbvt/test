package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.PageParameters;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.PathExpression;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.panels.LocationPanel;
import com.mindalliance.channels.playbook.ifm.Person;
import com.mindalliance.channels.playbook.ifm.Location;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 25, 2008
 * Time: 8:23:28 AM
 */
public class PersonForm extends WebPage {

    public PersonForm(final PageParameters pageParameters) {
        super(pageParameters);
        PlaybookSession session = (PlaybookSession) getSession();
        session.authenticate("admin", "admin");
        Ref user = session.getUser();
        final Ref person;
        Ref ref = (Ref) user.deref("person");
        if (ref == null) {
            person = new Person().persist();
            PathExpression.setNestedProperty(user, "person", person.getReference());
        }
        else {
            person = ref;
        }
        final LocationPanel locationPanel = new LocationPanel("location", (Location)person.deref("address"));
            add(new FeedbackPanel("feedback"));
            Form form = new Form("person") {
                public void onSubmit() {
                    Location editedLocation = locationPanel.getLocation();
                    PathExpression.setNestedProperty(person, "address", editedLocation);
                }
            };
            form.add(new TextField("firstName", new RefPropertyModel(person, "firstName")).setRequired(true));
            form.add(new TextField("middleName", new RefPropertyModel(person, "middleName")));
            form.add(new TextField("lastName", new RefPropertyModel(person, "lastName")).setRequired(true));
            form.add(locationPanel);
            add(form);
    }

}
