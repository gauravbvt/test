package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.LocationPanel;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 25, 2008
 * Time: 8:23:28 AM
 */
public class PersonPanel extends AbstractElementPanel {

    TextField firstNameField;
    TextField middleNameField;
    TextField lastNameField;
    PositionsPanel positionsPanel;
    LocationPanel locationPanel;

    public PersonPanel(String id, Ref p) {
        super(id, p);
    }

    @Override
    protected void init() {
        super.init();
        // Anything else?
    }

    @Override
    protected void load() {
        super.load();
        firstNameField = new TextField("firstName", new RefPropertyModel(element, "firstName"));
        middleNameField = new TextField("middleName", new RefPropertyModel(element, "middleName"));
        lastNameField = new TextField("lastName", new RefPropertyModel(element, "lastName"));
        addElementField(firstNameField);
        addElementField(middleNameField);
        addElementField(lastNameField);
        // Positions panel
        String[] ddPropNames = {"organization.name","name"};
        positionsPanel = new PositionsPanel("positions", element, "positions", ddPropNames);
        addRefListPanel(positionsPanel);
        // Location panel
        locationPanel = new LocationPanel("address", element, "address");
        addComponentPanel(locationPanel, "address");
    }

    @Override
     public void refresh(AjaxRequestTarget target) {
        super.refresh(target);
        element.changed("address"); // forces an immediate persist to session - needed to ensure location is in sync
        locationPanel.refresh(target);
    }

}
