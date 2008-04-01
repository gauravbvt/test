package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2008
 * Time: 8:44:22 AM
 */
public class OrganizationPanel extends AbstractElementPanel {
    TextField nameField;
    TextArea descriptionField;
    OrganizationTypesPanel organizationTypesPanel;
    LocationPanel addressPanel;
    LocationPanel jurisdictionPanel;

    public OrganizationPanel(String id, Ref p) {
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
        nameField = new TextField("name", new RefPropertyModel(element, "name"));
        descriptionField = new TextArea("description", new RefPropertyModel(element, "description"));
        addElementField(nameField);
        addElementField(descriptionField);
        // Organization types panel
        String[] ddOTPropNames = {"domain.name","jurisdictionType","name"};
        organizationTypesPanel = new OrganizationTypesPanel("organizationTypes", element, "organizationTypes", ddOTPropNames);
        addRefListPanel(organizationTypesPanel);
        // Address location panel
        addressPanel = new LocationPanel("address", element, "address");
        addComponentPanel(addressPanel, "address");
        // Jurisdication location panel
        jurisdictionPanel = new LocationPanel("jurisdication", element, "jurisdiction");
        addComponentPanel(jurisdictionPanel, "jurisdiction");
    }

    @Override
     public void refresh(AjaxRequestTarget target) {
        super.refresh(target);
        element.changed("name"); // forces an immediate persist to session - needed to ensure location is in sync
        addressPanel.refresh(target);
        jurisdictionPanel.refresh(target);
        organizationTypesPanel.refresh(target);
    }


}
