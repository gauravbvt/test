package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;

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
