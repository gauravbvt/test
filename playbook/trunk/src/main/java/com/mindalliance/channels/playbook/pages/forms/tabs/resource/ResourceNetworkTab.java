package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.ifm.resources.Relationship;
import com.mindalliance.channels.playbook.ifm.resources.Resource;
import com.mindalliance.channels.playbook.ifm.project.Agreement;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 24, 2008
 * Time: 9:45:02 AM
 */
public class ResourceNetworkTab extends AbstractFormTab {

    protected WebMarkupContainer relationshipsDiv;
    protected RefreshingView relationshipsView;
    protected ListChoice resourcesForRelationshipsList;
    protected Button addRelationshipButton;
    protected WebMarkupContainer agreementsDiv;
    protected RefreshingView agreementsView;
    protected ListChoice resourcesForAgreementsList;
    protected Button addAgreementButton;

    public ResourceNetworkTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        loadRelationships();
        loadAgreements();
    }
     private void loadRelationships() {
        // Relationships
        relationshipsDiv = new WebMarkupContainer("relationshipsDiv");
        relationshipsView = new RefreshingView("relationships", new RefPropertyModel(element, "relationships")) {
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
                List<Ref> relationshipTypes = project.findAllApplicableRelationshipTypes(element, withResource);
                final DropDownChoice relationshipTypesChoice = new DropDownChoice("relationshipType");  // DojoHtmlSuggestionList buggy: no event after Ajax redisplay
                relationshipTypesChoice.setModel(new Model(relationshipType));
                relationshipTypesChoice.setChoices(relationshipTypes);
                relationshipTypesChoice.setChoiceRenderer(new RefChoiceRenderer("name", "id"));
                relationshipTypesChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        Ref selectedRelationshipType = (Ref)relationshipTypesChoice.getModelObject(); // db is default
                        relationship.setRelationshipType(selectedRelationshipType);
                }
                });
                AjaxLink removeRelationshipLink = new AjaxLink("removeRelationship") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(element, "relationships", relationship);
                        target.addComponent(relationshipsDiv);
                    }
                };
                item.add(relationshipTypesChoice);
                item.add(relationshipResourceLink);
                item.add(removeRelationshipLink);
            }
        };
        // All resources
        List<Ref> allResources = project.allResourcesExcept(element);  // TODO - replace with filter tree
        resourcesForRelationshipsList = new ListChoice("resourcesForRelationships", new Model(), allResources, new RefChoiceRenderer("name", "id"));
        resourcesForRelationshipsList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                Ref selectedResource = (Ref)resourcesForRelationshipsList.getModelObject();
                addRelationshipButton.setEnabled(selectedResource != null);
                target.addComponent(addRelationshipButton);
            }
        });
        // Add relationship button
        addRelationshipButton = new Button("addRelationship");
        addRelationshipButton.setEnabled(false);
        addRelationshipButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                Relationship newRelationship = new Relationship();
                Ref selectedResource = (Ref)resourcesForRelationshipsList.getModelObject();
                newRelationship.setWithResource(selectedResource);
                RefUtils.add(element, "relationships", newRelationship);
                target.addComponent(relationshipsDiv);
            }
        });
        relationshipsDiv.add(relationshipsView);
        addReplaceable(relationshipsDiv);
        addReplaceable(addRelationshipButton);
        addReplaceable(resourcesForRelationshipsList);
    }

    private void loadAgreements() {
        // Agreements
        agreementsDiv = new WebMarkupContainer("agreementsDiv");
        agreementsView = new RefreshingView("agreements", new Model()) {
            protected Iterator getItemModels() {
                List<Ref> list = ((Resource)element.deref()).allAgreements();
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
                String s = agreement.deref().toString();
                String resourceName = (String)RefUtils.get(agreement, "toResource.name");
                Label agreementLabel = new Label("agreement", new Model("Will " + s + " " + resourceName));
                agreementLink.add(agreementLabel);
                AjaxLink removeAgreementLink = new AjaxLink("removeAgreement") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(project, "agreements", agreement);
                        target.addComponent(agreementsDiv);
                    }
                };
                item.add(agreementLink);
                item.add(removeAgreementLink);
            }
        };
        // All resources
        List<Ref> allResources = project.allResourcesExcept(element);  // TODO - replace with filter tree
        resourcesForAgreementsList = new ListChoice("resourcesForAgreements", new Model(), allResources, new RefChoiceRenderer("name", "id"));
        resourcesForAgreementsList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                Ref selectedResource = (Ref)resourcesForAgreementsList.getModelObject();
                addAgreementButton.setEnabled(selectedResource != null);
                target.addComponent(addAgreementButton);
            }
        });
        // Add agreement button
        addAgreementButton = new Button("addAgreement");
        addAgreementButton.setEnabled(false);
        addAgreementButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                Ref newAgreement = new Agreement().persist();
                Ref selectedResource = (Ref)resourcesForAgreementsList.getModelObject();
                RefUtils.set(newAgreement, "fromResource", element);
                RefUtils.set(newAgreement, "toResource", selectedResource);
                RefUtils.add(project, "agreements", newAgreement);
                target.addComponent(agreementsDiv);
            }
        });
        agreementsDiv.add(agreementsView);
        addReplaceable(agreementsDiv);
        addReplaceable(addAgreementButton);
        addReplaceable(resourcesForAgreementsList);
    }
}
