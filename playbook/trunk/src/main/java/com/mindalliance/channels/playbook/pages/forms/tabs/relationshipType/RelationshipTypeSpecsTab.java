package com.mindalliance.channels.playbook.pages.forms.tabs.relationshipType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractModelElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.model.RelationshipType;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 4:42:40 PM
 */
public class RelationshipTypeSpecsTab extends AbstractModelElementFormTab {

    protected DropDownChoice fromKindChoice;
    protected DropDownChoice toKindChoice;
    protected Label nameLabel;
    protected CheckBox transitiveField;

    public RelationshipTypeSpecsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        fromKindChoice = new DropDownChoice("fromKind", new RefPropertyModel(getElement(), "fromKind"), RelationshipType.getResourceKinds());
        fromKindChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String kind = fromKindChoice.getModelObjectAsString();
                if (kind != null) RefUtils.set(getElement(), "fromKind", kind);
            }
        });
        addReplaceable(fromKindChoice);
        toKindChoice = new DropDownChoice("fromKind", new RefPropertyModel(getElement(), "toKind"), RelationshipType.getResourceKinds());
        toKindChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String kind = toKindChoice.getModelObjectAsString();
                if (kind != null) RefUtils.set(getElement(), "toKind", kind);
            }
        });
        addReplaceable(toKindChoice);
        transitiveField = new CheckBox("transitive", new RefPropertyModel(getElement(), "transitive"));
        addInputField(transitiveField);
    }
}
