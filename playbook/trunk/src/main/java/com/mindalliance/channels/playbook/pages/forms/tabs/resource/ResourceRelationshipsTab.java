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
    Label newFromResourceNameLabel;
    AutoCompleteTextFieldWithChoices newRelationshipNameField;
    DynamicFilterTree resourcesTree;
    WebMarkupContainer reverseRelationshipDiv;
    Label reverseFromResourceNameLabel;
    AutoCompleteTextFieldWithChoices reverseRelationshipNameField;
    AjaxButton addRelationshipsButton;
    String newRelationshipName;
    Ref newToResource;
    String newReverseRelationshipName;

    public ResourceRelationshipsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        // existing relationships
        relationshipsDiv = new WebMarkupContainer("relationshipsDiv");
        relationshipsView = new RefreshingView("relationships", new RefQueryModel(getProject(), new Query("findAllRelationshipsOf", getElement()))) {
            protected Iterator getItemModels() {
                List<Ref> allRelationships = (List<Ref>) relationshipsView.getModelObject();
                return new ModelIteratorAdapter(allRelationships.iterator()) {
                     protected IModel model(Object relationship) {
                         return new RefModel((Ref) relationship);
                     }
                 };
            }
            protected void populateItem(Item item) {
                final Ref relationship = (Ref)item.getModelObject();
                AjaxLink fromResourceLink = new AjaxLink("fromResourceLink") {
                    public void onClick(AjaxRequestTarget target) {
                        edit((Ref)RefUtils.get(relationship,"fromrAgent"), target);
                    }
                };
                Label fromResourceNameLabel = new Label("fromResourceName", new RefPropertyModel(relationship, "fromAgent.name"));
                fromResourceLink.add(fromResourceNameLabel);
                item.add(fromResourceLink);
                Label relationshipNameLabel = new Label("relationshipName", new RefPropertyModel(relationship, "name"));
                item.add(relationshipNameLabel);
                AjaxLink toResourceLink = new AjaxLink("toResourceLink") {
                     public void onClick(AjaxRequestTarget target) {
                         edit((Ref)RefUtils.get(relationship,"toAgent"), target);
                     }
                 };
                 Label toResourceNameLabel = new Label("toResourceName", new RefPropertyModel(relationship, "toAgent.name"));
                 toResourceLink.add(toResourceNameLabel);
                 item.add(toResourceLink);
                 AjaxLink deleteRelationshipLink = new AjaxLink("deleteRelationship") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(getProject(), "relationships", relationship);
                        target.addComponent(relationshipsDiv);
                    }
                };
                item.add(deleteRelationshipLink);
            }
        };
        relationshipsDiv.add(relationshipsView);
        addReplaceable(relationshipsDiv);
        // new relationship
        newFromResourceNameLabel = new Label("newFromResourceName", (String)RefUtils.get(getElement(), "name"));
        addReplaceable(newFromResourceNameLabel);
        newRelationshipNameField = new AutoCompleteTextFieldWithChoices("newRelationshipName",
                                                                 new RefPropertyModel(getElement(), "relationshipName"),
                                                                 new RefQueryModel(Channels.instance(),new Query("findAllRelationshipNames")));
        newRelationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newRelationshipName = newRelationshipNameField.getModelObjectAsString();
                updateVisibility(target);
            }
        });
        addReplaceable(newRelationshipNameField);
        resourcesTree = new DynamicFilterTree("resources", new Model(),
                                               new RefQueryModel(getProject(), new Query("findAllResourcesExcept", RefUtils.get(getElement(), "fromAgent"))),
                                               SINGLE_SELECTION) {
            public void onFilterSelec(AjaxRequestTarget target, Filter filter) {
                newToResource = resourcesTree.getNewSelection();
                updateVisibility(target);
            }
        };
        addReplaceable(resourcesTree);
        reverseRelationshipDiv = new WebMarkupContainer("reverseRelationshipDiv");
        reverseFromResourceNameLabel = new Label("reverseFromResourceName", (String)RefUtils.get(newToResource, "name"));
        reverseRelationshipDiv.add(reverseFromResourceNameLabel);
        reverseRelationshipNameField = new AutoCompleteTextFieldWithChoices("reverseRelationshipName",
                                                                 new RefPropertyModel(getElement(), "relationshipName"),
                                                                 new RefQueryModel(Channels.instance(),new Query("findAllRelationshipNames")));
        reverseRelationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                newReverseRelationshipName = reverseRelationshipNameField.getModelObjectAsString();
                updateVisibility(target);
            }
        });
        reverseRelationshipDiv.add(reverseRelationshipNameField);
        Label reverseToResourceNameLabel = new Label("reverseToResourceName", (String)RefUtils.get(getElement(), "name"));
        reverseRelationshipDiv.add(reverseToResourceNameLabel);
        addReplaceable(reverseRelationshipDiv);
        reverseRelationshipDiv.add(new AttributeModifier("style", new Model("display:none")));
        addRelationshipsButton = new AjaxButton("addRelationships") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Relationship newRelationship = new Relationship();
                newRelationship.setFromAgent(getElement());
                newRelationship.setRelationshipName(newRelationshipName.trim().toLowerCase());
                newRelationship.setToAgent(newToResource);
                RefUtils.add(getProject(), "relationships", newRelationship.persist());
                newRelationshipName = "";
                newToResource = null;
                newRelationshipNameField.clearInput();
                target.addComponent(newRelationshipNameField);
                if (newReverseRelationshipName != null && !newReverseRelationshipName.trim().isEmpty()) {
                    Relationship newReverseRelationship = new Relationship();
                    newReverseRelationship.setFromAgent(newToResource);
                    newReverseRelationship.setRelationshipName(newReverseRelationshipName.trim().toLowerCase());
                    newReverseRelationship.setToAgent(getElement());
                    newReverseRelationship.setReverseRelationship(newRelationship.getReference());
                    newRelationship.setReverseRelationship(newReverseRelationship.getReference());                    
                    RefUtils.add(getProject(), "relationships", newReverseRelationship.persist());
                    newReverseRelationshipName = "";
                    reverseRelationshipNameField.clearInput();
                    target.addComponent(reverseRelationshipNameField);
                }
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
            newToResource != null) {
          reverseRelationshipDiv.add(new AttributeModifier("style", new Model("display:block")));
          addRelationshipsButton.setEnabled(true);
       }
        else {
          reverseRelationshipDiv.add(new AttributeModifier("style", new Model("display:none")));
          addRelationshipsButton.setEnabled(false);
       }
    }

 }
