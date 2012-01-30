package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.definition.*;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;

import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 30, 2008
 * Time: 3:46:21 PM
 */
public class ResourceDefinitionPanel extends AbstractDefinitionPanel {

    protected ResourceDefinition resourceDefinition;
    protected AjaxCheckBox anyTypeCheckBox;
    protected WebMarkupContainer typesDiv;
    protected DropDownChoice<String> typeChoice;
    protected AjaxCheckBox anyOrganizationCheckBox;
    protected WebMarkupContainer organizationSpecificationDiv;
    protected OrganizationSpecificationPanel organizationSpecificationPanel;
    protected AjaxCheckBox anyLocationCheckBox;
    protected WebMarkupContainer locationDefinitionDiv;
    protected LocationDefinitionPanel locationDefinitionPanel;
    protected AjaxCheckBox anyRelationshipCheckBox;
    protected WebMarkupContainer relationshipDefinitionsDiv;
    protected ListChoice relationshipDefinitionsChoice;
    protected Button addRelationshipDefinitionButton;
    protected Button removeRelationshipDefinitionButton;
    protected WebMarkupContainer relationshipDefinitionDiv;
    protected Component relationshipDefinitionPanel;
    protected RelationshipDefinition selectedRelationshipDefinition;

    public ResourceDefinitionPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        resourceDefinition = (ResourceDefinition)getComponent();
        anyTypeCheckBox = new AjaxCheckBox("anyType", new Model<Boolean>(resourceDefinition.getType().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyType = anyTypeCheckBox.getModelObject();
                if (anyType) {
                    setProperty("type", "", target);
                }
                setVisibility(typesDiv, !anyType, target);
            }
        };
        addReplaceable(anyTypeCheckBox);
        typesDiv = new WebMarkupContainer("typesDiv");
        setVisibility(typesDiv, !resourceDefinition.getType().isEmpty());
        addReplaceable(typesDiv);
        typeChoice = new DropDownChoice<String>(
                "types", new Model<String>(
                resourceDefinition.getType() ), ResourceDefinition.typeChoices() );
        typeChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        String newType =
                                typeChoice.getDefaultModelObjectAsString();
                        setProperty("type", newType, target);
                    }
                } );
        addReplaceableTo(typeChoice, typesDiv);
        anyOrganizationCheckBox = new AjaxCheckBox("anyOrganization", new Model<Boolean>(resourceDefinition.getOrganizationSpec().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyOrganization = (Boolean)anyOrganizationCheckBox.getModelObject();
                if (anyOrganization) {
                    setProperty("organizationSpec", new OrganizationSpecification(), target);
                    organizationSpecificationPanel = new OrganizationSpecificationPanel("organizationSpecification", ResourceDefinitionPanel.this, propPath+".organizationSpec");
                    addReplaceableTo(organizationSpecificationPanel, organizationSpecificationDiv);
                }
                setVisibility(organizationSpecificationDiv, !anyOrganization, target);
            }
        };
        addReplaceable(anyOrganizationCheckBox);
        organizationSpecificationDiv = new WebMarkupContainer("organizationSpecificationDiv");
        setVisibility(organizationSpecificationDiv, !resourceDefinition.getOrganizationSpec().matchesAll());
        addReplaceable(organizationSpecificationDiv);
        organizationSpecificationPanel = new OrganizationSpecificationPanel("organizationSpecification", this, propPath+".organizationSpec");
        addReplaceableTo(organizationSpecificationPanel, organizationSpecificationDiv);
        anyLocationCheckBox = new AjaxCheckBox("anyLocation", new Model<Boolean>(resourceDefinition.getLocationDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = (Boolean)anyLocationCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("locationDefinition", new LocationDefinition(), target);
                    locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", ResourceDefinitionPanel.this, propPath+".locationDefinition");
                    addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
                }
                setVisibility(locationDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyLocationCheckBox);
        locationDefinitionDiv = new WebMarkupContainer("locationDefinitionDiv");
        setVisibility(locationDefinitionDiv, !resourceDefinition.getLocationDefinition().matchesAll());
        addReplaceable(locationDefinitionDiv);
        locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", this, propPath+".locationDefinition");
        addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
        anyRelationshipCheckBox = new AjaxCheckBox("anyRelationship", new Model<Boolean>(resourceDefinition.getRelationshipDefinitions().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyRelationships = (Boolean)anyRelationshipCheckBox.getModelObject();
                if (anyRelationships) {
                    setProperty("relationshipDefinitions", new ArrayList<RelationshipDefinition>(), target);
                }
                setVisibility(relationshipDefinitionsDiv, !anyRelationships, target);
            }
        };
        addReplaceable(anyRelationshipCheckBox);
        relationshipDefinitionsDiv = new WebMarkupContainer("relationshipDefinitionsDiv");
        setVisibility(relationshipDefinitionsDiv, !resourceDefinition.getRelationshipDefinitions().isEmpty());
        addReplaceable(relationshipDefinitionsDiv);
        relationshipDefinitionsChoice = new ListChoice("relationshipDefinitions",
                                                        new Model(selectedRelationshipDefinition),
                                                        new RefPropertyModel(getComponent(), "relationshipDefinitions"),
                                                        new ChoiceRenderer("summary"));
        relationshipDefinitionsChoice.setMaxRows(MAX_CHOICE_ROWS);
        relationshipDefinitionsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                selectedRelationshipDefinition = (RelationshipDefinition)relationshipDefinitionsChoice.getModelObject();
                enable(removeRelationshipDefinitionButton, selectedRelationshipDefinition != null, target);
                updateRelationshipDefinitionPanel();
                setVisibility(relationshipDefinitionDiv, selectedRelationshipDefinition != null, target);
            }
        });
        addReplaceableTo(relationshipDefinitionsChoice, relationshipDefinitionsDiv);
        addRelationshipDefinitionButton = new Button("addRelationshipDefinition");
        addRelationshipDefinitionButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                selectedRelationshipDefinition = new RelationshipDefinition();
                RefUtils.add(getElement(), propPath+".relationshipDefinitions", selectedRelationshipDefinition);
                enable(removeRelationshipDefinitionButton, true, target);
                updateRelationshipDefinitionPanel();
                setVisibility(relationshipDefinitionDiv, true, target);
            }
        });
        addReplaceableTo(addRelationshipDefinitionButton, relationshipDefinitionsDiv);
        removeRelationshipDefinitionButton = new Button("deleteRelationshipDefinition");
        removeRelationshipDefinitionButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                RefUtils.remove(getElement(), propPath+".relationshipDefinitions", selectedRelationshipDefinition);
                selectedRelationshipDefinition = null;
                target.addComponent(relationshipDefinitionsChoice);
                enable(removeRelationshipDefinitionButton, false, target);
                setVisibility(relationshipDefinitionDiv, false, target);
            }
        });
        removeRelationshipDefinitionButton.setEnabled(false);
        addReplaceableTo(removeRelationshipDefinitionButton, relationshipDefinitionsDiv);
        relationshipDefinitionDiv = new WebMarkupContainer("relationshipDefinitionDiv");
        hide(relationshipDefinitionDiv);
        addReplaceableTo(relationshipDefinitionDiv, relationshipDefinitionsDiv);
        relationshipDefinitionPanel = new Label("relationshipDefinition", new Model("dummy"));
        addReplaceableTo(relationshipDefinitionPanel, relationshipDefinitionDiv);
    }

    private void updateRelationshipDefinitionPanel() {
        relationshipDefinitionDiv.remove(relationshipDefinitionPanel);
        if (selectedRelationshipDefinition != null) {
            int index = resourceDefinition.getRelationshipDefinitions().indexOf(selectedRelationshipDefinition);
            relationshipDefinitionPanel = new RelationshipDefinitionPanel("relationshipDefinition", this, propPath+".relationshipDefinitions["+index+"]");
        }
        else {
           relationshipDefinitionPanel = new Label("relationshipDefinition", new Model("dummy"));
        }
        addReplaceableTo(relationshipDefinitionPanel, relationshipDefinitionDiv);
    }

}
