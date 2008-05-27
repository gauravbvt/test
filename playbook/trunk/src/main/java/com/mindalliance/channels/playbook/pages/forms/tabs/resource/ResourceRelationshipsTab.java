package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship;
import com.mindalliance.channels.playbook.ifm.Channels;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.AttributeModifier;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 12:45:14 PM
 */
public class ResourceRelationshipsTab extends AbstractFormTab {

    WebMarkupContainer relationshipsDiv;
    RefreshingView relationshipsView;
    Label newFromAgentNameLabel;
    AutoCompleteTextFieldWithChoices newRelationshipNameField;
    DynamicFilterTree agentsTree;
    WebMarkupContainer reverseRelationshipDiv;
    Label reverseFromAgentNameLabel;
    AutoCompleteTextFieldWithChoices reverseRelationshipNameField;
    AjaxButton addRelationshipsButton;
    String newRelationshipName;
    Ref newToAgent;
    String newReverseRelationshipName;

    public ResourceRelationshipsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        // existing relationships
        relationshipsDiv = new WebMarkupContainer("relationshipsDiv");
        relationshipsView = new RefreshingView("relationships") {
            protected Iterator getItemModels() {
                List<Ref> allRelationships = (List<Ref>) Query.execute(getProject(), "findAllRelationshipsOf", getElement());
                return new ModelIteratorAdapter(allRelationships.iterator()) {
                     protected IModel model(Object relationship) {
                         return new RefModel((Ref) relationship);
                     }
                 };
            }
            protected void populateItem(Item item) {
                final Ref relationship = (Ref)item.getModelObject();
                AjaxLink fromAgentLink = new AjaxLink("fromAgentLink") {
                    public void onClick(AjaxRequestTarget target) {
                        edit((Ref)RefUtils.get(relationship,"fromAgent"), target);
                    }
                };
                Label fromAgentNameLabel = new Label("fromAgentName", new RefPropertyModel(relationship, "fromAgent.name"));
                fromAgentLink.add(fromAgentNameLabel);
                item.add(fromAgentLink);
                Label relationshipNameLabel = new Label("relationshipName", new RefPropertyModel(relationship, "name"));
                item.add(relationshipNameLabel);
                AjaxLink toAgentLink = new AjaxLink("toAgentLink") {
                     public void onClick(AjaxRequestTarget target) {
                         edit((Ref)RefUtils.get(relationship,"toAgent"), target);
                     }
                 };
                 Label toAgentNameLabel = new Label("toAgentName", new RefPropertyModel(relationship, "toAgent.name"));
                 toAgentLink.add(toAgentNameLabel);
                 item.add(toAgentLink);
                 AjaxLink deleteRelationshipLink = new AjaxLink("deleteRelationship") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(getProject(), "relationships", relationship);
                        relationship.delete();
                        target.addComponent(relationshipsDiv);
                    }
                };
                item.add(deleteRelationshipLink);
            }
        };
        relationshipsDiv.add(relationshipsView);
        addReplaceable(relationshipsDiv);
        // new relationship
        newFromAgentNameLabel = new Label("newFromAgentName", (String)RefUtils.get(getElement(), "name"));
        addReplaceable(newFromAgentNameLabel);
        newRelationshipNameField = new AutoCompleteTextFieldWithChoices("newRelationshipName",
                                                                 new Model(),
                                                                 new RefQueryModel(Channels.instance(),new Query("findAllRelationshipNames")));
        newRelationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newRelationshipName = newRelationshipNameField.getModelObjectAsString();
                updateVisibility(target);
            }
        });
        addReplaceable(newRelationshipNameField);
        agentsTree = new DynamicFilterTree("agents", new Model(),
                                               new RefQueryModel(getProject(), new Query("findAllResourcesExcept", RefUtils.get(getElement(), "fromAgent"))),
                                               SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                newToAgent = agentsTree.getNewSelection();
                updateVisibility(target);
                reverseFromAgentNameLabel.setModelObject((String)RefUtils.get(newToAgent, "name"));
                target.addComponent(reverseFromAgentNameLabel);
            }
        };
        addReplaceable(agentsTree);
        reverseRelationshipDiv = new WebMarkupContainer("reverseRelationshipDiv");
        addReplaceable(reverseRelationshipDiv);
        reverseFromAgentNameLabel = new Label("reverseFromAgentName", "");
        addReplaceableTo(reverseFromAgentNameLabel,reverseRelationshipDiv);
        reverseRelationshipNameField = new AutoCompleteTextFieldWithChoices("reverseRelationshipName",
                                                                 new Model(),
                                                                 new RefQueryModel(Channels.instance(),new Query("findAllRelationshipNames")));
        reverseRelationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newReverseRelationshipName = reverseRelationshipNameField.getModelObjectAsString();
                updateVisibility(target);
            }
        });
        addReplaceableTo(reverseRelationshipNameField,reverseRelationshipDiv);
        Label reverseToAgentNameLabel = new Label("reverseToAgentName", (String)RefUtils.get(getElement(), "name"));
        addReplaceableTo(reverseToAgentNameLabel,reverseRelationshipDiv);
        reverseRelationshipDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        addRelationshipsButton = new AjaxButton("addRelationships") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Relationship newRelationship = new Relationship();
                newRelationship.setFromAgent(getElement());
                newRelationship.setName(newRelationshipName.trim().toLowerCase());
                newRelationship.setToAgent(newToAgent);
                RefUtils.add(getProject(), "relationships", newRelationship.persist());
                if (newReverseRelationshipName != null && !newReverseRelationshipName.trim().isEmpty()) {
                    Relationship newReverseRelationship = new Relationship();
                    newReverseRelationship.setFromAgent(newToAgent);
                    newReverseRelationship.setName(newReverseRelationshipName.trim().toLowerCase());
                    newReverseRelationship.setToAgent(getElement());
                    newReverseRelationship.setReverseRelationship(newRelationship.getReference());
                    newRelationship.setReverseRelationship(newReverseRelationship.getReference());                    
                    RefUtils.add(getProject(), "relationships", newReverseRelationship.persist());
                }
                newRelationshipName = "";
                newRelationshipNameField.clearInput();
                newRelationshipNameField.setModelObject("");
                newRelationshipNameField.updateModel();
                target.addComponent(newRelationshipNameField);
                newToAgent = null;
                agentsTree.setSelections(new Model());
                agentsTree.modelChanged();
                target.addComponent(agentsTree);
                newReverseRelationshipName = "";
                reverseRelationshipNameField.clearInput();
                reverseRelationshipNameField.setModelObject("");
                reverseRelationshipNameField.updateModel();
                target.addComponent(reverseRelationshipNameField);
                updateVisibility(target);
                target.addComponent(relationshipsDiv);
            }
        };
        addRelationshipsButton.setEnabled(false);
        addReplaceable(addRelationshipsButton);
    }

    private void updateVisibility(AjaxRequestTarget target) {
       if (newRelationshipName != null &&
           !newRelationshipName.trim().isEmpty() &&
            newToAgent != null) {
          reverseRelationshipDiv.add(new AttributeModifier("style", true, new Model("display:block")));
          addRelationshipsButton.setEnabled(true);
       }
        else {
          reverseRelationshipDiv.add(new AttributeModifier("style", true, new Model("display:none")));
          addRelationshipsButton.setEnabled(false);
       }
        target.addComponent(reverseRelationshipDiv);
        target.addComponent(addRelationshipsButton);
    }

 }
