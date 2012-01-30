package com.mindalliance.channels.playbook.pages.forms.tabs.association;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.playbook.Association;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.Component;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 9:26:25 PM
 */
public class AssociationRelationshipTab extends AbstractFormTab {

    protected Association association;
    AutoCompleteTextFieldWithChoices relationshipNameField;
    DynamicFilterTree toAgentTree;
    Label toAgentNameLabel;

    public AssociationRelationshipTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        association = (Association) getElement().deref();
        relationshipNameField = new AutoCompleteTextFieldWithChoices("relationshipName",
                new RefPropertyModel(getElement(), "relationshipName"),
                new RefQueryModel(getProject(), new Query("findAllRelationshipNames")));
        relationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // do nothing
            }
        });
        addReplaceable(relationshipNameField);
        toAgentTree = new DynamicFilterTree("toAgent", new RefPropertyModel(getElement(), "toAgent"),
                new RefQueryModel(getPlaybook(),
                        new Query("findAllAgentsExcept", getElement(), "actors")),
                SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedResource = toAgentTree.getNewSelection();
                setProperty("toAgent", selectedResource);
            }
        };
        addReplaceable(toAgentTree);
    }


}
