package com.mindalliance.channels.playbook.pages.forms.tabs.relationshipType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractModelElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.model.RelationshipType;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 4:42:40 PM
 */
public class RelationshipTypeSpecsTab extends AbstractModelElementFormTab {

    protected ListMultipleChoice fromKindChoice;
    protected ListMultipleChoice toKindChoice;
    protected Label nameLabel;
    protected CheckBox transitiveField;

    public RelationshipTypeSpecsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        // TODO -- use CheckBoxMultipleChoice when it works with Ajax
        fromKindChoice = new ListMultipleChoice("fromKinds", new Model((Serializable)RefUtils.get(getElement(), "fromKinds")), RelationshipType.getResourceKinds());
        fromKindChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                List<String> kinds = (List<String>)fromKindChoice.getModelObject();
                RefUtils.set(getElement(), "fromKinds", kinds);
            }
        });
        nameLabel = new Label("name", new RefPropertyModel(getElement(), "name"));
        addReplaceable(nameLabel);
        addReplaceable(fromKindChoice);
        toKindChoice = new ListMultipleChoice("toKinds", new Model((Serializable)RefUtils.get(getElement(), "toKinds")), RelationshipType.getResourceKinds());
        toKindChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                List<String> kinds = (List<String>)fromKindChoice.getModelObject();
                RefUtils.set(getElement(), "toKinds", kinds);
            }
        });
        addReplaceable(toKindChoice);
        transitiveField = new CheckBox("transitive", new RefPropertyModel(getElement(), "transitive"));
        addInputField(transitiveField);
    }
}
