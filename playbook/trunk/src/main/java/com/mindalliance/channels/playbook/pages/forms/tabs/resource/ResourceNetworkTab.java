package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.ifm.project.resources.Relationship;
import com.mindalliance.channels.playbook.ifm.project.resources.Resource;
import com.mindalliance.channels.playbook.ifm.project.environment.Agreement;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractProjectElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 9:45:02 AM
 */
public class ResourceNetworkTab extends AbstractProjectElementFormTab {

    protected DynamicFilterTree resourcesTree;
    protected WebMarkupContainer relationshipsDiv;
    protected RefreshingView relationshipsView;
    protected Button addRelationshipButton;
    protected WebMarkupContainer agreementsDiv;
    protected RefreshingView agreementsView;
    protected Button addAgreementButton;

    protected Ref selectedResource;

    public ResourceNetworkTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        loadResources();
        loadButtons();
        loadRelationships();
        loadAgreements();
    }

    private void loadResources() {
        resourcesTree = new DynamicFilterTree("resources", new Model(new ArrayList<Ref>()),
                                                           new RefQueryModel(getProject(), new Query("allResourcesExcept", getElement())), true) {
            public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> newSelections = resourcesTree.getNewSelections();
                if (newSelections.size() > 0) {
                    selectedResource = newSelections.get(0);
                }
                else {
                    selectedResource = null;
                }
                addAgreementButton.setEnabled(selectedResource != null);
                addRelationshipButton.setEnabled(selectedResource != null);
                target.addComponent(addAgreementButton);
                target.addComponent(addRelationshipButton);
              }
        };
        addReplaceable(resourcesTree);
    }

    private void loadButtons() {
        // Add relationship button
        addRelationshipButton = new Button("addRelationship");
        addRelationshipButton.setEnabled(false);
        addRelationshipButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                Relationship newRelationship = new Relationship();
                newRelationship.setWithResource(selectedResource);
                RefUtils.add(getElement(), "relationships", newRelationship);
                target.addComponent(relationshipsDiv);
            }
        });
        addReplaceable(addRelationshipButton);
        // Add agreement button
       addAgreementButton = new Button("addAgreement");
       addAgreementButton.setEnabled(false);
       addAgreementButton.add(new AjaxEventBehavior("onclick") {
           protected void onEvent(AjaxRequestTarget target) {
               Ref newAgreement = new Agreement().persist();
               RefUtils.set(newAgreement, "fromResource", getElement());
               RefUtils.set(newAgreement, "toResource", selectedResource);
               RefUtils.add(getProject(), "agreements", newAgreement);
               target.addComponent(agreementsDiv);
           }
       });
       addReplaceable(addAgreementButton);
    }

     private void loadRelationships() {
        // Relationships
        relationshipsDiv = new WebMarkupContainer("relationshipsDiv");
        relationshipsView = new RefreshingView("relationships", new RefPropertyModel(getElement(), "relationships")) {
            protected Iterator getItemModels() {
                List<Relationship> relationships = (List<Relationship>) getModelObject();
                return new ModelIteratorAdapter(relationships.iterator()) {
                    protected IModel model(Object relationship) {
                        return new Model((Relationship)relationship);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Relationship relationship = (Relationship) item.getModelObject();
                final Ref withResource = relationship.getWithResource();
                AjaxLink relationshipResourceLink = new AjaxLink("relationshipResourceLink") {
                    public void onClick(AjaxRequestTarget target) {
                       edit(withResource, target);
                    }
                };
                // with tooltipped named resource
                Label relationshipResourceNameLabel = new Label("relationshipResourceName", new RefPropertyModel(relationship, "withResource.name"));
                relationshipResourceLink.add(relationshipResourceNameLabel);
                Ref relationshipType = relationship.getRelationshipType();
                // List<Ref> relationshipTypes = getProject().findAllApplicableRelationshipTypes(getElement(), withResource);
                final DropDownChoice relationshipTypesChoice = new DropDownChoice("relationshipType");  // DojoHtmlSuggestionList buggy: no event after Ajax redisplay
                relationshipTypesChoice.setModel(new Model(relationshipType));
                relationshipTypesChoice.setChoices(new RefQueryModel(getProject(), new Query("findAllApplicableRelationshipTypes", getElement(), withResource)));
                relationshipTypesChoice.setChoiceRenderer(new RefChoiceRenderer("name", "id"));
                relationshipTypesChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        Ref selectedRelationshipType = (Ref)relationshipTypesChoice.getModelObject(); // db is default
                        relationship.setRelationshipType(selectedRelationshipType);
                }
                });
                AjaxLink removeRelationshipLink = new AjaxLink("removeRelationship") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(getElement(), "relationships", relationship);
                        target.addComponent(relationshipsDiv);
                    }
                };
                item.add(relationshipTypesChoice);
                item.add(relationshipResourceLink);
                item.add(removeRelationshipLink);
            }
        };
        relationshipsDiv.add(relationshipsView);
        addReplaceable(relationshipsDiv);
    }

    private void loadAgreements() {
        // Agreements
        agreementsDiv = new WebMarkupContainer("agreementsDiv");
        agreementsView = new RefreshingView("agreements", new Model()) {
            protected Iterator getItemModels() {
                List<Ref> list = ((Resource)getElement().deref()).allAgreements();
                return new ModelIteratorAdapter(list.iterator()) {
                    protected IModel model(Object agreement) {
                        return new RefModel(agreement);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Ref agreement = (Ref) item.getModelObject();
               // with named resource
                AjaxLink agreementLink = new AjaxLink("agreementLink") {
                    public void onClick(AjaxRequestTarget target) {
                        edit(agreement, target);
                    }
                };
                String agreementString = agreement.deref().toString();
                Label agreementLabel = new Label("agreement", new Model(agreementString));
                agreementLink.add(agreementLabel);
                AjaxLink removeAgreementLink = new AjaxLink("removeAgreement") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(getProject(), "agreements", agreement);
                        target.addComponent(agreementsDiv);
                    }
                };
                item.add(agreementLink);
                item.add(removeAgreementLink);
            }
        };
        agreementsDiv.add(agreementsView);
        addReplaceable(agreementsDiv);
    }
}
