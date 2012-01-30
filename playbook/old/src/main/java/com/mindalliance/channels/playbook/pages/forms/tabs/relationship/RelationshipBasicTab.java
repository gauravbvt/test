package com.mindalliance.channels.playbook.pages.forms.tabs.relationship;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.ReferencePanel;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 20, 2008
 * Time: 12:00:39 PM
 */
public class RelationshipBasicTab extends AbstractFormTab {

    private ReferencePanel fromResourcePanel;
    private AutoCompleteTextFieldWithChoices relationshipNameField;
    private ReferencePanel toResourcePanel;

    public RelationshipBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    // TODO validate that string is not empty and relationship is not already defined in project
    protected void load() {
        super.load();
        fromResourcePanel = new ReferencePanel("fromResource", this, "fromResource",
                new RefQueryModel<List<Ref>>(getProject(), new Query("findAllResources")));
        add(fromResourcePanel);

        relationshipNameField = new AutoCompleteTextFieldWithChoices("name",
                                                                 new RefPropertyModel<String>(getElement(), "name"),
                                                                 new RefQueryModel(getProject(),new Query("findAllRelationshipNames")));
        relationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setProperty("name", relationshipNameField.getDefaultModelObjectAsString());
            }
        });
        add(relationshipNameField);

        toResourcePanel = new ReferencePanel("toResource", this, "toResource",
                new RefQueryModel<List<Ref>>(getProject(), new Query("findAllResources")));
        add(toResourcePanel);

    }
}
