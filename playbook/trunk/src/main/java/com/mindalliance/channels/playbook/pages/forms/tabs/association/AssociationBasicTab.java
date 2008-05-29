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
public class AssociationBasicTab extends AbstractFormTab {

    protected Association association;
    protected TextArea descriptionField;
    protected DynamicFilterTree actorAgentTree;
    AutoCompleteTextFieldWithChoices relationshipNameField;
    DynamicFilterTree toAgentTree;
    WebMarkupContainer reverseRelationshipDiv;
    Label toAgentNameLabel;
    AutoCompleteTextFieldWithChoices reverseRelationshipNameField;
    Label actorAgentNameLabel;

    public AssociationBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        association = (Association) getElement().deref();
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        actorAgentTree = new DynamicFilterTree("actorAgent", new RefPropertyModel(getElement(), "actorAgent"),
                new RefQueryModel(getPlaybook(), new Query("findAllAgents")),
                SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedAgent = actorAgentTree.getNewSelection();
                RefUtils.set(getElement(), "actorAgent", selectedAgent);
                setReverseRelationshipVisibility();
                target.addComponent(reverseRelationshipDiv);
            }
        };
        addReplaceable(actorAgentTree);
        relationshipNameField = new AutoCompleteTextFieldWithChoices("relationshipName",
                new RefPropertyModel(getElement(), "relationshipName"),
                new RefQueryModel(Channels.instance(),
                        new Query("findAllRelationshipNames")));
        relationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setReverseRelationshipVisibility();
                target.addComponent(reverseRelationshipDiv);
            }
        });
        addReplaceable(relationshipNameField);
        toAgentTree = new DynamicFilterTree("toAgent", new RefPropertyModel(getElement(), "toAgent"),
                new RefQueryModel(getPlaybook(),
                        new Query("findAllAgentsExcept", getElement(), "actorAgent")),
                SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedResource = toAgentTree.getNewSelection();
                RefUtils.set(getElement(), "toAgent", selectedResource);
                setReverseRelationshipVisibility();
                target.addComponent(reverseRelationshipDiv);
            }
        };
        addReplaceable(toAgentTree);
        reverseRelationshipDiv = new WebMarkupContainer("reverseRelationship");
        addReplaceable(reverseRelationshipDiv);
        toAgentNameLabel = new Label("toAgentName", new RefPropertyModel(getElement(), "toAgent.name"));
        addReplaceableTo(toAgentNameLabel, reverseRelationshipDiv);
        reverseRelationshipNameField = new AutoCompleteTextFieldWithChoices("reverseRelationshipName",
                new RefPropertyModel(getElement(), "reverseRelationshipName"),
                new RefQueryModel(Channels.instance(),
                        new Query("findAllRelationshipNames")));
        addReplaceableTo(reverseRelationshipNameField,reverseRelationshipDiv);
        actorAgentNameLabel = new Label("actorAgentName", new RefPropertyModel(getElement(), "actorAgent.name"));
        addReplaceableTo(actorAgentNameLabel, reverseRelationshipDiv);
        setReverseRelationshipVisibility();
    }

    private void setReverseRelationshipVisibility() {
        if (association.getActorAgent() != null &&
                association.getToAgent() != null &&
                association.getRelationshipName() != null &&
                !association.getRelationshipName().trim().isEmpty()) {
            reverseRelationshipDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            reverseRelationshipDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
    }
}
